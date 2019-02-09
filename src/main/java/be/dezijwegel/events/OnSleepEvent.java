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

    private int sleepDelay;
    private boolean multiworld;

    private List<SetTimeToDay> pendingTasks;


    public OnSleepEvent(Management management, BetterSleeping plugin)
    {
        this.plugin = plugin;
        this.management = management;
        sleepTracker = new SleepTracker(management);

        multiworld = management.getBooleanSetting("multiworld_support");
        sleepDelay = management.getIntegerSetting("sleep_delay");

        pendingTasks = new LinkedList<SetTimeToDay>();
    }

    @EventHandler
    public void onSleepEvent(PlayerBedEnterEvent event)
    {
        Player player = event.getPlayer();
        if (sleepTracker.playerMaySleep(player))
        {
            if (BetterSleeping.debug)
            {
                System.out.println(player.getName() + " got in bed!");
            }

            World world = player.getLocation().getWorld();
            sleepTracker.addSleepingPlayer(world);
            int sleepersLeft = sleepTracker.getTotalSleepersNeeded(world) - sleepTracker.getNumSleepingPlayers(world);

            if (BetterSleeping.debug)
            {
                World worldDebug = player.getLocation().getWorld();
                System.out.println("-----");
                System.out.println("World: \"" + worldDebug.getName() + "\" Multiworld: " + multiworld);
                System.out.println("# relevant players: " + sleepTracker.getRelevantPlayers(worldDebug).size() + " + Percentage needed: " + management.getIntegerSetting("percentage_needed"));
                System.out.println("Sleepers needed: " + sleepTracker.getTotalSleepersNeeded(world));
                System.out.println("Num sleeping: " + sleepTracker.getTotalSleepersNeeded(world));
                System.out.println("-----");
            }

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

                management.sendMessageToGroup("enough_sleeping", sleepTracker.getRelevantPlayers(player.getWorld()));

            } else if (sleepersLeft > 0)
            {
                Map<String, String> replace = new LinkedHashMap<String, String>();
                replace.put("<amount>", Integer.toString(sleepersLeft));
                management.sendMessageToGroup("amount_left", sleepTracker.getRelevantPlayers(world), replace);
            }
        }
    }

    @EventHandler
    public void onWakeEvent (PlayerBedLeaveEvent event)
    {
        World world = event.getPlayer().getWorld();
        sleepTracker.removeSleepingPlayer(world);

        int numNeeded = sleepTracker.getTotalSleepersNeeded(world) - sleepTracker.getNumSleepingPlayers(world);

        if (BetterSleeping.debug)
        {
            System.out.println("-----");
            System.out.println(event.getPlayer().getName() + " got out of bed!");
            System.out.println("Num needed: " + numNeeded);
            System.out.println("-----");
        }

        if (numNeeded > 0)
        {
            if (multiworld)
            {
                deScheduleTimeToDay(Arrays.asList(event.getPlayer().getWorld()));
            }
            else
            {
                List<World> worlds = new LinkedList<World>();
                for (World entry : Bukkit.getWorlds()) {
                    if (entry.getEnvironment().equals(World.Environment.NORMAL))
                        worlds.add(entry);
                }
                deScheduleTimeToDay(worlds);
            }
        }
    }

    /**
     * Add a task that will be performed after a set time, it can still be cancelled!
     * @param worlds
     */
    public void scheduleTimeToDay(List<World> worlds) {
        SetTimeToDay task = new SetTimeToDay(worlds, management, sleepTracker);
        pendingTasks.add(task);
        task.runTaskLater(plugin, sleepDelay);

        if (BetterSleeping.debug)
        {
            System.out.println("-----");
            System.out.println("Scheduling...");
            System.out.println("Delay: " + sleepDelay);
            System.out.println("# tasks: " + pendingTasks.size());
            System.out.println("Worlds: ");
            for (World world : worlds)
            {
                System.out.println(" - \"" + world.getName() + "\"");
            }
            System.out.println("-----");
        }
    }

    /**
     * Cancel tasks that will set time to day in the given worlds, they will also be removed from the pendingTasks list
     * @param worlds
     */
    public void deScheduleTimeToDay(List<World> worlds)
    {
        if (sleepTracker.getTimeSinceLastSetToDay(worlds.get(0)) < 10) {
            for (SetTimeToDay task : pendingTasks) {
                if (worlds.get(0) != null) {
                    if (task.getWorlds().contains(worlds.get(0))) {

                        task.cancel();
                        pendingTasks.remove(task);

                        if (BetterSleeping.debug) {
                            System.out.println("-----");
                            System.out.println("Descheduling: ");
                            System.out.println("After removing: ");
                            System.out.println("# Pendingtasks: " + pendingTasks.size());
                            System.out.println("-----");
                        }

                        if (multiworld) {
                            int numNeeded = sleepTracker.getTotalSleepersNeeded(worlds.get(0)) - sleepTracker.getNumSleepingPlayers(worlds.get(0));
                            Map<String, String> replace = new LinkedHashMap<String, String>();
                            replace.put("<amount>", Integer.toString(numNeeded));

                            List<Player> players = new LinkedList<Player>();
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                players.add(player);
                            }

                            management.sendMessageToGroup("cancelled", players, replace);
                        } else {
                            for (World world : task.getWorlds()) {
                                int numNeeded = sleepTracker.getTotalSleepersNeeded(world) - sleepTracker.getNumSleepingPlayers(world);
                                Map<String, String> replace = new LinkedHashMap<String, String>();
                                replace.put("<amount>", Integer.toString(numNeeded));
                                management.sendMessageToGroup("cancelled", sleepTracker.getRelevantPlayers(world), replace);
                            }
                        }
                    }
                }
            }
        }
    }
}
