package be.betterplugins.bettersleeping.sleepersneeded;

import be.betterplugins.bettersleeping.model.ConfigContainer;
import be.betterplugins.bettersleeping.model.sleeping.SleepWorld;
import be.betterplugins.core.messaging.logging.BPLogger;
import com.google.inject.Inject;

import java.util.logging.Level;

public class PercentageNeeded implements ISleepersCalculator
{

    private final int percentage;

    @Inject
    public PercentageNeeded(ConfigContainer config, BPLogger logger)
    {
        int percentage = config.getSleeping_settings().getInt("needed");

        if (percentage > 100)
            percentage = 100;
        else if (percentage < 0)
            percentage = 0;

        this.percentage = percentage;

        logger.log(Level.CONFIG, "Using 'percentage' as sleepers-needed calculator");
        logger.log(Level.CONFIG, "The percentage is set to " + percentage + "%");
    }


    /**
     * Get the required amount of sleeping players in this world
     * Ignores bypassed (afk, vanished, gm/permission bypassed,...) players
     *
     * @return the absolute amount of required sleepers
     */
    @Override
    public int getNumNeeded(SleepWorld world)
    {
        int numPlayers = world.getValidPlayersInWorld().size();
        return Math.max(Math.round(percentage * numPlayers / 100f), 1);
    }

    @Override
    public int getSetting()
    {
        return percentage;
    }
}