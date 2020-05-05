package be.dezijwegel.bettersleeping.events.handlers;

import be.dezijwegel.bettersleeping.hooks.EssentialsHook;
import be.dezijwegel.bettersleeping.messenger.MsgEntry;
import be.dezijwegel.bettersleeping.messenger.PlayerMessenger;
import be.dezijwegel.bettersleeping.permissions.BypassChecker;
import be.dezijwegel.bettersleeping.permissions.SleepDelayChecker;
import be.dezijwegel.bettersleeping.runnables.SleepersRunnable;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class BedEventHandler implements Listener {


    private final Plugin plugin;
    private final PlayerMessenger playerMessenger;
    private final BypassChecker bypassChecker;
    private final EssentialsHook essentialsHook;
    private final SleepDelayChecker sleepDelayChecker;
    private final Map<World, SleepersRunnable> runnables;


    public BedEventHandler(Plugin plugin, PlayerMessenger playerMessenger, BypassChecker bypassChecker, EssentialsHook essentialsHook, Map<World, SleepersRunnable> runnables)
    {
        this.plugin = plugin;

        this.playerMessenger = playerMessenger;
        this.bypassChecker = bypassChecker;
        this.essentialsHook = essentialsHook;
        sleepDelayChecker = new SleepDelayChecker(2);
        this.runnables = runnables;

        for (SleepersRunnable runnable : runnables.values())
            runnable.runTaskTimer(plugin, 40L, 1L);
    }


    @EventHandler
    public void onBedEnter (PlayerBedEnterEvent event)
    {
        // Check event status
        if (event.isCancelled() || event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK)
            return;

        Player player = event.getPlayer();

        // TODO Check bypass permissions, afk status(use EssentialsHook) and bypassed gamemode

        // Check sleep delay
        long delay = sleepDelayChecker.whenCanPlayerSleep(player.getUniqueId());
        if (delay > 0)
        {
            event.setCancelled(true);
            playerMessenger.sendMessage(player, "sleep_spam", new MsgEntry("<time>", "" + delay));
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
        runnables.get(player.getWorld()).playerLeaveBed(player);
    }


    @EventHandler
    public void teleportEvent (PlayerTeleportEvent event)
    {
        Player player = event.getPlayer();
        runnables.get(event.getFrom().getWorld()).playerLeaveBed(player);
    }


    @EventHandler
    public void logOutEvent (PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        runnables.get(player.getWorld()).playerLeaveBed(player);
    }

}
