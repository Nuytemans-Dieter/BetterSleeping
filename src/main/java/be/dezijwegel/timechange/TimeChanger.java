package be.dezijwegel.timechange;

import org.bukkit.World;

public abstract class TimeChanger {

    public enum TimeChangeType {
        SETTER,
        SMOOTH
    }

    final World world;

    public final static int TIME_MORNING = 0;          // Morning time
    public final static int TIME_RAIN_NIGHT = 12010;   // Time at which players can enter their bed during rain
    public final static int TIME_NIGHT = 12542;        // Time at which players can enter their bed


    public TimeChanger(World world)
    {
        this.world = world;
    }


    /**
     * Tick should advance the time to its next change
     * This method is executed every tick while enough players are sleeping
     * This can be an internal counter that sets the time at a certain value
     * Or this can be a single time change that appears to move the time smoothly
     *
     * @param numSleeping the amount of current sleeping players
     * @param numNeeded the amount of required sleeping players
     */
    public abstract void tick(int numSleeping, int numNeeded);

}
