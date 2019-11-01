package be.dezijwegel.Runnables;

import be.dezijwegel.events.SleepTracker;
import be.dezijwegel.management.Management;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EnableSkipWorld extends BukkitRunnable {

    private Plugin plugin;

    private Management management;
    private SleepTracker sleepTracker;

    private ArrayList<World> disabledWorlds;
    private World enableWorld;

    /**
     * Will enable the given world after the time has ran out
     * @param disabledWorlds
     * @param enable
     */
    public EnableSkipWorld(Plugin plugin, Management management, SleepTracker sleepTracker, ArrayList<World> disabledWorlds, World enable)
    {
        this.plugin = plugin;

        this.management = management;
        this.sleepTracker = sleepTracker;

        this.disabledWorlds = disabledWorlds;
        this.enableWorld = enable;
    }

    @Override
    public void run() {
        disabledWorlds.remove( enableWorld );

        // Tell the players that sleeping has been enabled again
        management.sendMessageToGroup("enable_skip", sleepTracker.getRelevantPlayers(enableWorld));

        // Check if enough people are sleeping. If this is the case, skip the night!
        int numSleeping = sleepTracker.getNumSleepingPlayers( enableWorld );
        int numNeeded = sleepTracker.getTotalSleepersNeeded( enableWorld );
        if (numSleeping >= numNeeded) {
            SetTimeToDay setTimeToDay = new SetTimeToDay(enableWorld, management, sleepTracker);
            setTimeToDay.runTaskLater(plugin, management.getIntegerSetting("sleep_delay"));

            Map<String, String> replace = new HashMap<String, String>();
            //Calculates the time players have to stay in bed, (double) and Math#ceil() for accuracy but (int) for a nice looking output
            int waitTime  = (int) Math.ceil( (double) management.getIntegerSetting("sleep_delay") / 20 );
            replace.put("<time>", Integer.toString(waitTime));
            replace.put("<user>", "The last required player");
            management.sendMessageToGroup("enough_sleeping", sleepTracker.getRelevantPlayers( enableWorld ), replace, waitTime == 1);
        }
    }
}
