package be.dezijwegel.events.handlers;

import be.dezijwegel.interfaces.SleepersNeededCalculator;
import be.dezijwegel.messenger.MsgEntry;
import be.dezijwegel.messenger.PlayerMessenger;
import be.dezijwegel.permissions.SleepDelayChecker;
import be.dezijwegel.runnables.TimeChangeRunnable;
import be.dezijwegel.timechange.TimeChanger;
import be.dezijwegel.timechange.TimeSetter;
import be.dezijwegel.timechange.TimeSmooth;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class BedEventHandler implements Listener {


    private final Plugin plugin;
    private final PlayerMessenger playerMessenger;
    private final SleepDelayChecker sleepDelayChecker;
    private final Map<World, TimeChangeRunnable> runnables;


    public BedEventHandler(Plugin plugin, PlayerMessenger playerMessenger, TimeChanger.TimeChangeType timeChangeType, SleepersNeededCalculator sleepCalculator)
    {
        this.plugin = plugin;

        this.playerMessenger = playerMessenger;
        sleepDelayChecker = new SleepDelayChecker(2);
        runnables = new HashMap<>();

        for (World world : Bukkit.getWorlds())
        {
            // Only check on the overworld
            if (world.getEnvironment() == World.Environment.NORMAL) {
                TimeChanger timeChanger = timeChangeType == TimeChanger.TimeChangeType.SETTER ? new TimeSetter(world) : new TimeSmooth(world);
                TimeChangeRunnable runnable = new TimeChangeRunnable(world, timeChanger, sleepCalculator);
                runnables.put(world, runnable);
                // Wait 40 ticks before starting the runnable
                runnable.runTaskTimer(plugin, 40L, 1L);
            }
        }
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
            playerMessenger.sendMessage(player, "sleep_delay", new MsgEntry("<delay>", "" + delay));
            return;
        }

        playerMessenger.sendMessage(player, "You entered your bed");

        // Notify the subsystems of the player entering their bed
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
