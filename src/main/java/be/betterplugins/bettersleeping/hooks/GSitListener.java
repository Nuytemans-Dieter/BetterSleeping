package be.betterplugins.bettersleeping.hooks;

import be.betterplugins.bettersleeping.model.sleeping.SleepWorldManager;
import be.betterplugins.bettersleeping.util.TimeUtil;
import me.gsit.api.events.PlayerGetUpLayEvent;
import me.gsit.api.events.PlayerLayEvent;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class GSitListener implements Listener {

    private final SleepWorldManager sleepWorldManager;

    public GSitListener( SleepWorldManager sleepWorldManager )
    {
        this.sleepWorldManager = sleepWorldManager;
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerLay (PlayerLayEvent playerLayEvent)
    {
        Player player = playerLayEvent.getPlayer();
        World world = player.getWorld();

        // If time for sleeping has not come yet, don't count the event
        if (! TimeUtil.isSleepPossible( world ))
        {
            return;
        }

        // Don't handle disabled worlds
        if ( !sleepWorldManager.isWorldEnabled( world ) )
        {
            return;
        }

        if (playerLayEvent.getPose() == Pose.SLEEPING)
        {
            sleepWorldManager.addSleeper(player);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerGetUp(PlayerGetUpLayEvent playerStandUpLayEvent)
    {
        Player player = playerStandUpLayEvent.getPlayer();

        // Don't handle disabled worlds
        if ( ! sleepWorldManager.isWorldEnabled( player.getWorld() ))
        {
            return;
        }

        this.sleepWorldManager.removeSleeper( player );
    }
}
