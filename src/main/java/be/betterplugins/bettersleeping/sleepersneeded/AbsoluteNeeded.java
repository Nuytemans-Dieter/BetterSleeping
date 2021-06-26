package be.betterplugins.bettersleeping.sleepersneeded;

import be.betterplugins.core.messaging.logging.BPLogger;
import be.betterplugins.bettersleeping.model.SleepWorld;
import be.betterplugins.bettersleeping.configuration.ConfigContainer;

import javax.inject.Inject;
import java.util.logging.Level;

public class AbsoluteNeeded implements ISleepersCalculator
{

    // Constants
    private final int numNeeded;

    @Inject
    public AbsoluteNeeded(ConfigContainer config, BPLogger logger)
    {
        int numSetting = config.getSleeping_settings().getInt("needed");
        this.numNeeded = Math.max(0, numSetting);

        logger.log(Level.CONFIG, "Using 'absolute' as sleepers-needed calculator");
        logger.log(Level.CONFIG, "The amount of required sleepers is set to " + numNeeded);
    }

    /**
     * Get the required amount of sleeping players in this world
     * @return the absolute amount of required sleepers
     */
    @Override
    public int getNumNeeded(SleepWorld world)
    {
        return numNeeded;
    }

    @Override
    public int getSetting()
    {
        return numNeeded;
    }

}
