package be.dezijwegel.bettersleeping.timechange;

import org.bukkit.World;

public abstract class TimeChanger {


    public enum TimeChangeType {
        SETTER,
        SMOOTH
    }


    public final static int TIME_MORNING = 0;          // Morning time
    public final static int TIME_RAIN_NIGHT = 12010;   // Time at which players can enter their bed during rain
    public final static int TIME_NIGHT = 12542;        // Time at which players can enter their bed


    final World world;
    boolean wasSetToDay = false;
    boolean removedStorm = false;


    public TimeChanger(World world)
    {
        this.world = world;
    }


    /**
     * Checks whether or not this object has set the time to day
     * Will only return true ONCE until the time has been set to day again
     * @return whether or not this object caused the time to be day
     */
    public boolean wasTimeSetToDay()
    {
        boolean old = wasSetToDay;
        wasSetToDay = false;
        return old;
    }


    /**
     * Checks whether or not this TimeChanger recently removed a storm
     * @param doReset when set to true, the removedStorm boolean will be reset
     * @return the current value of removedStorm
     */
    public boolean removedStorm(boolean doReset)
    {
        boolean value = removedStorm;
        removedStorm = !doReset && removedStorm;
        return value;
    }


    /**
     * Get the type of this time changer
     * @return the TimeChangerType of this object
     */
    public abstract TimeChangeType getType();


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
