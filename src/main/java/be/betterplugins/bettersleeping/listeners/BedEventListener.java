package be.betterplugins.bettersleeping.listeners;

import be.betterplugins.bettersleeping.configuration.ConfigContainer;
import be.betterplugins.bettersleeping.model.SleepWorldManager;
import be.betterplugins.core.messaging.logging.BPLogger;
import be.betterplugins.core.messaging.messenger.Messenger;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerQuitEvent;


import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class BedEventListener implements Listener
{

    private final HashMap<UUID, Long> lastBedEnterMap;
    private final int cooldownMs;

    private final SleepWorldManager sleepWorldManager;
    private final Messenger messenger;
    private final BPLogger logger;

    private final Set<BedEnterResult> blacklistedResults;

    @Inject
    public BedEventListener(SleepWorldManager sleepWorldManager, ConfigContainer container, Messenger messenger, BPLogger logger)
    {
        this.cooldownMs = 1000 * container.getSleeping_settings().getInt("bed_enter_cooldown");
        this.lastBedEnterMap = new HashMap<>();

        this.sleepWorldManager = sleepWorldManager;
        this.messenger = messenger;
        this.logger = logger;

        this.blacklistedResults = new HashSet<BedEnterResult>()
        {{
            add(BedEnterResult.NOT_POSSIBLE_HERE);
            add(BedEnterResult.NOT_POSSIBLE_NOW);
            add(BedEnterResult.TOO_FAR_AWAY);
            add(BedEnterResult.OTHER_PROBLEM);
        }};
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSleep(PlayerBedEnterEvent event)
    {

        //TODO Implement bypassing
//        if ( bypassChecker.isPlayerBypassed( player ) )
//        {
//            messenger.sendMessage(player, "bypass_message", false);
//            // Don't return, always allow the player to sleep
//            //return;
//        }

        // Only handle sleeping in enabled worlds
        if (! sleepWorldManager.isWorldEnabled( event.getPlayer().getWorld() ))
        {
            logger.log(Level.FINER, "Player " + event.getPlayer().getName() + " tried to enter a bed in a disabled world.");
            return;
        }

        // Handle sleeping with monsters
        if (event.getBedEnterResult() == BedEnterResult.NOT_SAFE)
        {
            if (event.getPlayer().hasPermission("bettersleeping.monsters_sleep"))
            {
                event.setUseBed(Event.Result.ALLOW);
            }
            else
            {
                return;
            }
        }

        // Handle failed bed enter events
        if ( blacklistedResults.contains( event.getBedEnterResult() ) )
        {
            logger.log(Level.FINER, "Player " + event.getPlayer().getName() + " failed to enter their bed: " + event.getBedEnterResult());
            event.setCancelled(true);
            return;
        }

        // Check sleep cooldown
        Player player = event.getPlayer();
        if (!canPlayerSleep(player))
        {
            messenger.sendMessage(player, "bed_enter_cooldown");
            event.setUseBed(Event.Result.DENY);
            return;
        }

        logger.log(Level.FINE, "Player " + event.getPlayer().getName() + " entered their bed");

        lastBedEnterMap.put( player.getUniqueId(), System.currentTimeMillis() );
        messenger.sendMessage(player, "bed_enter_message");
        sleepWorldManager.addSleeper( player );
    }

    public boolean canPlayerSleep(Player player)
    {
        UUID uuid = player.getUniqueId();
        if (!this.lastBedEnterMap.containsKey( uuid ))
            return true;

        long previousTime = this.lastBedEnterMap.get( uuid );
        long currentTime = System.currentTimeMillis();
        return currentTime - previousTime >= this.cooldownMs;
    }

    @EventHandler
    public void onWake(PlayerBedLeaveEvent event)
    {
        logger.log(Level.FINE, "Player " + event.getPlayer().getName() + " left their bed at time: " + event.getBed().getWorld().getTime());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        lastBedEnterMap.remove( event.getPlayer().getUniqueId() );
    }

}
