package be.dezijwegel.events;

import be.dezijwegel.bettersleeping.BetterSleeping;
import be.dezijwegel.bettersleeping.Reloadable;
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
public class OnSleepEvent implements Listener, Reloadable {
    
    private BetterSleeping plugin;
    
    private int playersNeeded;
    private int playersSleeping;
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
        
        this.playersSleeping = 0;
        playersNeeded = configFile.getInt("percentage_needed");
        if (playersNeeded > 100) playersNeeded = 100;
        else if (playersNeeded < 1) playersNeeded = 1;
        
        prefix = langFile.getString("prefix");
        enough_sleeping = langFile.getString("enough_sleeping");
        amount_left = langFile.getString("amount_left");
    }
    
    @EventHandler
    public void onPlayerEnterBed(PlayerBedEnterEvent e)
    {
        playersSleeping++;
        float numNeeded = playersNeeded();
        
        if (playersSleeping >= numNeeded)
        {
            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                for(World world : Bukkit.getWorlds()) {
                    world.setStorm(false);
                    world.setTime(1000);
                }
                
                for (Player p : Bukkit.getOnlinePlayers())
                {
                    p.sendMessage(prefix + enough_sleeping);
                }
            }, 40L);
                    
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
        playersNeeded = configFile.getInt("percentage_needed");
        if (playersNeeded > 100) playersNeeded = 100;
        else if (playersNeeded < 1) playersNeeded = 1;
        
        prefix = langFile.getString("prefix");
        enough_sleeping = langFile.getString("enough_sleeping");
        amount_left = langFile.getString("amount_left");
    }
}
