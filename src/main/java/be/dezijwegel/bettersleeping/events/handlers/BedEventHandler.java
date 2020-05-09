package be.dezijwegel.bettersleeping.events.handlers;

import be.dezijwegel.bettersleeping.hooks.EssentialsHook;
import be.dezijwegel.bettersleeping.interfaces.Reloadable;
import be.dezijwegel.bettersleeping.messaging.MsgEntry;
import be.dezijwegel.bettersleeping.messaging.Messenger;
import be.dezijwegel.bettersleeping.permissions.BypassChecker;
import be.dezijwegel.bettersleeping.permissions.SleepDelayChecker;
import be.dezijwegel.bettersleeping.runnables.SleepersRunnable;
import be.dezijwegel.bettersleeping.util.SleepStatus;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BedEventHandler implements Listener, Reloadable {


    private final Plugin plugin;
    private final Messenger messenger;
    private final BypassChecker bypassChecker;
    private final EssentialsHook essentialsHook;
    private final SleepDelayChecker sleepDelayChecker;
    private final Map<World, SleepersRunnable> runnables;


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
        // Check event status
        if (event.isCancelled() || event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK)
            return;

        Player player = event.getPlayer();

        if ( bypassChecker.isPlayerBypassed( player ) )
        {
            messenger.sendMessage(player, "bypass_message");
            // Don't return, allow the player to sleep
            //return;
        }

        // Check sleep delay
        long delay = sleepDelayChecker.whenCanPlayerSleep(player.getUniqueId());
        if (delay > 0)
        {
            event.setCancelled(true);
            messenger.sendMessage(player, "sleep_spam", new MsgEntry("<time>", "" + delay));
            return;
        }

        // Notify the subsystems of the player entering their bed. Subsystems will handle player messaging
        sleepDelayChecker.bedEnterEvent(player.getUniqueId());
        runnables.get(player.getWorld()).playerEnterBed(player);
    }


    @EventHandler
    public void bedLeaveEvent (PlayerBedLeaveEvent event)
    {
        Player player = event.getPlayer();
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
        runnables.get( player.getWorld() ).playerLeaveBed(player);
    }


    @Override
    public void reload()
    {
        for(SleepersRunnable runnable : runnables.values())
            runnable.cancel();
    }
}
