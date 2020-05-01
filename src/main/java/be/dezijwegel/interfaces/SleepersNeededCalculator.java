package be.dezijwegel.interfaces;

import org.bukkit.World;

public interface SleepersNeededCalculator {

    /**
     * Gets the required amount of sleeping players in a world
     * @return the amount of required sleeping players
     */
    int getNumNeeded(World world);

}
