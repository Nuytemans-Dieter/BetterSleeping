package be.betterplugins.bettersleeping.model;

import be.betterplugins.bettersleeping.sleepersneeded.AbsoluteNeeded;
import be.betterplugins.core.messaging.logging.BPLogger;
import be.betterplugins.bettersleeping.permissions.BypassChecker;
import be.betterplugins.bettersleeping.sleepersneeded.ISleepersCalculator;
import be.betterplugins.bettersleeping.sleepersneeded.PercentageNeeded;
import be.betterplugins.bettersleeping.configuration.ConfigContainer;
import be.betterplugins.bettersleeping.util.TimeUtil;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class SleepWorld
{

    private long nightDuration;
    private long dayDuration;

    private final World world;
    private double time;

    private final ISleepersCalculator sleepersCalculator;
    private final BypassChecker bypassChecker;


    public SleepWorld(World world, ConfigContainer config, BypassChecker bypassChecker, BPLogger logger)
    {
        this.world = world;
        this.time = world.getTime();
        this.bypassChecker = bypassChecker;

        String counterMode = config.getSleeping_settings().getString("sleeper_counter");
        boolean usePercentage = counterMode == null || !counterMode.equalsIgnoreCase("absolute");
        sleepersCalculator = usePercentage ? new PercentageNeeded(config, logger) : new AbsoluteNeeded(config, logger);
    }


    public List<Player> getAllPlayersInWorld()
    {
        return world.getPlayers();
    }


    public List<Player> getValidPlayersInWorld()
    {
        return world.getPlayers().stream()
                .filter(player -> !bypassChecker.isPlayerBypassed(player))
                .collect(Collectors.toList());
    }


    public List<Player> getSleepingPlayersInWorld()
    {
        return world.getPlayers().stream()
                .filter(LivingEntity::isSleeping)
                .collect(Collectors.toList());
    }


    /**
     * @deprecated Avoid using this method, unless absolutely necessary. Attempt to use SleepWorld as a wrapper (rather add a method to SleepWorld than get the world first).
     */
    @Deprecated
    public World getWorld()
    {
        return world;
    }

    public void clearWeather()
    {
        if (world.hasStorm() || world.isThundering())
        {
            world.setStorm(false);
            world.setThundering(false);
        }
    }

    public boolean isNight()
    {
        return !TimeUtil.isDayTime( this.world );
    }

    public double getInternalTime()
    {
        return this.time;
    }

    public long getWorldTime()
    {
        return world.getTime();
    }

    public void setTime(double newTime)
    {
        this.time = newTime % 24000;
        world.setTime((long) this.time);
    }

    public boolean addTime(double deltaTime)
    {
        assert deltaTime >= 0;
        this.time = this.time + deltaTime;

        boolean isNextDay = false;
        if (this.time >= 24000)
        {
            isNextDay = true;
            this.time = this.time % 24000;
        }

        world.setTime( (long) this.time );
        return isNextDay;
    }


    public int getNumNeeded()
    {
        return sleepersCalculator.getNumNeeded(this);
    }
}
