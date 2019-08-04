package be.dezijwegel.events;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class OnTeleportEvent implements Listener {

    private SleepTracker sleepTracker;

    public OnTeleportEvent(SleepTracker sleepTracker)
    {
        this.sleepTracker = sleepTracker;
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onTeleportEvent(PlayerTeleportEvent teleport)
    {
        Player player = teleport.getPlayer();
        if ( player.isSleeping() )
        {
            Block bed = teleport.getFrom().getBlock();
            try {
                PlayerBedLeaveEvent newEvent = new PlayerBedLeaveEvent(player, bed, false);
                Bukkit.getPluginManager().callEvent( newEvent );
            } catch (Exception e) {}
        }
    }

}
