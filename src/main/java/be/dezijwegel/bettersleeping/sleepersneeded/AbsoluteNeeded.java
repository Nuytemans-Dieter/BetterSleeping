package be.dezijwegel.bettersleeping.sleepersneeded;

import be.dezijwegel.bettersleeping.interfaces.SleepersNeededCalculator;
import org.bukkit.World;

public class AbsoluteNeeded implements SleepersNeededCalculator {

    // Constants
    private final int numNeeded;

    public AbsoluteNeeded(int numNeeded)
    {
        this.numNeeded = numNeeded;
    }

    /**
     * Get the required amount of sleeping players in this world
     * @return the absolute amount of required sleepers
     */
    @Override
    public int getNumNeeded(World world)
    {
        return numNeeded;
    }

    @Override
    public int getSetting()
    {
        return numNeeded;
    }

}
