package be.dezijwegel.runnables;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class TimeChangeRunnable extends BukkitRunnable {

    // Final variables
    private final World world;
    private final Set<Player> sleepers;

    // Variables that handle the status


    // Variables for internal working
    private boolean isSkipping = false;
    private long oldTime;
    private long numSleepers;


    /**
     * A runnable that will detect time changes and its cause
     * @param world
     */
    public TimeChangeRunnable(World world)
    {
        this.sleepers = new HashSet<>();

        this.world = world;
        oldTime = world.getTime();
    }


    /**
     * Mark a player as sleeping
     * A reference to the player will be stored
     * @param player the now sleeping player
     */
    public void playerEnterBed(Player player)
    {
        sleepers.add(player);
    }


    /**
     * Mark a player as awake
     * The player's reference will be deleted
     * @param player the now awake player
     */
    public void playerLeaveBed(Player player)
    {
        sleepers.remove(player);
    }


    @Override
    public void run() {
        // Time check subsystem: detect time set to day
        long newTime = world.getTime();
        // True if normal execution
        if (newTime != oldTime + 1) {
            Bukkit.getLogger().info("Time set detected in " + world.getName() + "!!");
        }

        // Sleeper handler
        // If no changes in the amount of sleepers
//        if (numSleepers == sleepers.size())
//            return;


    }
}
