package be.dezijwegel.bettersleeping.runnables;

import be.dezijwegel.bettersleeping.model.SleepWorld;
import be.dezijwegel.bettersleeping.util.ConfigContainer;
import be.dezijwegel.bettersleeping.util.TimeUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class SleepRunnable extends BukkitRunnable
{

    private final SleepWorld sleepWorld;
    private final Set<UUID> fakeSleepers;
    private final Set<UUID> sleepers;

    private double lastTime;



    public SleepRunnable(ConfigContainer config, SleepWorld sleepWorld)
    {
        this.sleepWorld = sleepWorld;

        this.fakeSleepers = new HashSet<>();
        this.sleepers = new HashSet<>();
        lastTime = sleepWorld.getInternalTime();

        YamlConfiguration sleepingSettings = config.getSleeping_settings();
        long dayDuration = sleepingSettings.getLong("day_length") * 20;
        long nightDuration = sleepingSettings.getLong("night_length") * 20;
        long skippedNightDuration = sleepingSettings.getLong("night_skip_length") * 20;

        double daySpeedup = (float) dayDuration / (float) TimeUtil.DAY_DURATION;
        double nightSpeedup = (float) nightDuration / (float) TimeUtil.NIGHT_DURATION;

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
        // Check the passed time
        long currentTime = sleepWorld.getWorldTime();

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

        boolean skipNight = numSleepers >= numNeeded;
        if (skipNight)
        {
            // Handle night skipping
        }
        else
        {
            // Handle normal time passing

        }

        // TODO Handle custom day/night length + night skipping
        //use below + day/night length settings
        TimeUtil.isDayTime( this.sleepWorld.getWorld() );
        // Set the correct time
        this.sleepWorld.setTime( currentTime + 1 );
        lastTime = currentTime;
    }
}
