package be.dezijwegel;

import be.dezijwegel.events.handlers.BedEventHandler;
import be.dezijwegel.interfaces.Reloadable;
import be.dezijwegel.messenger.PlayerMessenger;
import be.dezijwegel.sleepersneeded.PercentageNeeded;
import be.dezijwegel.timechange.TimeChanger;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

/**
 *
 * @author Dieter Nuytemans
 */
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
        PlayerMessenger messenger = new PlayerMessenger(new HashMap<>());
        BedEventHandler beh = new BedEventHandler(this, messenger, TimeChanger.TimeChangeType.SMOOTH, new PercentageNeeded(30));
        getServer().getPluginManager().registerEvents(beh, this);

        //this.getCommand("bettersleeping").setExecutor(commandHandler);
        //this.getCommand("bettersleeping").setTabCompleter(new TabCompletion(onSleepEvent.getSleepTracker()));
    }



    private static class UpdateChecker extends Thread {

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
//                if (updateVersion.equals(currentVersion)) {
//                    ConsoleLogger.logInfo("You are using the latest version: " + currentVersion);
//                } else {
//                    ConsoleLogger.logNegative("Update detected! You are using version " + currentVersion + " and the latest version is " + updateVersion + "! Download it at https://www.spigotmc.org/resources/bettersleeping-1-12-1-15.60837/", ChatColor.RED);
//                }
            } catch (IOException | NullPointerException e) {
                Bukkit.getLogger().info("[BetterSleeping] An error occurred while retrieving the latest version!");
            }
        }

    }

}
