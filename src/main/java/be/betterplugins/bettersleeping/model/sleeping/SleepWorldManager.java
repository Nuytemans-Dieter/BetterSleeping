package be.betterplugins.bettersleeping.model.sleeping;

import be.betterplugins.bettersleeping.listeners.AnimationHandler;
import be.betterplugins.bettersleeping.model.permissions.BypassChecker;
import be.betterplugins.bettersleeping.model.ConfigContainer;
import be.betterplugins.bettersleeping.model.SleepStatus;
import be.betterplugins.bettersleeping.runnables.SleepRunnable;
import be.betterplugins.core.collections.DoubleMap;
import be.betterplugins.core.messaging.logging.BPLogger;
import be.betterplugins.core.messaging.messenger.Messenger;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.logging.Level;

@Singleton
public class SleepWorldManager
{

    private final DoubleMap<SleepWorld, World, SleepRunnable> sleepRunnables;
    private final AnimationHandler animationHandler;

    @Inject
    public SleepWorldManager(List<World> allWorlds, ConfigContainer config, BypassChecker bypassChecker, Messenger messenger, AnimationHandler animationHandler, JavaPlugin plugin, BPLogger logger)
    {
        YamlConfiguration sleepingSettings = config.getSleeping_settings();
        this.sleepRunnables = new DoubleMap<>();

        this.animationHandler = animationHandler;

        for (World world : allWorlds)
        {
            // Only allow sleeping in the overworld
            if (world.getEnvironment() != World.Environment.NORMAL && world.getEnvironment() != World.Environment.CUSTOM)
            {
                logger.log(Level.FINE, "Sleeping in world " + world.getName() + " will not be handled because it is not an overworld / custom world (but: " + world.getEnvironment() + ")");
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
     * Get the SleepStatus of a specific world, if sleeping is enabled in that world
     *
     * @param world the world for which the status should be retrieved
     * @return null if sleeping is not enabled in this world, the relevant SleepStatus otherwise
     */
    public @Nullable SleepStatus getSleepStatus(World world)
    {
        SleepRunnable runnable = this.sleepRunnables.getBackward(world);
        return runnable != null ? runnable.getSleepStatus() : null;
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
        {
            runnable.addSleeper(player);
            this.animationHandler.startSleepingAnimation( player );
        }
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
