package be.dezijwegel.events;

import be.dezijwegel.BetterSleeping;
import be.dezijwegel.Runnables.EnableSkipWorld;
import be.dezijwegel.management.Management;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DisableSkipTracker {

    Plugin plugin;

    Management management;
    SleepTracker sleepTracker;

    private ArrayList<World> disabledWorlds;        // The last time that skipping the night was disabled

    boolean enabled;    // Whether or not this feature is enabled
    int duration;       // The duration (in minutes) of this feature

    public DisableSkipTracker(Plugin plugin, Management management, SleepTracker sleepTracker, boolean enabled, int duration)
    {
        this.plugin = plugin;

        this.management = management;
        this.sleepTracker = sleepTracker;

        disabledWorlds = new ArrayList<>();
        //lastUserNightCmd = new HashMap<>();

        this.enabled = enabled;
        this.duration = duration;
    }


    /**
     * Disable skipping the night in a given world for the set duration
     * @param world the world where skipping the night is temporarily disabled
     * @param player the player who issued the command
     */
    public void disableSkip(World world, Player player)
    {
        if ( ! enabled )
            return;

        if ( ! disabledWorlds.contains( world ))
            disabledWorlds.add( world );
        EnableSkipWorld enableWorld = new EnableSkipWorld(plugin, management, sleepTracker, disabledWorlds, world);
        enableWorld.runTaskLater(plugin, duration * 1200);
    }


    /**
     * Checks whether or not skipping the night in a world is still disabled
     * @param world
     * @return
     */
    public boolean isDisabled(World world)
    {
        if ( ! enabled)
            return false;

        if (disabledWorlds.contains(world))
            return true;
        else
            return false;
    }


    /**
     * Get for how many minutes skipping the night will be disabled
     * @return a duration in minutes
     */
    public int getDuration()
    {
        return duration;
    }

}
