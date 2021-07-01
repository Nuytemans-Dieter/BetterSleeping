package be.betterplugins.bettersleeping.model;

import be.betterplugins.bettersleeping.runnables.SleepRunnable;
import be.betterplugins.core.collections.DoubleMap;
import be.betterplugins.core.messaging.logging.BPLogger;
import be.betterplugins.bettersleeping.permissions.BypassChecker;
import be.betterplugins.bettersleeping.configuration.ConfigContainer;
import be.betterplugins.core.messaging.messenger.Messenger;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.logging.Level;

@Singleton
public class SleepWorldManager
{

    private final DoubleMap<SleepWorld, World, SleepRunnable> sleepRunnables;

    @Inject
    @Singleton
    public SleepWorldManager(List<World> allWorlds, ConfigContainer config, BypassChecker bypassChecker, Messenger messenger, JavaPlugin plugin, BPLogger logger)
    {
        YamlConfiguration sleepingSettings = config.getSleeping_settings();
        this.sleepRunnables = new DoubleMap<>();

        for (World world : allWorlds)
        {
            // Only allow sleeping in the overworld
            if (world.getEnvironment() != World.Environment.NORMAL)
            {
                logger.log(Level.FINEST, "Sleeping in world " + world.getName() + " will not be handled because it is not an overworld (but: " + world.getEnvironment() + ")");
                continue;
            }

            String isEnabledPath = "world_settings." + world.getName() + ".enabled";
            boolean isEnabled = ( !sleepingSettings.contains(isEnabledPath) ) || sleepingSettings.getBoolean(isEnabledPath);

            Boolean doDayLightRule = world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE);
            boolean doDayLightCycle = doDayLightRule == null || doDayLightRule;

            // Only enable if this world is enabled in the config AND time has been paused
            if (isEnabled && !doDayLightCycle)
            {
                logger.log(Level.CONFIG, "Enabling BetterSleeping in world " + world.getName());

                SleepWorld sleepWorld = new SleepWorld(world, config, bypassChecker, logger);
                SleepRunnable runnable = new SleepRunnable(config, sleepWorld, messenger, logger);

                this.sleepRunnables.put(sleepWorld, world, runnable );

                runnable.runTaskTimer(plugin, 1L, 1L);
            }
            else
            {
                logger.log(Level.CONFIG, "NOT enabling BetterSleeping in world " + world.getName() + ". Enabled in config? " + isEnabled + ". DoDayLightCycle? " + doDayLightCycle);
            }
        }
    }


    /**
     * Check whether this world is a BetterSleeping world
     *
     * @param world the world to be checked
     * @return true if BetterSleeping handles sleeping
     */
    public boolean isWorldEnabled(World world)
    {
        return sleepRunnables.getBackward( world ) != null;
    }

    /**
     * Add a sleeper to the right sleep runnable
     *
     * @param player the player that should be marked as sleeping
     */
    public void addSleeper(Player player)
    {
        SleepRunnable runnable = sleepRunnables.getBackward( player.getWorld() );
        if (runnable != null)
            runnable.addSleeper( player );
    }


    /**
     * Remove a sleeper from the right sleep runnable
     *
     * @param player the player that should no longer be marked as sleeping
     */
    public void removeSleeper(Player player)
    {
        SleepRunnable runnable = sleepRunnables.getBackward( player.getWorld() );
        if (runnable != null)
            runnable.addSleeper( player );
    }


    /**
     * Stop all sleeping runnables
     */
    public void stopRunnables()
    {
        for (SleepRunnable runnable : this.sleepRunnables.values())
            if (!runnable.isCancelled())
                runnable.cancel();
        this.sleepRunnables.clear();
    }
}
