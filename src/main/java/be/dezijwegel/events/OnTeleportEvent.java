package be.dezijwegel.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        if ( player.isSleeping() && teleport.getFrom().distance( teleport.getTo() ) > 10)
        {
            Block bed = teleport.getFrom().getBlock();
            try {
                if (Bukkit.getVersion().contains("1.12"))
                {
                    Bukkit.getConsoleSender().sendMessage("[BetterSleeping] " + ChatColor.RED + "One of your players (" + player.getName() + ChatColor.RED + ") may be trying to exploit a glitch within BetterSleeping! In versions beyond 1.12, this bug cannot be exploited anymore.");
                    return;
                }

                PlayerBedLeaveEvent newEvent = new PlayerBedLeaveEvent(player, bed, false);
                Bukkit.getPluginManager().callEvent( newEvent );

            } catch (Exception e) {}
        }
    }

}
