package be.dezijwegel;

import be.dezijwegel.Runnables.DateChecker;
import be.dezijwegel.commands.CommandHandler;
import be.dezijwegel.commands.TabCompletion;
import be.dezijwegel.events.OnPhantomSpawnEvent;
import be.dezijwegel.events.OnSleepEvent;
import be.dezijwegel.events.OnTeleportEvent;
import be.dezijwegel.files.Console;
import be.dezijwegel.files.EventsConfig;
import be.dezijwegel.interfaces.Reloadable;
import be.dezijwegel.management.Management;
import be.dezijwegel.placeholderAPI.BetterSleepingExpansion;
import be.dezijwegel.util.ConsoleLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

/**
 *
 * @author Dieter Nuytemans
 */
public class BetterSleeping extends JavaPlugin implements Reloadable {

    public static boolean debug = false;

    // Events
    private OnSleepEvent onSleepEvent;
    private OnPhantomSpawnEvent onPhantomSpawnEvent;
    private OnTeleportEvent onTeleportEvent;

    private DateChecker dateChecker;

    // Commands
    private CommandHandler commandHandler;

    private LinkedList<Reloadable> reloadables;
    
    @Override
    public void onEnable()
   {
       startPlugin();
   }

    @Override
    public void reload() {

        // Reset where needed
        dateChecker.cancel();
        HandlerList.unregisterAll(this);

        // Restart all
        startPlugin();
    }

    private void startPlugin()
    {
        if (BetterSleeping.debug)
        {
            Bukkit.getLogger().info("-----");
            Bukkit.getLogger().info("Starting BetterSleeping in debugging mode...");
            Bukkit.getLogger().info("-----");
        }
        ConsoleLogger.setConfig(new Console(this));

        Management management = new Management(this);

        onSleepEvent = new OnSleepEvent(management, this);
        onPhantomSpawnEvent = new OnPhantomSpawnEvent(management);
        onTeleportEvent = new OnTeleportEvent( onSleepEvent.getSleepTracker() );
        getServer().getPluginManager().registerEvents(onSleepEvent, this);
        getServer().getPluginManager().registerEvents(onPhantomSpawnEvent, this);
        getServer().getPluginManager().registerEvents(onTeleportEvent, this);

        reloadables = new LinkedList<Reloadable>();
        reloadables.add(this);

        commandHandler = new CommandHandler(reloadables, management, onSleepEvent.getSleepTracker(), this);


        // If PlaceholderAPI is registered
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new BetterSleepingExpansion(this, management, onSleepEvent.getSleepTracker()).register();
            ConsoleLogger.logPositive("Succesfully hooked into PlaceholderAPI!", ChatColor.GREEN);
        }

        try {
            this.getCommand("bettersleeping").setExecutor(commandHandler);
            this.getCommand("bettersleeping").setTabCompleter(new TabCompletion(onSleepEvent.getSleepTracker()));
        } catch(NullPointerException e)
        {
            e.printStackTrace();
        }

        if (management.getBooleanSetting("update_checker")) {
            String version = this.getDescription().getVersion();
            new UpdateChecker( version );
        }



        dateChecker = new DateChecker(this, management);
        // 20x3600 = 72 000 -> Ticks in an hour
        dateChecker.runTaskTimer(this, 0, 72000);
    }

    private class UpdateChecker extends Thread {

        private String currentVersion;

        /**
         * Create an instance of UpdateChecker and perform an update check
         */
        UpdateChecker(String currentVersion)
        {
            this.currentVersion = currentVersion;
            this.start();
        }

        @Override
        public void run()
        {
            URL url = null;
            try {
                url = new URL("https://api.spigotmc.org/legacy/update.php?resource=60837");
            } catch (MalformedURLException e) {
                Bukkit.getLogger().info("[BetterSleeping] An error occurred while retrieving the latest version!");
            }

            URLConnection conn = null;
            try {
                conn = url.openConnection();
            } catch (IOException | NullPointerException e) {
                Bukkit.getLogger().info("[BetterSleeping] An error occurred while retrieving the latest version!");
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String updateVersion = reader.readLine();
                if (updateVersion.equals(currentVersion)) {
                    ConsoleLogger.logInfo("You are using the latest version: " + currentVersion);
                } else {
                    ConsoleLogger.logNegative("Update detected! You are using version " + currentVersion + " and the latest version is " + updateVersion + "! Download it at https://www.spigotmc.org/resources/bettersleeping-1-12-1-15.60837/", ChatColor.RED);
                }
            } catch (IOException | NullPointerException e) {
                Bukkit.getLogger().info("[BetterSleeping] An error occurred while retrieving the latest version!");
            }
        }

    }

}
