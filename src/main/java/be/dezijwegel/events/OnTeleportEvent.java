package be.dezijwegel.events;

import be.dezijwegel.BetterSleeping;
import be.dezijwegel.util.ConsoleLogger;
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
        final int max_distance = 10;

        Player player = teleport.getPlayer();
        boolean sameWorld = teleport.getFrom().equals( teleport.getTo() );
        double distance = 0;
        if (sameWorld)
            distance = teleport.getFrom().distance( teleport.getTo() );
        else distance = max_distance + 1;

        if ( player.isSleeping() && distance > max_distance)
        {
            Block bed = teleport.getFrom().getBlock();
            try {
                if (Bukkit.getVersion().contains("1.12"))
                {
                    ConsoleLogger.logNegative("One of your players (" + player.getName() + (ConsoleLogger.isNegativeColored() ? ChatColor.RED : "" ) + ") may be trying to exploit a glitch within BetterSleeping! In versions beyond 1.12, this bug cannot be exploited anymore.", ChatColor.RED);
                    return;
                }

                player.teleport( teleport.getFrom() );
                PlayerBedLeaveEvent newEvent = new PlayerBedLeaveEvent(player, bed, false);
                Bukkit.getPluginManager().callEvent( newEvent );

            } catch (Exception e) {}
        }

        if (BetterSleeping.debug)
            Bukkit.getLogger().info("Sleeping: " + player.isSleeping() + ", dist: " + distance + "/" + max_distance);
    }

}
