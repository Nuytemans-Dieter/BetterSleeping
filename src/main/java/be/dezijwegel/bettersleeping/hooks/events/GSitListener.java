package be.dezijwegel.bettersleeping.hooks.events;

import be.dezijwegel.bettersleeping.runnables.SleepersRunnable;
import me.gsit.api.events.PlayerLayEvent;
import me.gsit.api.events.PlayerStandUpLayEvent;
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
        player.sendMessage("You lay down");

        // Don't handle disabled worlds
        if ( ! sleepHandlers.containsKey( player.getWorld() ))
            return;

        sleepHandlers.get( player.getWorld() ).playerCustomEnterBEd( player );
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerGetUp(PlayerStandUpLayEvent playerStandUpLayEvent)
    {
        Player player = playerStandUpLayEvent.getPlayer();
        player.sendMessage("You stand up");

        // Don't handle disabled worlds
        if ( ! sleepHandlers.containsKey( player.getWorld() ))
            return;

        sleepHandlers.get( player.getWorld() ).playerCustomLeaveBEd( player );
    }
}
