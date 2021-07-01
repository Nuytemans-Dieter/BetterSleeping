package be.betterplugins.bettersleeping.listeners;

import be.betterplugins.bettersleeping.model.SleepWorld;
import be.betterplugins.bettersleeping.model.SleepWorldManager;
import be.betterplugins.core.messaging.logging.BPLogger;
import be.betterplugins.core.messaging.messenger.Messenger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import javax.inject.Inject;
import java.util.logging.Level;

public class BedEventListener implements Listener
{

    private final SleepWorldManager sleepWorldManager;
    private final Messenger messenger;
    private final BPLogger logger;

    @Inject
    public BedEventListener(SleepWorldManager sleepWorldManager, Messenger messenger, BPLogger logger)
    {
        this.sleepWorldManager = sleepWorldManager;
        this.messenger = messenger;
        this.logger = logger;
    }

    @EventHandler
    public void onSleep(PlayerBedEnterEvent event)
    {

        if (! sleepWorldManager.isWorldEnabled( event.getPlayer().getWorld() ))
        {
            logger.log(Level.FINER, "Player " + event.getPlayer().getName() + " tried to enter a bed in a disabled world.");
            return;
        }

        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK)
        {
            logger.log(Level.FINER, "Player " + event.getPlayer().getName() + " failed to enter their bed: " + event.getBedEnterResult());
            return;
        }

        logger.log(Level.FINE, "Player " + event.getPlayer().getName() + " entered their bed");

        messenger.sendMessage(event.getPlayer(), "bed_enter_message");
        sleepWorldManager.addSleeper( event.getPlayer() );
    }

    @EventHandler
    public void onWake(PlayerBedLeaveEvent event)
    {
        logger.log(Level.FINE, "Player " + event.getPlayer().getName() + " left their bed at time: " + event.getBed().getWorld().getTime());
    }

}
