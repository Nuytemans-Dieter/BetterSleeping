package be.dezijwegel.bettersleeping.events.listeners;

import be.dezijwegel.bettersleeping.animation.Animation;
import be.dezijwegel.bettersleeping.animation.SleepingAnimation;
import be.dezijwegel.bettersleeping.animation.ZAnimation;
import be.dezijwegel.bettersleeping.animation.location.PlayerLocation;
import be.dezijwegel.bettersleeping.animation.location.StaticLocation;
import be.dezijwegel.bettersleeping.hooks.EssentialsHook;
import be.dezijwegel.bettersleeping.interfaces.Reloadable;
import be.dezijwegel.bettersleeping.messaging.MsgEntry;
import be.dezijwegel.bettersleeping.messaging.Messenger;
import be.dezijwegel.bettersleeping.permissions.BypassChecker;
import be.dezijwegel.bettersleeping.permissions.SleepDelayChecker;
import be.dezijwegel.bettersleeping.runnables.SleepersRunnable;
import be.dezijwegel.bettersleeping.util.SleepStatus;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BedEventHandler implements Listener, Reloadable {


    private final Plugin plugin;
    private final Messenger messenger;
    private final BypassChecker bypassChecker;
    private final EssentialsHook essentialsHook;
    private final SleepDelayChecker sleepDelayChecker;
    private final Map<World, SleepersRunnable> runnables;
    private final Map<UUID, SleepingAnimation> sleepingAnimations;
    private final Animation sleepingAnimation;


    public BedEventHandler(Plugin plugin, Messenger messenger, BypassChecker bypassChecker, EssentialsHook essentialsHook, int bedEnterDelay, Map<World, SleepersRunnable> runnables)
    {
        this.plugin = plugin;

        this.messenger = messenger;
        this.bypassChecker = bypassChecker;
        this.essentialsHook = essentialsHook;
        sleepDelayChecker = new SleepDelayChecker(bedEnterDelay);
        this.runnables = runnables;

        for (SleepersRunnable runnable : runnables.values())
            runnable.runTaskTimer(plugin, 40L, 1L);

        this.sleepingAnimations = new HashMap<>();
        this.sleepingAnimation = new ZAnimation(Particle.COMPOSTER, 0.5, 0.1);
    }


    /**
     * Get the sleep status in a given world
     * @param world the chosen world
     * @return the SleepStatus containing all kinds of information on the current sleep status
     */
    @Nullable
    public SleepStatus getSleepStatus(World world)
    {
        return runnables.containsKey(world) ? runnables.get(world).getSleepStatus() : null;
    }


    @EventHandler
    public void onBedEnter (PlayerBedEnterEvent event)
    {
        // Only handle checked worlds
        if ( ! runnables.containsKey( event.getPlayer().getWorld() ))
            return;

        // Check event status
        if (event.isCancelled() || event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK)
            return;

        Player player = event.getPlayer();

        // Check sleep delay
        long delay = sleepDelayChecker.whenCanPlayerSleep(player.getUniqueId());
        if (delay > 0)
        {
            event.setCancelled(true);
            messenger.sendMessage(player, "sleep_spam", true, new MsgEntry("<time>", "" + delay));
            return;
        }

        // Checks any reason for bypassing, including afk players and vanished players
        if ( bypassChecker.isPlayerBypassed( player ) )
        {
            messenger.sendMessage(player, "bypass_message", true);
            // Don't return, always allow the player to sleep
            //return;
        }

        // Notify the subsystems of the player entering their bed. Subsystems will handle player messaging
        sleepDelayChecker.bedEnterEvent(player.getUniqueId());
        runnables.get(player.getWorld()).playerEnterBed(player);

        // Start animations
        SleepingAnimation animation = new SleepingAnimation(this.sleepingAnimation, 200);
        animation.startAnimation( new PlayerLocation( player ));
        this.sleepingAnimations.put(player.getUniqueId(), animation);
    }


    @EventHandler
    public void bedLeaveEvent (PlayerBedLeaveEvent event)
    {
        // Stop animations
        UUID uuid =  event.getPlayer().getUniqueId();
        if (this.sleepingAnimations.containsKey( uuid ))
        {
            this.sleepingAnimations.remove(uuid).stopAnimation();
        }

        Player player = event.getPlayer();
        if (runnables.containsKey( player.getWorld() ))
            runnables.get( player.getWorld() ).playerLeaveBed(player);
    }


    @EventHandler
    public void teleportEvent (PlayerTeleportEvent event)
    {
        Player player = event.getPlayer();
        SleepersRunnable runnable = runnables.get( event.getFrom().getWorld() );
        if (runnable != null) runnable.playerLeaveBed(player);
    }


    @EventHandler
    public void logOutEvent (PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        if (runnables.containsKey( player.getWorld() ))
            runnables.get( player.getWorld() ).playerLogout(player);
    }


    @Override
    public void reload()
    {
        for(SleepersRunnable runnable : runnables.values())
            runnable.cancel();
    }
}
