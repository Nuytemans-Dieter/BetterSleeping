package be.dezijwegel.bettersleeping.sleepersneeded;

import be.dezijwegel.bettersleeping.model.SleepWorld;

public interface ISleepersCalculator
{

    /**
     * Gets the required amount of sleeping players in a world
     * @return the amount of required sleeping players
     */
    int getNumNeeded(SleepWorld world);

    /**
     * Get the amount to which this object is set
     * @return the required sleeping percentage OR
     */
    int getSetting();

}
