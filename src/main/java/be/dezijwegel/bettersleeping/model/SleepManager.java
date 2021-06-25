package be.dezijwegel.bettersleeping.model;

import be.betterplugins.core.collections.DoubleMap;
import be.betterplugins.core.messaging.logging.BPLogger;
import be.dezijwegel.bettersleeping.permissions.BypassChecker;
import be.dezijwegel.bettersleeping.runnables.SleepRunnable;
import be.dezijwegel.bettersleeping.util.ConfigContainer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import java.util.logging.Level;

public class SleepManager
{

    private final DoubleMap<SleepWorld, World, SleepRunnable> sleepRunnables;

    @Inject
    public SleepManager(ConfigContainer config, BypassChecker bypassChecker, JavaPlugin plugin, BPLogger logger)
    {
        YamlConfiguration sleepingSettings = config.getSleeping_settings();
        this.sleepRunnables = new DoubleMap<>();

        for (World world : Bukkit.getWorlds())
        {
            // Only allow sleeping in the overworld
            if (world.getEnvironment() != World.Environment.NORMAL)
            {
                logger.log(Level.FINEST, "Sleeping in world " + world.getName() + " will not be handled because it is not an overworld (but: " + world.getEnvironment() + ")");
                continue;
            }

            String isEnabledPath = "world_settings." + world.getName() + ".enabled";
            boolean isEnabled = !sleepingSettings.contains(isEnabledPath) || sleepingSettings.getBoolean(isEnabledPath);

            if (isEnabled)
            {
                logger.log(Level.CONFIG, "Enabling BetterSleeping in world " + world.getName());

                SleepWorld sleepWorld = new SleepWorld(world, config, bypassChecker, logger);
                SleepRunnable runnable = new SleepRunnable(config, sleepWorld);

                this.sleepRunnables.put(sleepWorld, world, runnable );

                runnable.runTaskTimer(plugin, 1L, 1L);
            }
            else
            {
                logger.log(Level.CONFIG, "NOT enabling BetterSleeping in world " + world.getName());
            }
        }
    }


    public void addFakeSleeper(Player player)
    {
        SleepRunnable runnable = sleepRunnables.getBackward( player.getWorld() );
        if (runnable != null)
            runnable.addFakeSleeper( player );
    }


    public void removeFakeSleeper(Player player)
    {
        SleepRunnable runnable = sleepRunnables.getBackward( player.getWorld() );
        if (runnable != null)
            runnable.addFakeSleeper( player );
    }


    public void stopRunnables()
    {
        // TODO: After upgrading BetterCore to 1.0.1 or later: Use DoubleMap#values()
        for (SleepWorld sleepWorld : this.sleepRunnables.keySetForward())
        {
            SleepRunnable runnable = this.sleepRunnables.getForward( sleepWorld );
            if (!runnable.isCancelled())
                runnable.cancel();
        }
        this.sleepRunnables.clear();
    }
}
