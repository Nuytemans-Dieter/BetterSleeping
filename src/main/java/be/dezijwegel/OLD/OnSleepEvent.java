package be.dezijwegel.OLD;

import be.dezijwegel.bettersleeping.BetterSleeping;
import be.dezijwegel.bettersleeping.Reloadable;
import be.dezijwegel.OLD.FileManagement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;


/**
 *
 * @author Dieter Nuytemans
 */
public class OnSleepEvent implements Listener, Reloadable {
    
    private FileManagement configFile;
    private FileManagement langFile;
    private BetterSleeping plugin;
    
    private boolean prevBehavior;
    
    private HashMap<UUID, Long> lastSleepList;
    
    //Can be accessed in subclasses
    protected int playersNeeded;
    protected long sleepDelay;
    protected int bedEnterDelay;
    protected String prefix;
    protected String enough_sleeping;
    protected String amount_left;
    protected String good_morning;
    protected String cancelled;
    protected String sleep_spam;
    
    public OnSleepEvent(FileManagement configFile, FileManagement langFile, BetterSleeping plugin)
    {
        this.configFile = configFile;
        this.langFile = langFile;
        this.plugin = plugin;
        
        lastSleepList = new HashMap<>();
        
        prevBehavior = configFile.contains("world_specific_behavior") && configFile.getBoolean("world_specific_behavior");
    }
        
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        lastSleepList.put(e.getPlayer().getUniqueId(), 0L);
    }
    
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e)
    {
        lastSleepList.remove(e.getPlayer().getUniqueId());
    }
    
    public boolean PlayerMaySleep (UUID uuid)
    {
        long currentTime = System.currentTimeMillis() / 1000L;
        
        if (lastSleepList.containsKey(uuid))
        {
            if (currentTime - lastSleepList.get(uuid) > bedEnterDelay)
            {
                lastSleepList.put(uuid, currentTime);
                return true;
            } else {
                return false;
            }
        } else {
            lastSleepList.put(uuid, currentTime);
            return true;
        }
    }

    /**
     * Reload all config settings from the config files into this object
     */
    @Override
    public void reload() {
        //Will check if files exist and if not, the default ones will be generated
        configFile.saveDefaultConfig();
        langFile.saveDefaultConfig();
        configFile.reloadFile();
        langFile.reloadFile();

        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
//
//
//            LOADING CONFIGURATION OPTIONS
//
//

        if (!configFile.containsIgnoreDefault("sleep_delay") || !configFile.containsIgnoreDefault("world_specific_behavior")) {

            console.sendMessage("[BetterSleeping] " + ChatColor.GREEN + "New configuration option(s) found!");
            console.sendMessage("[BetterSleeping] " + ChatColor.RED + "Resetting the config file..");

            sleepDelay = 40;
            configFile.forceDefaultConfig();
        } else {
            sleepDelay = configFile.getLong("sleep_delay");
        }


        if (configFile.containsIgnoreDefault("bed_enter_delay"))
        {
            bedEnterDelay = configFile.getInt("bed_enter_delay");
        } else {
            console.sendMessage("[BetterSleeping] " + ChatColor.RED + "A missing option \'bed_enter_delay\' has been found in config.yml! Now using default value: 10");
            bedEnterDelay = 10;
        }


        if (configFile.containsIgnoreDefault("percentage_needed")) {

            playersNeeded = configFile.getInt("percentage_needed");

            if (playersNeeded > 100) playersNeeded = 100;
            else if (playersNeeded < 1) playersNeeded = 1;

        } else {
            playersNeeded = 30;
            console.sendMessage("[BetterSleeping] " + ChatColor.RED + "A missing option \'playersneeded\' has been found in config.yml! Now using default value: 30");
        }


        if (configFile.containsIgnoreDefault("world_specific_behavior"))
        {
            if (configFile.getBoolean("world_specific_behavior") != prevBehavior){}
                //plugin.reloadBehavior();
        } else {
            console.sendMessage("[BetterSleeping] " + ChatColor.RED + "A missing option \'world_specific_behavior\' has been found in config.yml, the config file might be reset upon restart!");
        }


//
//
//            LOADING LANGUAGE OPTIONS
//
//

        if (langFile.containsIgnoreDefault("prefix")) {
            prefix = langFile.getString("prefix");
            if (!prefix.toLowerCase().contains("bettersleeping")) {
                console.sendMessage("[BetterSleeping] " + ChatColor.RED + "Please consider keeping the name of this plugin in the prefix.");
                console.sendMessage("[BetterSleeping] " + ChatColor.RED + "You are not obliged to do so but it would be greatly appreciated! :-)");
            }
        } else prefix = "§6[BetterSleeping] §3";


        if (langFile.containsIgnoreDefault("enough_sleeping")) {
            enough_sleeping = langFile.getString("enough_sleeping");
        }
        else {
            this.enough_sleeping = "Enough people are sleeping now!";
            reportMissingOption("enough_sleeping");
        }

        if (langFile.containsIgnoreDefault("amount_left"))
            amount_left = langFile.getString("amount_left");
        else {
            amount_left = "There are <amount> more people needed to skip the night/storm!";
            reportMissingOption("amount_left");
        }

        if (langFile.containsIgnoreDefault("good_morning"))
            good_morning = langFile.getString("good_morning");
        else {
            good_morning = "Good morning!";
            reportMissingOption("good_morning");
        }

        if (langFile.containsIgnoreDefault("cancelled")) {
            cancelled = langFile.getString("cancelled");
        } else {
            cancelled = "Someone left their bed so the night won't be skipped!";
            reportMissingOption("cancelled");
        }

        if (langFile.containsIgnoreDefault("sleep_spam")) {
            sleep_spam = langFile.getString("sleep_spam");
        } else {
            sleep_spam = "You have to wait a little longer before you can sleep again!";
            reportMissingOption("sleep_spam");
        }

        console.sendMessage("[BetterSleeping] " + ChatColor.GREEN + "Configuration files reloaded!");

    }
    
    private void reportMissingOption(String option)
    {
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        console.sendMessage("[BetterSleeping] " + ChatColor.YELLOW + "lang.yml is missing \'" + option + "\', please add \'" + option + "\' to lang.yml if you want to customise that message!");
    }
}
