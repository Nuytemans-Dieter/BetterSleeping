package be.betterplugins.bettersleeping.runnables;

import be.betterplugins.bettersleeping.api.BecomeDayEvent;
import be.betterplugins.bettersleeping.api.BecomeDayEvent.Cause;
import be.betterplugins.bettersleeping.configuration.ConfigContainer;
import be.betterplugins.bettersleeping.model.SleepStatus;
import be.betterplugins.bettersleeping.model.SleepWorld;
import be.betterplugins.bettersleeping.util.TimeUtil;
import be.betterplugins.core.messaging.logging.BPLogger;
import be.betterplugins.core.messaging.messenger.Messenger;
import be.betterplugins.core.messaging.messenger.MsgEntry;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class SleepRunnable extends BukkitRunnable
{

    private final Messenger messenger;

    private final SleepWorld sleepWorld;
    private final Set<UUID> sleepers;

    private boolean isSkipping;

    private final double daySpeedup;
    private final double nightSpeedup;
    private final double sleepSpeedup;


    public SleepRunnable(ConfigContainer config, SleepWorld sleepWorld, Messenger messenger, BPLogger logger)
    {
        this.sleepWorld = sleepWorld;
        this.messenger = messenger;

        this.sleepers = new HashSet<>();
        this.isSkipping = false;

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
     * Mark a player as sleeping, he does not actually have to sleep
     * Will fail silently when the player is already sleeping
     *
     * @param sleeper the relevant player
     */
    public void addSleeper(Player sleeper)
    {
        List<Player> players = sleepWorld.getAllPlayersInWorld();
        players.removeIf(player -> player.getUniqueId() == sleeper.getUniqueId());

        this.messenger.sendMessage(players,"bed_enter_broadcast");
        if (!sleeper.isSleeping())
            sleepers.add( sleeper.getUniqueId() );
    }


    /**
     * Mark a player as no longer sleeping
     *
     * @param player the relevant player
     */
    public void removeSleeper(Player player)
    {
        sleepers.remove( player.getUniqueId() );
    }


    /**
     * Reset the internal players sleeping state: makes BetterSleeping think all players woke up
     */
    public void resetSleepers()
    {
        this.sleepers.clear();
    }

    /**
     * Get the amount of players that count as sleeping
     *
     * @return the amount of sleeping players
     */
    public SleepStatus getSleepStatus()
    {
        return new SleepStatus
        (
            this.sleepers.size(),
            this.sleepWorld.getNumNeeded(),
            this.sleepWorld.getAllPlayersInWorld().size(),
            this.daySpeedup,
            this.nightSpeedup,
            this.sleepSpeedup
        );
    }

    /**
     * Check whether a player should still be counted as a sleeping player
     *
     * @param uuid the UUID of the player to be checked
     * @return True if the player is no longer in the right world or the player is offline. False otherwise
     */
    private boolean isNotValidSleeper(UUID uuid)
    {
        Player player = Bukkit.getPlayer( uuid );
        return !(player != null && player.isOnline() && this.sleepWorld.isInWorld( player ));
    }


    private double calcSpeedup()
    {
        final double speedup;

        if ( this.sleepWorld.isNight() )
        {
            speedup = isSkipping ? sleepSpeedup : nightSpeedup;
            if (isSkipping)
                this.sleepWorld.clearWeather();
        }
        else
        {
            speedup = this.daySpeedup;
        }

        return speedup;
    }

    @Override
    public void run()
    {
        // Remove invalid sleepers
        this.sleepers.removeIf(this::isNotValidSleeper);

        // Calculate the amounts
        int numSleepers = sleepers.size();
        int numNeeded = sleepWorld.getNumNeeded();


        // Only start skipping when enough players sleep & at least someone sleeps
        if (!isSkipping && numSleepers >= numNeeded && numSleepers > 0)
        {
            this.isSkipping = true;
            messenger.sendMessage(sleepWorld.getAllPlayersInWorld(), "enough_sleeping");
        }

        // Calculate the acceleration
        final double acceleration = calcSpeedup();

        // Set the correct time
        boolean isNightSkipped = this.sleepWorld.addTime( acceleration );

        if (isNightSkipped)
        {
            messenger.sendMessage(
                    new ArrayList<>(Bukkit.getOnlinePlayers()),
                    "<num> player slept!",
                    new MsgEntry("<num>", numSleepers)
            );


            // Find the cause for night skipping
            final Cause cause = isSkipping ? Cause.SLEEPING : Cause.NATURAL;

            // Find all players that slept
            final List<Player> restedPlayers = this.sleepers.stream()
                    .map(Bukkit::getPlayer)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // Get all players that did not sleep
            final List<Player> tiredPlayers = sleepWorld.getAllPlayersInWorld();
            tiredPlayers.removeIf(player -> this.sleepers.contains( player.getUniqueId() ));

            BecomeDayEvent event = new BecomeDayEvent(sleepWorld.getWorld(), cause, restedPlayers, tiredPlayers);
            Bukkit.getServer().getPluginManager().callEvent( event );

            this.isSkipping = false;
            this.sleepers.clear();
        }
    }
}
