package be.dezijwegel.events;

import be.dezijwegel.Runnables.SetTimeToDay;
import be.dezijwegel.bettersleeping.BetterSleeping;
import be.dezijwegel.management.Management;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class OnSleepEvent implements Listener {

    private BetterSleeping plugin;
    private Management management;
    private SleepTracker sleepTracker;

    private long sleepDelay;
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
        if(!event.isCancelled() && event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
            if (sleepTracker.playerMaySleep(player)) {

                World world = player.getLocation().getWorld();
                sleepTracker.addSleepingPlayer(world);
                int numSleepers = sleepTracker.getNumSleepingPlayers(world);
                int sleepersLeft = sleepTracker.getTotalSleepersNeeded(world) - numSleepers;


                int numPlayersInWorld = 0;
                for (Player p: Bukkit.getOnlinePlayers())
                    if (p.getWorld().equals(world)) numPlayersInWorld++;

                if (numSleepers == numPlayersInWorld)
                {
                    // Prevents default sleeping mechanics from taking control by setting the time to day quicker
                    // The players would receive wrong messages otherwise

                    List<World> worlds = new LinkedList<World>();
                    if (multiworld)
                        worlds = Arrays.asList(player.getWorld());
                    else {
                        for (World entry : Bukkit.getWorlds()) {
                            worlds.add(entry);
                        }
                    }
                    SetTimeToDay task = new SetTimeToDay(worlds, management, sleepTracker);
                    task.run();

                    //Make sure everyone gets their buff
                    if (management.areBuffsEnabled())
                    {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (worlds.contains(p.getWorld())) {
                                int numBuffs = management.getNumBuffs();
                                management.addEffects(p);
                                Map<String, String> replace = new HashMap<String, String>();
                                replace.put("<amount>", Integer.toString( numBuffs ));
                                management.sendMessage("buff_received", p, replace, numBuffs == 1);
                            }
                        }
                    }
                }
                else if (sleepersLeft == 0)
                {
                    if (multiworld) {
                        scheduleTimeToDay(Arrays.asList(player.getWorld()));
                    } else {
                        List<World> worlds = new LinkedList<World>();
                        for (World entry : Bukkit.getWorlds()) {
                            worlds.add(entry);
                        }
                        scheduleTimeToDay(worlds);
                    }

                    Map<String, String> replace = new HashMap<String, String>();
                    //Calculates the time players have to stay in bed, (double) and Math#ceil() for accuracy but (int) for a nice looking output
                    int waitTime  = (int) Math.ceil( (double) management.getIntegerSetting("sleep_delay") / 20 );
                    replace.put("<time>", Integer.toString(waitTime));
                    replace.put("<user>", event.getPlayer().getDisplayName());
                    management.sendMessageToGroup("enough_sleeping", sleepTracker.getRelevantPlayers(player.getWorld()), replace, waitTime == 1);

                } else if (sleepersLeft > 0) {
                    Map<String, String> replace = new LinkedHashMap<String, String>();
                    replace.put("<amount>", Integer.toString(sleepersLeft));
                    replace.put("<user>", event.getPlayer().getDisplayName());
                    boolean singular;
                    if (sleepersLeft == 1) singular = true; else singular = false;
                    management.sendMessageToGroup("amount_left", sleepTracker.getRelevantPlayers(world), replace, singular);
                }
            } else {
                event.setCancelled(true);
            }
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
            if (multiworld)
            {
                deScheduleTimeToDay(Arrays.asList(event.getPlayer().getWorld()), event.getPlayer().getDisplayName());
            } else {
                List<World> worlds = new LinkedList<World>();
                for (World entry : Bukkit.getWorlds()) {
                        worlds.add(entry);
                }
                deScheduleTimeToDay(worlds, event.getPlayer().getDisplayName());
            }
        }
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event)
    {
        sleepTracker.playerLogout(event.getPlayer());
    }

    /**
     * Add a task that will be performed after a set time, it can still be cancelled!
     * @param worlds
     */
    public void scheduleTimeToDay(List<World> worlds) {
        SetTimeToDay task = new SetTimeToDay(worlds, management, sleepTracker);
        pendingTasks.add(task);
        task.runTaskLater(plugin, sleepDelay);
    }

    /**
     * Cancel tasks that will set time to day in the given worlds, they will also be removed from the pendingTasks list
     * @param worlds the world for which the time to day is cancelled
     * @param playerName the player that left their bed
     */
    public void deScheduleTimeToDay(List<World> worlds, String playerName)
    {
        if (sleepTracker.getTimeSinceLastSetToDay(worlds.get(0)) > 10)
        {
            if (multiworld) {
                int numNeeded = sleepTracker.getTotalSleepersNeeded(worlds.get(0)) - sleepTracker.getNumSleepingPlayers(worlds.get(0));

                Map<String, String> replace = new LinkedHashMap<String, String>();
                replace.put("<amount>", Integer.toString(numNeeded));
                replace.put("<user>", playerName);
                boolean singular;
                if (numNeeded == 1) singular = true; else singular = false;
                management.sendMessageToGroup("cancelled", sleepTracker.getRelevantPlayers(worlds.get(0)), replace, singular);

            }
            else
            {
                World world = worlds.get(0);

                int numNeeded = sleepTracker.getTotalSleepersNeeded(world) - sleepTracker.getNumSleepingPlayers(world);

                Map<String, String> replace = new LinkedHashMap<String, String>();
                replace.put("<amount>", Integer.toString(numNeeded));
                replace.put("<user>", playerName);
                boolean singular;
                if (numNeeded == 1) singular = true; else singular = false;
                management.sendMessageToGroup("cancelled", sleepTracker.getRelevantPlayers(world), replace, singular);

            }
        }

        for (SetTimeToDay task : pendingTasks) {
            if (worlds.get(0) != null) {
                if (task.getWorlds().contains(worlds.get(0)))
                {
                    task.cancel();
                    pendingTasks.remove(task);
                }
            }
        }
    }
}
