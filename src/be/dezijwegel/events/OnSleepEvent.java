package be.dezijwegel.events;

import be.dezijwegel.bettersleeping.BetterSleeping;
import be.dezijwegel.bettersleeping.Reloadable;
import be.dezijwegel.files.FileManagement;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

/**
 *
 * @author Dieter Nuytemans
 */
public class OnSleepEvent implements Listener, Reloadable {
    
    private BetterSleeping plugin;
    
    private int playersNeeded;
    private int playersSleeping;
    private long sleepDelay;
    private FileManagement configFile;
    private FileManagement langFile;
    
    private String prefix;
    private String enough_sleeping;
    private String amount_left;
    
    public OnSleepEvent(FileManagement configFile, FileManagement langFile, BetterSleeping plugin)
    {
        this.plugin = plugin;
        
        this.configFile = configFile;
        this.langFile = langFile;
        
        reload();
    }
    
    @EventHandler
    public void onPlayerEnterBed(PlayerBedEnterEvent e)
    {
        playersSleeping++;
        float numNeeded = playersNeeded();
        
        if (playersSleeping >= numNeeded)
        {
            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (playersSleeping >= numNeeded) {
                    for(World world : Bukkit.getWorlds()) {
                        world.setStorm(false);
                        world.setTime(1000);
                    }

                    for (Player p : Bukkit.getOnlinePlayers())
                    {
                        p.sendMessage(prefix + enough_sleeping);
                    }
                }
            }, sleepDelay);
                    
        } else {
            float numLeft = numNeeded - playersSleeping;
            if (numLeft > 0 ) {
                
                String msg = amount_left.replaceAll("<amount>", Integer.toString((int) Math.round(numLeft)));
                
                for (Player p : Bukkit.getOnlinePlayers())
                {
                    p.sendMessage(prefix + msg);
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerLeaveBed(PlayerBedLeaveEvent e)
    {
        playersSleeping--;
    }
    
    /**
     * Calculate the amount of players needed according to the settings and current online players
     * @return float
     */
    public float playersNeeded()
    {
        int numOnline = Bukkit.getOnlinePlayers().size();
        return (playersNeeded * numOnline / 100.0f);
    }

    /**
     * Reload all config settings from the confg files into this object
     */
    @Override
    public void reload() {
        if (configFile.contains("sleep_delay"))
            sleepDelay = configFile.getLong("sleep_delay");
        else {
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            console.sendMessage("[BetterSleeping]" + ChatColor.GREEN + "New configuration option(s) found!");
            console.sendMessage("[BetterSleeping]" + ChatColor.RED + "Resetting the config file..");
            sleepDelay = 40;
            configFile.forceDefaultConfig();
        }
        
        if (configFile.contains("percentage_needed")) {
            
            playersNeeded = configFile.getInt("percentage_needed");
        
            if (playersNeeded > 100) playersNeeded = 100;
            else if (playersNeeded < 1) playersNeeded = 1;
            
        } else playersNeeded = 30;
        
        if (langFile.contains("prefix"))
            prefix = langFile.getString("prefix");
        else prefix = "§6[BetterSleeping] §3";
        
        if (langFile.contains("enough_sleeping"))
            enough_sleeping = langFile.getString("enough_sleeping");
        else enough_sleeping = "Enough people are sleeping now!";
        
        if (langFile.contains("amount_left"))
            amount_left = langFile.getString("amount_left");
        else amount_left = "There are <amount> more people needed to skip the night/storm!";
    }
}
