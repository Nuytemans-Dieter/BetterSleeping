package be.dezijwegel.bettersleeping.interfaces;

import org.bukkit.World;

public interface SleepersNeededCalculator {

    /**
     * Gets the required amount of sleeping players in a world
     * @return the amount of required sleeping players
     */
    int getNumNeeded(World world);

    /**
     * Get the amount to which this object is set
     * @return the required sleeping percentage OR
     */
    int getSetting();

}
