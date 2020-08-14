package be.dezijwegel.bettersleeping;

import be.dezijwegel.bettersleeping.commands.CommandHandler;
import be.dezijwegel.bettersleeping.util.ConfigLib;
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
import be.dezijwegel.bettersleeping.util.MetricsHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
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
    public ConfigLib sleeping;

    @Override
    public void onEnable()
   {
       startPlugin();
   }


    @Override
    public void reload() {

        // Cancels all internal Runnables
        bedEventHandler.reload();

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

        sleeping = new ConfigLib("sleeping_settings.yml", this, autoAddOptions);
        ConfigLib hooks    = new ConfigLib("hooks.yml",             this, autoAddOptions);
        ConfigLib bypassing= new ConfigLib("bypassing.yml",         this, autoAddOptions);


        // Handle configuration

        if (disablePhantoms)
            getServer().getPluginManager().registerEvents(new PhantomHandler(), this);

        if (checkUpdate)
            new UpdateChecker(this.getDescription().getVersion(), logger).start();

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


        // Read all messages from lang.yml

        Map<String, String> messages = new HashMap<>();
        FileConfiguration langConfig = lang.getConfiguration();
        for (String path : langConfig.getKeys(true))
        {
            if ( ! langConfig.isConfigurationSection(path))
                messages.put(path, langConfig.getString(path));
        }

        Messenger messenger = new Messenger(messages, fileConfig.getBoolean("shorten_prefix"));


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
        for (World world : Bukkit.getWorlds())
        {
            // Only check on the overworld
            //if (world.getEnvironment() == World.Environment.NORMAL) {

                TimeChanger timeChanger;

                if (timeChangerType == TimeChanger.TimeChangeType.SMOOTH)
                {
                    int baseSpeedup      = sleepConfig.getInt("smooth.base_speedup");
                    int speedupPerPlayer = sleepConfig.getInt("smooth.speedup_per_player");
                    int maxSpeedup       = sleepConfig.getInt("smooth.max_speedup");
                    timeChanger = new TimeSmooth(world, baseSpeedup, speedupPerPlayer, maxSpeedup);
                }
                else
                {
                    int sleepDelay = sleepConfig.getInt("setter.delay");
                    timeChanger = new TimeSetter(world, sleepDelay);
                }
                SleepersRunnable runnable = new SleepersRunnable(world, messenger, timeChanger, calculator);
                runnables.put(world, runnable);
                numWorlds++;
            //}
        }

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

        // bStats handles enabling/disabling metrics collection, no check required
        new MetricsHandler(this, localised, autoAddOptions, essentialsHook, counter, timeChangerType,
                            sleepConfig.getInt("percentage.needed"), sleepConfig.getInt("absolute.needed"),
                            bypassChecker, fileConfig.getBoolean("shorten_prefix"), buffsHandler);

        this.getCommand("bettersleeping").setExecutor(new CommandHandler(this, messenger, buffsHandler, bypassChecker));
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

        private final String currentVersion;
        private final ConsoleLogger logger;


        UpdateChecker(String currentVersion, ConsoleLogger logger)
        {
            this.currentVersion = currentVersion;
            this.logger = logger;
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
                if (updateVersion.equals(currentVersion)) {
                    logger.log("You are using the latest version: " + currentVersion);
                } else {
                    logger.log("Update detected! You are using version " + currentVersion + " and the latest version is " + updateVersion + "! Download it at https://www.spigotmc.org/resources/bettersleeping-1-12-1-15.60837/", ChatColor.RED);
                }
            } catch (IOException | NullPointerException e) {
                logger.log("An error occurred while retrieving the latest version!");
            }
        }

    }


}
