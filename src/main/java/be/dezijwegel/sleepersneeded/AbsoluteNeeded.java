package be.dezijwegel.sleepersneeded;

import be.dezijwegel.interfaces.SleepersNeededCalculator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

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

}
