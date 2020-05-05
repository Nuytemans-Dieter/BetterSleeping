package be.dezijwegel;

import be.dezijwegel.configuration.ConfigLib;
import be.dezijwegel.events.handlers.BedEventHandler;
import be.dezijwegel.events.handlers.PhantomHandler;
import be.dezijwegel.hooks.EssentialsHook;
import be.dezijwegel.interfaces.Reloadable;
import be.dezijwegel.interfaces.SleepersNeededCalculator;
import be.dezijwegel.messenger.PlayerMessenger;
import be.dezijwegel.permissions.BypassChecker;
import be.dezijwegel.runnables.SleepersRunnable;
import be.dezijwegel.sleepersneeded.AbsoluteNeeded;
import be.dezijwegel.sleepersneeded.PercentageNeeded;
import be.dezijwegel.timechange.TimeChanger;
import be.dezijwegel.timechange.TimeSetter;
import be.dezijwegel.timechange.TimeSmooth;
import be.dezijwegel.util.ConsoleLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;


public class BetterSleeping extends JavaPlugin implements Reloadable {


    @Override
    public void onEnable()
   {
       startPlugin();
   }


    @Override
    public void reload() {

        // Reset where needed: prevent events being handled twice
        HandlerList.unregisterAll(this);

        // Restart all
        startPlugin();
    }


    private void startPlugin()
    {

        // Load configuration

        ConfigLib config = new ConfigLib("config.yml", this);
        boolean autoAddOptions  = config.getConfiguration().getBoolean("auto_add_missing_options");
        boolean disablePhantoms = config.getConfiguration().getBoolean("disable_phantoms");
        boolean checkUpdate     = config.getConfiguration().getBoolean("update_notifier");
        String  localised       = config.getConfiguration().getString("language");

        ConfigLib sleeping = new ConfigLib("sleeping_settings.yml", this, autoAddOptions);
        ConfigLib hooks    = new ConfigLib("hooks.yml",             this, autoAddOptions);
        ConfigLib bypassing= new ConfigLib("bypassing.yml",         this, autoAddOptions);


        // Handle configuration

        ConsoleLogger logger = new ConsoleLogger(true);

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

        PlayerMessenger messenger = new PlayerMessenger(messages);


        // Get the time skip mode

        TimeChanger.TimeChangeType timeChangerType;
        String mode = sleeping.getConfiguration().getString("mode");
        if (mode != null && mode.equalsIgnoreCase("setter"))
            timeChangerType = TimeChanger.TimeChangeType.SETTER;
        else
            timeChangerType = TimeChanger.TimeChangeType.SMOOTH;
        logger.log("Using '" + timeChangerType.toString().toLowerCase() + "' as night skip mode");


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
            calculator = new PercentageNeeded(needed);
            logger.log("Using required sleepers counter 'percentage' which is set to " + needed + "% of players required");
        }


        // Read hooks settings

        FileConfiguration hooksConfig = hooks.getConfiguration();
        EssentialsHook essentialsHook = new EssentialsHook(hooksConfig.getBoolean("essentials_afk_ignored"), hooksConfig.getBoolean("vanished_ignored"));



        // Read bypass settings

        FileConfiguration bypassConfig = bypassing.getConfiguration();
        boolean enableBypass = bypassConfig.getBoolean("enable_bypass_permissions");
        List<GameMode> bypassedGamemodes = new ArrayList<>();
        for (String path : bypassConfig.getKeys(false))
        {
            if (path.contains("ignore_") && bypassConfig.getBoolean(path))
            {
                path = path.replace("ignore_", "");
                bypassedGamemodes.add( GameMode.valueOf(path) );
            }
        }
        BypassChecker bypassChecker = new BypassChecker(enableBypass, essentialsHook, bypassedGamemodes);


        // get a runnable for each world

        FileConfiguration sleepConfig = sleeping.getConfiguration();

        Map<World, SleepersRunnable> runnables = new HashMap<>();
        for (World world : Bukkit.getWorlds())
        {
            // Only check on the overworld
            if (world.getEnvironment() == World.Environment.NORMAL) {

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
            }
        }

        BedEventHandler beh = new BedEventHandler(this, messenger, bypassChecker, essentialsHook, runnables);
        getServer().getPluginManager().registerEvents(beh, this);

        //this.getCommand("bettersleeping").setExecutor(commandHandler);
        //this.getCommand("bettersleeping").setTabCompleter(new TabCompletion(onSleepEvent.getSleepTracker()));
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
