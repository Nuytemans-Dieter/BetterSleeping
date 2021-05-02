package be.dezijwegel.bettersleeping.hooks.events;

import be.dezijwegel.bettersleeping.runnables.SleepersRunnable;
import be.dezijwegel.bettersleeping.util.SleepTimeChecker;
import me.gsit.api.events.PlayerGetUpLayEvent;
import me.gsit.api.events.PlayerLayEvent;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;

public class GSitListener implements Listener {

    private final Map<World, SleepersRunnable> sleepHandlers;

    public GSitListener( Map<World, SleepersRunnable> sleepHandlers )
    {
        this.sleepHandlers = sleepHandlers;
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerLay (PlayerLayEvent playerLayEvent)
    {
        Player player = playerLayEvent.getPlayer();

        // If time for sleeping has not come yet, don't count the event
        if (! SleepTimeChecker.isSleepPossible( player.getWorld() ))
            return;

        // Don't handle disabled worlds
        if ( ! sleepHandlers.containsKey( player.getWorld() ))
            return;

        sleepHandlers.get( player.getWorld() ).playerCustomEnterBed( player );
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerGetUp(PlayerGetUpLayEvent playerStandUpLayEvent)
    {
        Player player = playerStandUpLayEvent.getPlayer();

        // Don't handle disabled worlds
        if ( ! sleepHandlers.containsKey( player.getWorld() ))
            return;

        sleepHandlers.get( player.getWorld() ).playerCustomLeaveBed( player );
    }
}
