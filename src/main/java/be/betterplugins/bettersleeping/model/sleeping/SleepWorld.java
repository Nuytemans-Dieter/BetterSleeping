package be.betterplugins.bettersleeping.model.sleeping;

import be.betterplugins.bettersleeping.model.BypassChecker;
import be.betterplugins.bettersleeping.sleepersneeded.AbsoluteNeeded;
import be.betterplugins.core.messaging.logging.BPLogger;
import be.betterplugins.bettersleeping.sleepersneeded.ISleepersCalculator;
import be.betterplugins.bettersleeping.sleepersneeded.PercentageNeeded;
import be.betterplugins.bettersleeping.model.ConfigContainer;
import be.betterplugins.bettersleeping.util.TimeUtil;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class SleepWorld
{

    private final World world;
    private double time;

    private final ISleepersCalculator sleepersCalculator;
    private final BypassChecker bypassChecker;

    public SleepWorld(World world, ConfigContainer config, BypassChecker bypassChecker, BPLogger logger)
    {
        this.world = world;
        this.time = world.getTime();
        this.bypassChecker = bypassChecker;

        String counterMode = config.getSleeping_settings().getString("sleeper_calculator");
        boolean usePercentage = counterMode == null || !counterMode.equalsIgnoreCase("absolute");
        sleepersCalculator = usePercentage ? new PercentageNeeded(config, logger) : new AbsoluteNeeded(config, logger);
    }


    /**
     * Get all players that are in this world, regardless of whether they sleep/are bypassed
     *
     * @return a list of players
     */
    public List<Player> getAllPlayersInWorld()
    {
        return world.getPlayers();
    }


    /**
     * Get all players that are not bypassed and are in this world
     * Will return all players in this world if bypassing is disabled
     *
     * @return a list of players
     */
    public List<Player> getValidPlayersInWorld()
    {
        return world.getPlayers().stream()
                .filter(player -> !bypassChecker.isPlayerBypassed(player))
                .collect(Collectors.toList());
    }


    /**
     * Get all players that are currently sleeping in this world
     *
     * @return a list of sleeping players, that are in this world
     */
    public List<Player> getSleepingPlayersInWorld()
    {
        return world.getPlayers().stream()
                .filter(LivingEntity::isSleeping)
                .collect(Collectors.toList());
    }


    /**
     * Check whether a player is in this world
     *
     * @param player the player to be checked
     * @return true if this player is currently in this world, false otherwise
     */
    public boolean isInWorld(Player player)
    {
        return player.getWorld().getName().equals( this.world.getName() );
    }


    /**
     * Clear the weather in this world, if the weather is not clear
     */
    public void clearWeather()
    {
        if (!world.isClearWeather())
        {
            world.setStorm(false);
            world.setThundering(false);
        }
    }


    /**
     * Check whether or not it is currently night in this world
     *
     * @return whether or not it is nighttime
     */
    public boolean isNight()
    {
        return !TimeUtil.isDayTime( this.world );
    }

    /**
     * Get the BetterSleeping time in this world
     *
     * @return the time in (double) ticks
     */
    public double getInternalTime()
    {
        return this.time;
    }


    /**
     * Get the time in this minecraft world
     *
     * @return the time in ticks
     */
    public long getWorldTime()
    {
        return world.getTime();
    }

    /**
     * Set the new time in this world
     *
     * @param newTime the desired time, a modulo of 24000 will be performed!
     */
    public void setTime(double newTime)
    {
        this.time = newTime % 24000;
        world.setTime((long) this.time);
    }


    /**
     * Add an amount of ticks to this world, on top of the current time
     *
     * @param deltaTicks the amount of ticks to forward the time
     * @return whether or not adding this time caused the time to become day
     */
    public boolean addTime(double deltaTicks)
    {
        assert deltaTicks >= 0;
        this.time = this.time + deltaTicks;

        boolean isNextDay = false;
        if (this.time >= 24000)
        {
            isNextDay = true;
            this.time = this.time % 24000;
        }

        world.setTime( (long) this.time );
        return isNextDay;
    }


    /**
     * Calculate the passed time between now and a given amount of ticks
     *
     * @param sinceTicks the time (in ticks) since when the time should be calculated
     * @return the amount of ticks between the argument sinceTicks and now
     */
    public long calcPassedTime(final long sinceTicks)
    {
        long currentTicks = this.world.getTime();
        if (currentTicks >= sinceTicks)
        {
            return currentTicks - sinceTicks;
        }
        else
        {
            return (24000 + currentTicks) - sinceTicks;
        }
    }


    /**
     * Check whether time became day between now and the given amount of ticks
     *
     * @param sinceTicks the time since when we are checking whether time became day
     * @return whether or not the night was over during sinceTicks and now
     */
    public boolean didTimeBecomeDay(final long sinceTicks)
    {
        return this.world.getTime() < sinceTicks;
    }

    public int getNumNeeded()
    {
        return sleepersCalculator.getNumNeeded(this);
    }

    /**
     * Get the world which is represented by this SleepWorld
     * @deprecated Do not make direct calls to the retrieved worlds, use SleepWorld as a wrapper for this object.
     * This getter is required in rare instances, which is why it is not removed but should be used as little as possible.
     *
     * @return the relevant World
     */
    @Deprecated
    public World getWorld()
    {
        return this.world;
    }
}
