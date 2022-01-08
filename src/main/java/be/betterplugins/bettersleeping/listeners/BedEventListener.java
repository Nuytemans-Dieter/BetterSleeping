package be.betterplugins.bettersleeping.listeners;

import be.betterplugins.bettersleeping.model.permissions.BypassChecker;
import be.betterplugins.bettersleeping.model.ConfigContainer;
import be.betterplugins.bettersleeping.model.SleepStatus;
import be.betterplugins.bettersleeping.model.sleeping.SleepWorldManager;
import be.betterplugins.bettersleeping.util.TimeUtil;
import be.betterplugins.core.messaging.logging.BPLogger;
import be.betterplugins.core.messaging.messenger.Messenger;
import be.betterplugins.core.messaging.messenger.MsgEntry;
import com.google.inject.Inject;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class BedEventListener implements Listener
{

    private final HashMap<UUID, Long> lastBedEnterMap;
    private final int cooldownMs;

    private final SleepWorldManager sleepWorldManager;
    private final BypassChecker bypassChecker;
    private final Messenger messenger;
    private final BPLogger logger;

    private final Set<BedEnterResult> blacklistedResults;

    @Inject
    public BedEventListener(SleepWorldManager sleepWorldManager, ConfigContainer container, BypassChecker bypassChecker, Messenger messenger, BPLogger logger)
    {
        this.cooldownMs = 1000 * container.getSleeping_settings().getInt("bed_enter_cooldown");
        this.lastBedEnterMap = new HashMap<>();

        this.sleepWorldManager = sleepWorldManager;
        this.bypassChecker = bypassChecker;
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

    /**
     * Highest priority to call this method as late as possible, this gives other plugins the chance to cancel the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSleep(PlayerBedEnterEvent event)
    {
        // Only handle sleeping in enabled worlds
        if (! sleepWorldManager.isWorldEnabled( event.getPlayer().getWorld() ))
        {
            logger.log(Level.FINER, "Player " + event.getPlayer().getName() + " tried to enter a bed in a disabled world.");
            return;
        }

        // Handle sleeping with monsters
        if (event.getBedEnterResult() == BedEnterResult.NOT_SAFE)
        {
            if (event.getPlayer().hasPermission("bettersleeping.monsters_sleep") || event.getPlayer().hasPermission("bettersleeping.monstersnearby"))
            {
                logger.log(Level.FINER, "Player " + event.getPlayer().getName() + " was allowed to enter their bed with nearby monsters");
                event.setUseBed(Event.Result.ALLOW);
            }
            else
            {
                logger.log(Level.FINER, "Player " + event.getPlayer().getName() + " failed to enter their bed with nearby monsters");
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
            messenger.sendMessage(player, "sleep_spam",
                    new MsgEntry("<time>", Math.round(calcRemainingCooldown(player) / 1000.0)));
            event.setUseBed(Event.Result.DENY);
            return;
        }

        // Don't allow sleeping once the night should be skipped already
        if ( player.getWorld().getTime() >= (TimeUtil.TIME_NIGHT_END + 500) )
        {
            event.setUseBed(Event.Result.DENY);
            return;
        }

        // Notify bypassed players
        if ( bypassChecker.isPlayerBypassed( player ) )
        {
            messenger.sendMessage(player, "bypass_message", new MsgEntry("<player>", player.getName()));
        }

        logger.log(Level.FINE, "Player " + event.getPlayer().getName() + " entered their bed");

        lastBedEnterMap.put( player.getUniqueId(), System.currentTimeMillis() );
        sleepWorldManager.addSleeper( player );
        SleepStatus sleepStatus = sleepWorldManager.getSleepStatus( player.getWorld() );
        if (sleepStatus != null)
            messenger.sendMessage(player, "bed_enter_message",
                    new MsgEntry("<num_sleeping>", sleepStatus.getNumSleepers()),
                    new MsgEntry("<needed_sleeping>", sleepStatus.getNumNeeded()),
                    new MsgEntry("<remaining_sleeping>", sleepStatus.getNumMissing()),
                    new MsgEntry("<player>", player.getName()));
    }

    private long calcRemainingCooldown(Player player)
    {
        UUID uuid = player.getUniqueId();
        if (!this.lastBedEnterMap.containsKey( uuid ))
            return 0;

        long previousTime = this.lastBedEnterMap.get( uuid );
        long currentTime = System.currentTimeMillis();
        return Math.max(0, this.cooldownMs - (currentTime - previousTime));
    }

    public boolean canPlayerSleep(Player player)
    {
        return calcRemainingCooldown(player) <= 0;
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
