package be.dezijwegel.bettersleeping.runnables;

import be.betterplugins.core.messaging.logging.BPLogger;
import be.dezijwegel.bettersleeping.model.SleepWorld;
import be.dezijwegel.bettersleeping.configuration.ConfigContainer;
import be.dezijwegel.bettersleeping.util.TimeUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class SleepRunnable extends BukkitRunnable
{

    private final SleepWorld sleepWorld;
    private final Set<UUID> fakeSleepers;
    private final Set<UUID> sleepers;

    private long lastTime;

    private final double daySpeedup;
    private final double nightSpeedup;
    private final double sleepSpeedup;

    public SleepRunnable(ConfigContainer config, SleepWorld sleepWorld, BPLogger logger)
    {
        this.sleepWorld = sleepWorld;

        this.fakeSleepers = new HashSet<>();
        this.sleepers = new HashSet<>();
        lastTime = sleepWorld.getWorldTime();

        YamlConfiguration sleepingSettings = config.getSleeping_settings();
        double dayDuration = (double) sleepingSettings.getLong("day_length") * 20;
        double nightDuration = (double) sleepingSettings.getLong("night_length") * 20;
        double skippedNightDuration = (double) sleepingSettings.getLong("night_skip_length") * 20;

        this.daySpeedup   = TimeUtil.DAY_DURATION / dayDuration;
        this.nightSpeedup = TimeUtil.NIGHT_DURATION / nightDuration;
        this.sleepSpeedup = TimeUtil.NIGHT_DURATION / skippedNightDuration;

        logger.log(Level.FINEST, "Day speedup: " + daySpeedup);
        logger.log(Level.FINEST, "Night speedup: " + nightSpeedup);
        logger.log(Level.FINEST, "Sleep speedup: " + sleepSpeedup);
    }

    /**
     * Mark a player as sleeping, that is not actually sleeping
     *
     * @param player the relevant player
     */
    public void addFakeSleeper(Player player)
    {
        fakeSleepers.add( player.getUniqueId() );
    }

    /**
     * Mark a player as no longer being fake sleeping
     *
     * @param player the relevant player
     */
    public void removeSleeper(Player player)
    {
        fakeSleepers.remove( player.getUniqueId() );
    }

    @Override
    public void run()
    {
        // Reset the passed time
        this.sleepWorld.setTime( lastTime );

        // Count sleepers
        this.sleepers.clear();
        this.sleepers.addAll(
                sleepWorld.getSleepingPlayersInWorld().stream()
                .map(Entity::getUniqueId)
                .collect(Collectors.toList())
        );
        int numSleepers = sleepers.size() + fakeSleepers.size();

        // Check num needed
        int numNeeded = sleepWorld.getNumNeeded();

        final double speedup;
        if ( this.sleepWorld.isNight() )
        {
            boolean skipNight = numSleepers >= numNeeded;
            speedup = skipNight ? sleepSpeedup : nightSpeedup;
            if (skipNight)
            {
                this.sleepWorld.clearWeather();
            }
        }
        else
        {
            speedup = this.daySpeedup;
        }


        // Set the correct time
        this.sleepWorld.addTime( speedup );
        lastTime = this.sleepWorld.getWorldTime();
    }
}
