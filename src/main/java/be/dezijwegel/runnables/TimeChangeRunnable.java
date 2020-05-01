package be.dezijwegel.runnables;

import be.dezijwegel.interfaces.SleepersNeededCalculator;
import be.dezijwegel.timechange.TimeChanger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class TimeChangeRunnable extends BukkitRunnable {

    // Final data
    private final World world;
    private final Set<Player> sleepers;

    // Utility
    private final SleepersNeededCalculator sleepersCalculator;
    private final TimeChanger timeChanger;

    // Variables for internal working
    private int numNeeded;
    private long oldTime;


    /**
     * A runnable that will detect time changes and its cause
     * @param world
     */
    public TimeChangeRunnable(World world, TimeChanger timeChanger, SleepersNeededCalculator sleepersCalculator)
    {
        this.world = world;
        oldTime = world.getTime();

        this.timeChanger = timeChanger;

        this.sleepersCalculator = sleepersCalculator;
        numNeeded = sleepersCalculator.getNumNeeded(world);

        this.sleepers = new HashSet<>();
    }


    /**
     * Mark a player as sleeping
     * A reference to the player will be stored
     * @param player the now sleeping player
     */
    public void playerEnterBed(Player player)
    {
        sleepers.add(player);
        numNeeded = sleepersCalculator.getNumNeeded(world);
    }


    /**
     * Mark a player as awake
     * The player's reference will be deleted
     * @param player the now awake player
     */
    public void playerLeaveBed(Player player)
    {
        sleepers.remove(player);
        numNeeded = sleepersCalculator.getNumNeeded(world);
    }





    @Override
    public void run() {

        /*
         *  TIME DETECTOR
         */

        // Time check subsystem: detect time set to day
        long newTime = world.getTime();

        // True if time is set to day
        if (oldTime < 2350 && newTime < oldTime + 1) {
            Bukkit.getLogger().info("Time fidd in " + world.getName() + " is " + (newTime-oldTime));
            Bukkit.getLogger().info("Time set detected in " + world.getName() + "!!");
        }

        oldTime = newTime;

        // Early return if players can't sleep anyway
        if (newTime < TimeChanger.TIME_RAIN_NIGHT)
            return;

        /*
         *  SLEEP HANDLER
         */


        // Make sure the set does not contain any false sleepers
        sleepers.removeIf(player -> !player.isSleeping());

        if (sleepers.size() >= numNeeded)
        {
            timeChanger.tick();
        }
        

    }
}
