package be.betterplugins.bettersleeping.runnables;

import be.betterplugins.core.messaging.logging.BPLogger;
import be.betterplugins.bettersleeping.model.SleepWorld;
import be.betterplugins.bettersleeping.configuration.ConfigContainer;
import be.betterplugins.bettersleeping.util.TimeUtil;
import be.betterplugins.core.messaging.messenger.Messenger;
import be.betterplugins.core.messaging.messenger.MsgEntry;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class SleepRunnable extends BukkitRunnable
{

    private final Messenger messenger;

    private final SleepWorld sleepWorld;
    private final Set<UUID> fakeSleepers;
    private final Set<UUID> sleepers;

    private long lastTime;

    private final double daySpeedup;
    private final double nightSpeedup;
    private final double sleepSpeedup;

    public SleepRunnable(ConfigContainer config, SleepWorld sleepWorld, Messenger messenger, BPLogger logger)
    {
        this.sleepWorld = sleepWorld;

        this.messenger = messenger;

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
     * Will fail silently when the player is already sleeping
     *
     * @param player the relevant player
     */
    public void addFakeSleeper(Player player)
    {
        if (!player.isSleeping())
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
        boolean isNightSkipped = this.sleepWorld.addTime( speedup );

        if (isNightSkipped)
        {
            messenger.sendMessage(new ArrayList<>(Bukkit.getOnlinePlayers()), "<num> player slept!", new MsgEntry("<num>", numSleepers));
            for (UUID uuid : fakeSleepers)
            {
                messenger.sendMessage( Bukkit.getPlayer( uuid ), "You were a FAKE sleeper!" );
            }
            for (UUID uuid : sleepers)
            {
                messenger.sendMessage( Bukkit.getPlayer( uuid ), "You were a REAL sleeper!" );
            }
            for (Player p : Bukkit.getOnlinePlayers())
            {
                if (!fakeSleepers.contains( p.getUniqueId() ) && !sleepers.contains( p.getUniqueId() ))
                {
                    messenger.sendMessage( p, "You did NOT sleep!" );
                }
            }
        }

        lastTime = this.sleepWorld.getWorldTime();
    }
}
