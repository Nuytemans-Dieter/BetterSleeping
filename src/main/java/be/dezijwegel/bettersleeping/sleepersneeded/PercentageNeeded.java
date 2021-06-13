package be.dezijwegel.bettersleeping.sleepersneeded;

import be.betterplugins.core.messaging.logging.BPLogger;
import be.dezijwegel.bettersleeping.interfaces.SleepersNeededCalculator;
import be.dezijwegel.bettersleeping.permissions.BypassChecker;
import be.dezijwegel.bettersleeping.util.ConfigContainer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.logging.Level;

public class PercentageNeeded implements SleepersNeededCalculator {

    private final BypassChecker bypassChecker;

    // Constants
    private final int percentage;

    @Inject
    public PercentageNeeded (ConfigContainer config, BypassChecker bypassChecker, BPLogger logger)
    {
        int percentage = config.getSleeping_settings().getInt("needed");

        if (percentage > 100)
            percentage = 100;
        else if (percentage < 0)
            percentage = 0;

        this.percentage = percentage;

        this.bypassChecker = bypassChecker;

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
    public int getNumNeeded(World world)
    {
        int numPlayers = 0;     // Num players in the world
        for (Player player : world.getPlayers())
        {
            // Internally checks Essentials bypass causes like /afk and /vanish
            if ( ! bypassChecker.isPlayerBypassed( player ))
            {
                numPlayers++;
            }
        }

        return Math.max(Math.round(percentage * numPlayers / 100f), 1);
    }

    @Override
    public int getSetting()
    {
        return percentage;
    }
}