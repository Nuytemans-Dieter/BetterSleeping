package be.dezijwegel.events;

import be.dezijwegel.Runnables.SetTimeToDay;
import be.dezijwegel.bettersleeping.BetterSleeping;
import be.dezijwegel.bettersleeping.Management;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import java.util.*;

public class OnSleepEvent implements Listener {

    private BetterSleeping plugin;
    private Management management;
    private SleepTracker sleepTracker;

    private int bedEnterDelay;
    private boolean multiworld;

    private List<SetTimeToDay> pendingTasks;


    public OnSleepEvent(Management management, BetterSleeping plugin)
    {
        this.plugin = plugin;
        this.management = management;
        sleepTracker = new SleepTracker(management);

        multiworld = management.getBooleanSetting("multiworld_support");
        bedEnterDelay = management.getIntegerSetting("bed_enter_delay");

        pendingTasks = new LinkedList<SetTimeToDay>();
    }

    @EventHandler
    public void onSleepEvent(PlayerBedEnterEvent event)
    {
        Player player = event.getPlayer();
        if (sleepTracker.playerMaySleep(player))
        {
            World world = player.getLocation().getWorld();
            sleepTracker.addSleepingPlayer(world);
            int sleepersLeft = sleepTracker.getTotalSleepersNeeded(world) - sleepTracker.getNumSleepingPlayers(world);

            if (sleepersLeft == 0)
            {
                if (multiworld)
                {
                    scheduleTimeToDay(Arrays.asList(player.getWorld()));
                }
                else
                {
                    List<World> worlds = new LinkedList<World>();
                    for (World entry : Bukkit.getWorlds()) {
                        if (entry.getEnvironment().equals(World.Environment.NORMAL))
                            worlds.add(entry);
                    }
                    scheduleTimeToDay(worlds);
                }
            } else if (sleepersLeft > 0)
            {
                Map<String, String> replace = new LinkedHashMap<String, String>();
                replace.put("<amount>", Integer.toString(sleepersLeft));
                management.sendMessageToGroup("amount_left", sleepTracker.getRelevantPlayers(world), replace);
            }
        } else {
            Map<String, String> replace = new LinkedHashMap<String, String>();
            replace.put("<time>", Long.toString(sleepTracker.whenCanPlayerSleep(player.getUniqueId())));
            management.sendMessage("sleep_spam", player, replace);
        }
    }

    @EventHandler
    public void onWakeEvent (PlayerBedLeaveEvent event)
    {
        World world = event.getPlayer().getWorld();
        sleepTracker.removeSleepingPlayer(world);

        int numNeeded = sleepTracker.getTotalSleepersNeeded(world) - sleepTracker.getNumSleepingPlayers(world);

        if (numNeeded > 0)
        {
            Map<String, String> replace = new LinkedHashMap<String, String>();
            replace.put("<amount>", Integer.toString(numNeeded));
            management.sendMessageToGroup("cancelled", sleepTracker.getRelevantPlayers(world), replace);
        }
    }

    /**
     * Add a task that will be performed after a set time, it can still be cancelled!
     * @param worlds
     */
    public void scheduleTimeToDay(List<World> worlds) {
        SetTimeToDay task = new SetTimeToDay(worlds, management);
        pendingTasks.add(task);
        task.runTaskLater(plugin, bedEnterDelay);
    }

    /**
     * Cancel tasks that will set time to day in the given worlds, they will also be removed from the pendingTasks list
     * @param worlds
     */
    public void deScheduleTimeToDay(List<World> worlds)
    {
        for (SetTimeToDay task : pendingTasks)
        {
            if (worlds.get(0) != null)
            {
                if (task.getWorlds().contains(worlds.get(0)))
                {
                    task.cancel();
                    pendingTasks.remove(task);
                }
            }
        }
    }
}
