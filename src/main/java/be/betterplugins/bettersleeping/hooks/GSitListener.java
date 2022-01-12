package be.betterplugins.bettersleeping.hooks;

import be.betterplugins.bettersleeping.model.sleeping.SleepWorldManager;
import be.betterplugins.bettersleeping.util.TimeUtil;
import com.google.inject.Inject;
import dev.geco.gsit.api.event.PlayerGetUpPoseEvent;
import dev.geco.gsit.api.event.PlayerPoseEvent;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class GSitListener implements Listener {

    private final SleepWorldManager sleepWorldManager;

    @Inject
    public GSitListener( SleepWorldManager sleepWorldManager )
    {
        this.sleepWorldManager = sleepWorldManager;
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerLay (PlayerPoseEvent layOrCrawlEvent)
    {
        Player player = layOrCrawlEvent.getPlayer();
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

        if (layOrCrawlEvent.getPoseSeat().getPose() == Pose.SLEEPING)
        {
            sleepWorldManager.addSleeper(player);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerGetUp(PlayerGetUpPoseEvent playerGetUpFromCrawlOrLayEvent)
    {
        Player player = playerGetUpFromCrawlOrLayEvent.getPlayer();

        // Don't handle disabled worlds
        if ( ! sleepWorldManager.isWorldEnabled( player.getWorld() ))
        {
            return;
        }

        this.sleepWorldManager.removeSleeper( player );
    }
}
