package be.dezijwegel.bettersleeping;

import be.dezijwegel.bettersleeping.commands.CommandHandler;
import be.dezijwegel.bettersleeping.events.custom.TimeSetToDayEvent;
import be.dezijwegel.bettersleeping.events.handlers.TimeSetToDayCounter;
import be.dezijwegel.bettersleeping.hooks.PapiExpansion;
import be.dezijwegel.bettersleeping.runnables.NotifyUpdateRunnable;
import be.dezijwegel.bettersleeping.util.*;
import be.dezijwegel.bettersleeping.events.handlers.BedEventHandler;
import be.dezijwegel.bettersleeping.events.handlers.BuffsHandler;
import be.dezijwegel.bettersleeping.events.handlers.PhantomHandler;
import be.dezijwegel.bettersleeping.hooks.EssentialsHook;
import be.dezijwegel.bettersleeping.interfaces.Reloadable;
import be.dezijwegel.bettersleeping.interfaces.SleepersNeededCalculator;
import be.dezijwegel.bettersleeping.messaging.Messenger;
import be.dezijwegel.bettersleeping.permissions.BypassChecker;
import be.dezijwegel.bettersleeping.runnables.SleepersRunnable;
import be.dezijwegel.bettersleeping.sleepersneeded.AbsoluteNeeded;
import be.dezijwegel.bettersleeping.sleepersneeded.PercentageNeeded;
import be.dezijwegel.bettersleeping.timechange.TimeChanger;
import be.dezijwegel.bettersleeping.timechange.TimeSetter;
import be.dezijwegel.bettersleeping.timechange.TimeSmooth;
import be.dezijwegel.bettersleeping.messaging.ConsoleLogger;
import be.dezijwegel.bettersleeping.messaging.ScreenMessenger;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;


public class BetterSleeping extends JavaPlugin implements Reloadable {

    BedEventHandler bedEventHandler;
    private UpdateChecker updateChecker;

    @Override
    public void onEnable()
   {
       startPlugin();
   }


    @Override
    public void reload() {

        // Cancels all internal Runnables
        bedEventHandler.reload();
        updateChecker.stopReminder();

        // Reset where needed: prevent events being handled twice
        HandlerList.unregisterAll(this);

        // Restart all
        startPlugin();
    }


    private void startPlugin()
    {
        ConsoleLogger logger = new ConsoleLogger(true);

        // Handle multiworld support
        List<String> multiworldPlugins = new ArrayList<String>() {{
            add("Multiverse-Core");
            add("MultiWorld");
            add("BentoBox");
            add("Skyblock");
        }};

        boolean isMultiWorldServer;
        int index = 0;
        do
        {
            isMultiWorldServer = Bukkit.getServer().getPluginManager().getPlugin( multiworldPlugins.get(index) ) != null;
            index++;
        } while(!isMultiWorldServer && index < multiworldPlugins.size());

        if (isMultiWorldServer)
        {
            String mwPlugin = multiworldPlugins.get(index-1);
            logger.log("Detected " + mwPlugin +". Delayed detection of worlds until " + mwPlugin + " loaded them into memory.");
        }

        // Load configuration

        ConfigLib config = new ConfigLib("config.yml", this);
        FileConfiguration fileConfig = config.getConfiguration();
        boolean autoAddOptions  = fileConfig.getBoolean("auto_add_missing_options");
        boolean disablePhantoms = fileConfig.getBoolean("disable_phantoms");
        boolean checkUpdate     = fileConfig.getBoolean("update_notifier");
        String  localised       = fileConfig.getString("language");

        ConfigLib sleeping = new ConfigLib("sleeping_settings.yml", this, autoAddOptions, "disabled_worlds");
        ConfigLib hooks    = new ConfigLib("hooks.yml",             this, autoAddOptions);
        ConfigLib bypassing= new ConfigLib("bypassing.yml",         this, autoAddOptions);


        // Handle configuration

        if (disablePhantoms)
            getServer().getPluginManager().registerEvents(new PhantomHandler(), this);

        // Get the correct lang file
        
        ConfigLib lang;
        localised = localised == null ? "en-us" : localised;
        String filename = "lang/" + localised.toLowerCase() + ".yml";
        // Check if the internal file exists (slash must be added or the resource will not be found!)
        URL internalLangFile = BetterSleeping.class.getResource("/" + filename);
        if (internalLangFile != null)
        {
            logger.log("Using localised lang file for: " + localised);
            lang = new ConfigLib(filename, this, autoAddOptions);
        }
        else
        {
            logger.log("Localised lang file not found! Please make sure " + localised + " exists. Defaulting to en-US...", ChatColor.DARK_RED);
            lang = new ConfigLib("lang/en-us.yml", this);
        }

        // Get the time skip mode

        TimeChanger.TimeChangeType timeChangerType;
        String mode = sleeping.getConfiguration().getString("mode");
        if (mode != null && mode.equalsIgnoreCase("setter"))
            timeChangerType = TimeChanger.TimeChangeType.SETTER;
        else
            timeChangerType = TimeChanger.TimeChangeType.SMOOTH;
        logger.log("Using '" + timeChangerType.toString().toLowerCase() + "' as night skip mode");


        // Read hooks settings

        FileConfiguration hooksConfig = hooks.getConfiguration();
        EssentialsHook essentialsHook = new EssentialsHook( hooksConfig.getBoolean("essentials_afk_ignored"),
                                                            hooksConfig.getBoolean("vanished_ignored"),
                                                            hooksConfig.getInt("minimum_afk_time"));


        // Read bypass settings

        FileConfiguration bypassConfig = bypassing.getConfiguration();
        boolean enableBypass = bypassConfig.getBoolean("enable_bypass_permissions");
        List<GameMode> bypassedGamemodes = new ArrayList<>();
        for (String path : bypassConfig.getKeys(false))
        {
            if (path.contains("ignore_") && bypassConfig.getBoolean(path))
            {
                path = path.replace("ignore_", "");
                try
                {
                    GameMode gm = GameMode.valueOf(path.toUpperCase());
                    bypassedGamemodes.add( gm );
                } catch(IllegalArgumentException e)
                {
                    logger.log("Unknown gamemode in bypassing.yml: " + path);
                }
            }
        }
        BypassChecker bypassChecker = new BypassChecker(enableBypass, essentialsHook, bypassedGamemodes);
        // Read all messages from lang.yml

        Map<String, String> messages = new HashMap<>();
        FileConfiguration langConfig = lang.getConfiguration();
        for (String path : langConfig.getKeys(true))
        {
            if ( ! langConfig.isConfigurationSection(path)) {
                messages.put(path, langConfig.getString(path));
            }
        }
        
        
        // Determine if able and configured to post messages in chat or action bar
        
        Messenger messenger;
        if (fileConfig.getBoolean("action_bar_messages") && SpigotChecker.hasSpigot())
        {
            messenger = new ScreenMessenger(this, messages, bypassChecker, bypassConfig.getBoolean("send_messages"), fileConfig.getBoolean("shorten_prefix"));
            logger.log("Messages will be shown in the action bar");
        }
        else
        {
            messenger = new Messenger(this, messages, bypassChecker, bypassConfig.getBoolean("send_messages"), fileConfig.getBoolean("shorten_prefix"));
            logger.log("Messages will be shown in chat");
        }
        
        
        // Get the num sleeping players needed calculator

        SleepersNeededCalculator calculator;
        String counter = sleeping.getConfiguration().getString("sleeper_counter");
        if (counter != null && counter.equalsIgnoreCase("absolute"))
        {
            int needed = sleeping.getConfiguration().getInt("absolute.needed");
            calculator = new AbsoluteNeeded(needed);
            logger.log("Using required sleepers counter 'absolute' which is set to " + needed + " players required");
        }
        else
        {
            int needed = sleeping.getConfiguration().getInt("percentage.needed");
            calculator = new PercentageNeeded(needed, bypassChecker);
            logger.log("Using required sleepers counter 'percentage' which is set to " + needed + "% of players required");
        }


        // get a runnable for each world

        FileConfiguration sleepConfig = sleeping.getConfiguration();

        int numWorlds = 0;      // The amount of detected worlds
        Map<World, SleepersRunnable> runnables = new HashMap<>();
        Set<String> disabledWorlds = new HashSet<String> (sleepConfig.getStringList("disabled_worlds") );
        for (World world : Bukkit.getWorlds())
        {
            // Only enable worlds where sleeping is possible
            if (world.getEnvironment() == World.Environment.NORMAL)
            {
                // Only enable non-disabled worlds and worlds that have a daylight cycle
                Boolean doDaylightCycle = world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE);
                if (!disabledWorlds.contains(world.getName()) && (doDaylightCycle == null || doDaylightCycle)) {
                    TimeChanger timeChanger;

                    if (timeChangerType == TimeChanger.TimeChangeType.SMOOTH) {
                        int baseSpeedup = sleepConfig.getInt("smooth.base_speedup");
                        int speedupPerPlayer = sleepConfig.getInt("smooth.speedup_per_player");
                        int maxSpeedup = sleepConfig.getInt("smooth.max_speedup");
                        timeChanger = new TimeSmooth(world, baseSpeedup, speedupPerPlayer, maxSpeedup);
                    } else {
                        int sleepDelay = sleepConfig.getInt("setter.delay");
                        timeChanger = new TimeSetter(world, sleepDelay);
                    }
                    SleepersRunnable runnable = new SleepersRunnable(world, messenger, timeChanger, calculator);
                    runnables.put(world, runnable);
                    numWorlds++;
                } else {
                    logger.log("Not enabling BetterSleeping in world '" + world.getName() + "'");
                }
            }
        }
        
        // Test free->premium pipeline

        if (isMultiWorldServer)
            logger.log("Found " + numWorlds + " worlds in memory.");

        // Read buffs config and register event handler
        FileConfiguration buffsConfig = new ConfigLib(false, "buffs.yml", this).getConfiguration();
        BuffsHandler buffsHandler = new BuffsHandler(logger, messenger, bypassChecker, buffsConfig);
        getServer().getPluginManager().registerEvents(buffsHandler, this);

        // Register bed event handler
        bedEventHandler = new BedEventHandler(this, messenger, bypassChecker, essentialsHook, sleepConfig.getInt("bed_enter_delay"), runnables);
        getServer().getPluginManager().registerEvents(bedEventHandler, this);

        logger.log("The message below is always shown, even if collecting data is disabled: ");
        logger.log("BetterSleeping collects anonymous statistics once every 30 minutes. Opt-out at bStats/config.yml");

        // Register Papi Expansion
        if (Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null)
            new PapiExpansion(this, bedEventHandler, buffsHandler).register();

        // Handle update checking
        if (checkUpdate)
        {
            updateChecker = new UpdateChecker(this, this.getDescription().getVersion(), logger, messenger);
            updateChecker.start();
        }

        TimeSetToDayCounter timeSetToDayCounter = new TimeSetToDayCounter();
        getServer().getPluginManager().registerEvents(timeSetToDayCounter, this);

        // bStats handles enabling/disabling metrics collection, no check required
        new BStatsHandler(this, config, sleeping, bypassing, essentialsHook, buffsHandler, timeSetToDayCounter, isMultiWorldServer);

        Objects.requireNonNull(this.getCommand("bettersleeping")).setExecutor(new CommandHandler(this, messenger, buffsHandler, bypassChecker));
    }


    /**
     * Package private on purpose.
     * @return get the BedEventHandler
     */
    public BedEventHandler getBedEventHandler()
    {
        return bedEventHandler;
    }


    private static class UpdateChecker extends Thread {

        private final Plugin plugin;
        private final String currentVersion;
        private final ConsoleLogger logger;
        private final Messenger messenger;
        private BukkitRunnable reminder = null;

        UpdateChecker(Plugin plugin, String currentVersion, ConsoleLogger logger, Messenger messenger)
        {
            this.plugin = plugin;
            this.currentVersion = currentVersion;
            this.logger = logger;
            this.messenger = messenger;
        }

        /**
         * Stop the internal runnable reminder
         */
        public void stopReminder()
        {
            if (reminder != null)
                reminder.cancel();
        }


        @Override
        public void run()
        {
            URL url = null;
            try {
                url = new URL("https://api.spigotmc.org/legacy/update.php?resource=60837");
            } catch (MalformedURLException e) {
                logger.log("An error occurred while retrieving the latest version!", ChatColor.RED);
            }

            URLConnection conn = null;
            try {
                conn = Objects.requireNonNull(url).openConnection();
            } catch (IOException | NullPointerException e) {
                logger.log("An error occurred while retrieving the latest version!");
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(conn).getInputStream()));
                String updateVersion = reader.readLine();

                Version current = new Version(currentVersion);
                Version latest = new Version(updateVersion);

                boolean isVersionCorrect = current.isCorrectFormat() && latest.isCorrectFormat();

                if ( (isVersionCorrect && current.compareTo(latest) == 0 ) || (!isVersionCorrect && updateVersion.equals(currentVersion) )) {
                    logger.log("You are using the latest version: " + currentVersion);
                } else if (!isVersionCorrect || current.compareTo(latest) < 0) {
                    logger.log("Update detected! You are using version " + currentVersion + " and the latest version is " + updateVersion + "! Download it at https://www.spigotmc.org/resources/bettersleeping-1-12-1-15.60837/", ChatColor.RED);
                    if (reminder != null) reminder.cancel();
                    reminder = new NotifyUpdateRunnable(messenger, currentVersion, updateVersion);
                    reminder.runTaskTimer(plugin, 1200, 72000);
                }
            } catch (IOException | NullPointerException e) {
                logger.log("An error occurred while retrieving the latest version!");
            }
        }

    }


}
