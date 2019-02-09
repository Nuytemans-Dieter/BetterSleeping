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

        multiworld = management.getBooleanSetting("multiworld_support");
        bedEnterDelay = management.getIntegerSetting("bed_enter_delay");

        sleepTracker = new SleepTracker(multiworld, management.getIntegerSetting("bed_enter_delay"));

        pendingTasks = new LinkedList<SetTimeToDay>();
    }

    @EventHandler
    public void onSleepEvent(PlayerBedEnterEvent event)
    {
        Player player = event.getPlayer();
        if (playerMaySleep(player))
        {
            if (multiworld)
            {
                addTask(Arrays.asList(event.getPlayer().getWorld()));
            }
            else
            {
                List<World> worlds = new LinkedList<World>();
                for (World world : Bukkit.getWorlds())
                {
                    if (world.getEnvironment().equals(World.Environment.NORMAL))
                        worlds.add(world);
                }
                addTask(worlds);
            }
        } else {
            LinkedHashMap<String, String> replace = new LinkedHashMap<String, String>();
            replace.put("<time>", Long.toString(sleepTracker.whenCanPlayerSleep(player.getUniqueId())));
            management.sendMessage("sleep_spam", player, replace);
        }
    }

    @EventHandler
    public void onWakeEvent (PlayerBedLeaveEvent event)
    {

    }

    /**
     * Add a task that will be performed after a set time, it can still be cancelled!
     * @param worlds
     */
    public void addTask(List<World> worlds) {
        SetTimeToDay task = new SetTimeToDay(worlds, management);
        pendingTasks.add(task);
        task.runTaskLater(plugin, management.getIntegerSetting("sleep_delay"));
    }

    /**
     * Cancel tasks that will set time to day in the given worlds, they will also be removed from the pendingTasks list
     * @param worlds
     */
    public void removeTask(List<World> worlds)
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

    /**
     * Check if a Player meets the requirements to sleep
     * Also checks if the World meets the requirements
     * Sends messages to players if needed
     * @return
     */
    public boolean playerMaySleep(Player player)
    {
        World worldObj = player.getWorld();
        if (worldObj.getTime() > 12500 || worldObj.hasStorm() || worldObj.isThundering()) {

            UUID uuid = player.getUniqueId();

            if (sleepTracker.checkPlayerSleepDelay(uuid))
                return true;
            else return false;
        }
        return false;
    }
}
