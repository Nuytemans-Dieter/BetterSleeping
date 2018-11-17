package be.dezijwegel.events;

import be.dezijwegel.bettersleeping.Reloadable;
import be.dezijwegel.files.FileManagement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.Listener;


/**
 *
 * @author Dieter Nuytemans
 */
public class OnSleepEvent implements Listener, Reloadable {
    
    private FileManagement configFile;
    private FileManagement langFile;
    
    //Can be accessed in subclasses
    int playersNeeded;
    long sleepDelay;
    String prefix;
    String enough_sleeping;
    String amount_left;
    String good_morning;
    String cancelled;
    
    public OnSleepEvent(FileManagement configFile, FileManagement langFile)
    {
        this.configFile = configFile;
        this.langFile = langFile;
        
        //reload();
    }
    
    /**
     * Reload all config settings from the confg files into this object
     */
    @Override
    public void reload() {
        if (!configFile.containsIgnoreDefault("sleep_delay") || !configFile.containsIgnoreDefault("world_specific_behavior")) {
            
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            console.sendMessage("[BetterSleeping]" + ChatColor.GREEN + "New configuration option(s) found!");
            console.sendMessage("[BetterSleeping]" + ChatColor.RED + "Resetting the config file..");
            
            sleepDelay = 40;
            configFile.forceDefaultConfig();
        } else {
            sleepDelay = configFile.getLong("sleep_delay");
        }
        
        if (configFile.containsIgnoreDefault("percentage_needed")) {
            
            playersNeeded = configFile.getInt("percentage_needed");
        
            if (playersNeeded > 100) playersNeeded = 100;
            else if (playersNeeded < 1) playersNeeded = 1;
            
        } else playersNeeded = 30;
        
        if (langFile.containsIgnoreDefault("prefix")) {
            prefix = langFile.getString("prefix");
            if (!prefix.toLowerCase().contains("bettersleeping")) {
                ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                console.sendMessage("[BetterSleeping] " + ChatColor.RED + "Please consider keeping the name of this plugin in the prefix.");
                console.sendMessage("[BetterSleeping] " + ChatColor.RED + "You are not obliged to do so but it would be greatly appreciated! :-)");
            }
        } else prefix = "§6[BetterSleeping] §3";
        
        if (langFile.containsIgnoreDefault("enough_sleeping"))
            enough_sleeping = langFile.getString("enough_sleeping");
        else {
            enough_sleeping = "Enough people are sleeping now!";
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
    }
    
    private void reportMissingOption(String option)
    {
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        console.sendMessage("[BetterSleeping] " + ChatColor.YELLOW + "lang.yml is missing \'" + option + "\', please add \'" + option + "\' to lang.yml if you want to customise that message!");
    }
}
