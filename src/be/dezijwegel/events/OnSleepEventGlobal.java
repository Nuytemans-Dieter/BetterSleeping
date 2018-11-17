/*
 * Geen license header toegevoegd
 * Dieter Nuytemans
 */
package be.dezijwegel.events;

import be.dezijwegel.bettersleeping.BetterSleeping;
import be.dezijwegel.files.FileManagement;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

/**
 *
 * @author Dieter Nuytemans
 */
public class OnSleepEventGlobal extends OnSleepEvent implements Listener{
    
    private BetterSleeping plugin;
    
    private int playersSleeping;
    
    public OnSleepEventGlobal(FileManagement configFile, FileManagement langFile, BetterSleeping plugin)
    {
        super(configFile, langFile);
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerEnterBed(PlayerBedEnterEvent e)
    {
        playersSleeping++;
        float numNeeded = playersNeeded();
        
        if (playersSleeping >= numNeeded)
        {
            for (Player p : Bukkit.getOnlinePlayers())
            {
                p.sendMessage(prefix + enough_sleeping);
            }
            
            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (playersSleeping >= numNeeded) {
                    for(World world : Bukkit.getWorlds()) {
                        world.setStorm(false);
                        world.setTime(1000);
                    }
                    
                    for (Player p : Bukkit.getOnlinePlayers())
                    {
                        p.sendMessage(prefix + good_morning);
                    }
                } else {
                    for (Player p : Bukkit.getOnlinePlayers())
                    {
                        p.sendMessage(prefix + cancelled);
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
}
