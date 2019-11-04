package be.dezijwegel.events;

import be.dezijwegel.BetterSleeping;
import be.dezijwegel.Runnables.SetTimeToDay;
import be.dezijwegel.management.Management;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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

    private List<SetTimeToDay> pendingTasks;


    public OnSleepEvent(Management management, BetterSleeping plugin)
    {
        this.plugin = plugin;
        this.management = management;
        sleepTracker = new SleepTracker(plugin, management);

        sleepDelay = management.getIntegerSetting("sleep_delay");

        pendingTasks = new LinkedList<SetTimeToDay>();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSleepEvent(PlayerBedEnterEvent event)
    {
        Player player = event.getPlayer();

        if (BetterSleeping.debug)
        {
            Bukkit.getLogger().info("-----");
            Bukkit.getLogger().info("A player went to bed");
        }

        if(!event.isCancelled() && ( Bukkit.getVersion().contains("1.12") || event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK ) ) {
            if (sleepTracker.playerMaySleep(player)) {

                World world = player.getWorld();
                sleepTracker.addSleepingPlayer(world);
                int numSleepers = sleepTracker.getNumSleepingPlayers(world);
                int sleepersLeft = sleepTracker.getTotalSleepersNeeded(world) - numSleepers;


                int numPlayersInWorld = 0;
                for (Player p: Bukkit.getOnlinePlayers()) {
                    if (p.getWorld().equals(world)) {
                        if ( ! sleepTracker.isPlayerBypassed( p ) )
                            numPlayersInWorld++;
                    }
                }

                if (BetterSleeping.debug)
                {
                    Bukkit.getLogger().info("World: " + event.getPlayer().getWorld().getName());
                    Bukkit.getLogger().info("Num sleeping players: " + numSleepers);
                    Bukkit.getLogger().info("Total sleepers needed " + sleepTracker.getTotalSleepersNeeded(world));
                }

                if (numSleepers == numPlayersInWorld)
                {
                    // Prevents default sleeping mechanics from taking control by setting the time to day quicker
                    // The players would receive wrong messages otherwise

                    if (BetterSleeping.debug)
                    {
                        Bukkit.getLogger().info("!Attention! Default mechanics try to skip the night, ignoring sleep_delay");
                    }

                    SetTimeToDay task = new SetTimeToDay(player.getWorld(), management, sleepTracker, false);
                    task.run();

                    //Make sure everyone gets their buff
                    if (management.areBuffsEnabled())
                    {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (world.equals(p.getWorld())) {
                                boolean isBypassed = sleepTracker.isPlayerBypassed( p );
                                boolean giveBypassBuffs = management.getBooleanSetting("buffs_for_bypassing_players");

                                if (BetterSleeping.debug)
                                {
                                    Bukkit.getLogger().info("[BetterSleeping] ---");
                                    Bukkit.getLogger().info("[BetterSleeping] SetTimeToDay");
                                    Bukkit.getLogger().info("[BetterSleeping] Player: " + p.getName());
                                    Bukkit.getLogger().info("[BetterSleeping] isBypassed: " + isBypassed);
                                    Bukkit.getLogger().info("[BetterSleeping] giveBypassBuffs: " + giveBypassBuffs);
                                    Bukkit.getLogger().info("[BetterSleeping] Buffs? " + (!isBypassed || giveBypassBuffs));
                                }

                                if (!isBypassed || giveBypassBuffs) {
                                    int numBuffs = management.getNumBuffs();
                                    management.addEffects(p);
                                    Map<String, String> replace = new HashMap<String, String>();
                                    replace.put("<amount>", Integer.toString(numBuffs));
                                    management.sendMessage("buff_received", p, replace, numBuffs == 1);
                                } else {
                                    Map<String, String> replace = new HashMap<>();
                                    replace.put("<amount>", Integer.toString( management.getNumBuffs() ));
                                    management.sendMessage("no_buff_received", p, replace, management.getNumBuffs() == 1);
                                }
                            }
                        }
                    }

                    // Remove the scheduled task(s)
                    for (Iterator<SetTimeToDay> it = pendingTasks.iterator(); it.hasNext();)
                    {
                        SetTimeToDay setTask = it.next();
                        if (world != null) {
                            if (setTask.getWorld().equals(world))
                            {
                                setTask.cancel();
                                it.remove();
                            }
                        }
                    }
                }
                else if (sleepTracker.getDisableSkipTracker().isDisabled( world ))
                {
                    if (sleepersLeft == 0)
                        management.sendMessageToGroup("disabled_enough_sleeping", sleepTracker.getRelevantPlayers(player.getWorld()));
                    else
                        management.sendMessage("disabled_amount_left", player);
                }
                else if (sleepersLeft == 0)
                {
                    if (BetterSleeping.debug)
                    {
                        Bukkit.getLogger().info("Enough players sleeping");
                        Bukkit.getLogger().info("Default mechanics are not taking over");
                    }

                    scheduleTimeToDay(player.getWorld());

                    Map<String, String> replace = new HashMap<String, String>();
                    //Calculates the time players have to stay in bed, (double) and Math#ceil() for accuracy but (int) for a nice looking output
                    int waitTime  = (int) Math.ceil( (double) management.getIntegerSetting("sleep_delay") / 20 );
                    replace.put("<time>", Integer.toString(waitTime));
                    replace.put("<user>", event.getPlayer().getName());
                    management.sendMessageToGroup("enough_sleeping", sleepTracker.getRelevantPlayers(player.getWorld()), replace, waitTime == 1);

                } else if (sleepersLeft > 0) {

                    if (BetterSleeping.debug)
                    {
                        Bukkit.getLogger().info("More players need to sleep");
                    }

                    Map<String, String> replace = new LinkedHashMap<String, String>();
                    replace.put("<amount>", Integer.toString(sleepersLeft));
                    replace.put("<user>", event.getPlayer().getName());
                    replace.put("<total_amount>", Integer.toString(sleepTracker.getTotalSleepersNeeded(world)));
                    replace.put("<current_amount>", Integer.toString(sleepTracker.getTotalSleepersNeeded(world) - sleepersLeft));
                    boolean singular;
                    if (sleepersLeft == 1) singular = true; else singular = false;
                    management.sendMessageToGroup("amount_left", sleepTracker.getRelevantPlayers(world), replace, singular);
                }
            } else {
                if (BetterSleeping.debug)
                {
                    Bukkit.getLogger().info("-----");
                    Bukkit.getLogger().info("Player " + player.getName() + " may not sleep!");
                    Bukkit.getLogger().info("Event cancelled");
                    Bukkit.getLogger().info("-----");
                }
                event.setCancelled(true);
            }
        }

        if (BetterSleeping.debug)
        {
            Bukkit.getLogger().info("-----");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWakeEvent (PlayerBedLeaveEvent event)
    {
        World world = event.getPlayer().getWorld();
        sleepTracker.removeSleepingPlayer(world);

        int numNeeded = sleepTracker.getTotalSleepersNeeded(world) - sleepTracker.getNumSleepingPlayers(world);

        if (BetterSleeping.debug)
        {
            Bukkit.getLogger().info("-----");
            Bukkit.getLogger().info("Wake event fires");
            Bukkit.getLogger().info("Player left bed: " + event.getPlayer().getName());
            Bukkit.getLogger().info("More players needed: " + numNeeded);
        }

        if (numNeeded > 0)
        {
            deScheduleTimeToDay(event.getPlayer().getWorld(), event.getPlayer().getName());
        }

        if (BetterSleeping.debug)
        {
            Bukkit.getLogger().info("-----");
        }
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event)
    {
        sleepTracker.playerLogout(event.getPlayer());
    }

    /**
     * Add a task that will be performed after a set time, it can still be cancelled!
     * @param world
     */
    public void scheduleTimeToDay(World world) {
        SetTimeToDay task = new SetTimeToDay(world, management, sleepTracker);
        pendingTasks.add(task);
        task.runTaskLater(plugin, sleepDelay);
    }

    /**
     * Cancel tasks that will set time to day in the given worlds, they will also be removed from the pendingTasks list
     * @param world the world for which the time to day is cancelled
     * @param playerName the name of the player that left their bed
     */
    public void deScheduleTimeToDay(World world, String playerName)
    {

        if (BetterSleeping.debug)
        {
            Bukkit.getLogger().info("-----");
            Bukkit.getLogger().info("Descheduling planned task!");
            Bukkit.getLogger().info("-----");
        }

        // If player gets out of bed himself / bed is broken / any other reason which is not a time change
        if (world.getTime() > 5000 || world.isThundering() ) {
            if (sleepTracker.getTimeSinceLastSetToDay(world) > 10) {
                int numNeeded;

                numNeeded = sleepTracker.getTotalSleepersNeeded(world) - sleepTracker.getNumSleepingPlayers(world);

                Map<String, String> replace = new LinkedHashMap<String, String>();
                replace.put("<amount>", Integer.toString(numNeeded));
                replace.put("<user>", playerName);
                replace.put("<total_amount>", Integer.toString(sleepTracker.getTotalSleepersNeeded(world)));
                replace.put("<current_amount>", Integer.toString(sleepTracker.getTotalSleepersNeeded(world) - numNeeded));
                boolean singular;
                if (numNeeded == 1) singular = true;
                else singular = false;

                management.sendMessageToGroup("cancelled", sleepTracker.getRelevantPlayers(world), replace, singular);
            }

            for (Iterator<SetTimeToDay> it = pendingTasks.iterator(); it.hasNext();)
            {
                SetTimeToDay task = it.next();
                if (world != null) {
                    if (task.getWorld().equals(world))
                    {
                        task.cancel();
                        it.remove();
                    }
                }
            }

        // If the time was just set to day
        } else {
            if (BetterSleeping.debug)
            {
                Bukkit.getLogger().info("-----");
                Bukkit.getLogger().info("Time was set to day!");
                Bukkit.getLogger().info("-----");
            }
        }
    }

    /**
     * Get the instance of sleepTracker
     * @return the sleepTracker object in use
     */
    public SleepTracker getSleepTracker()
    {
        return sleepTracker;
    }
}
