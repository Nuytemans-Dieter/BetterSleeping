package be.dezijwegel.events;

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
    
    private int playersNeeded;
    private int playersSleeping;
    private FileManagement configFile;
    private FileManagement langFile;
    
    private String prefix;
    private String enough_sleeping;
    private String amount_left;
    
    public OnSleepEvent(FileManagement configFile, FileManagement langFile)
    {
        this.configFile = configFile;
        this.langFile = langFile;
        
        this.playersSleeping = 0;
        playersNeeded = configFile.getInt("percentage_needed");
        
        prefix = langFile.getString("prefix");
        enough_sleeping = langFile.getString("enough_sleeping");
        amount_left = langFile.getString("amount_left");
    }
    
    @EventHandler
    public void onPlayerEnterBed(PlayerBedEnterEvent e)
    {
        playersSleeping++;
        int numNeeded = playersNeeded();
        
        if (playersSleeping >= numNeeded)
        {
            for(World world : Bukkit.getWorlds()) { 
                    world.setStorm(false);
                    world.setTime(1000);
            }
            
            for (Player p : Bukkit.getOnlinePlayers()) 
            {
                p.sendMessage(prefix + enough_sleeping);
            }
        }
        
        if (playersSleeping < numNeeded)
        {
            int numLeft = numNeeded - playersSleeping;
            if (numLeft >0 ) {
                String msg;
                if (amount_left.contains("<amount>")) 
                    msg = amount_left.replaceAll("<amount>", Integer.toString(numLeft));
                else
                    msg = amount_left;
                e.getPlayer().sendMessage(prefix + msg);
            }
        }
    }
    
    @EventHandler
    public void onPlayerLeaveBed(PlayerBedLeaveEvent e)
    {
        playersSleeping--;
        /*
        int numLeft = playersNeeded() - playersSleeping;
        
        for (Player p : Bukkit.getOnlinePlayers())
        {
            if (numLeft > 0 ) {
                String msg;
                if (amount_left.contains("<amount>")) 
                    msg = amount_left.replaceAll("<amount>", Integer.toString(numLeft));
                else msg = amount_left;
                e.getPlayer().sendMessage(prefix + msg);
            }
        }
    */
    }
    
    public int playersNeeded()
    {
        int numOnline = Bukkit.getOnlinePlayers().size();
        return (playersNeeded * numOnline / 100);
    }

    @Override
    public void reload() {
        playersNeeded = configFile.getInt("percentage_needed");
        prefix = langFile.getString("prefix");
        enough_sleeping = langFile.getString("enough_sleeping");
        amount_left = langFile.getString("amount_left");
    }
}
