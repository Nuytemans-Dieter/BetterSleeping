package be.dezijwegel.Runnables;

import be.dezijwegel.management.Management;
import be.dezijwegel.events.SleepTracker;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SetTimeToDay extends BukkitRunnable {

    Set<World> worlds;

    Management management;
    SleepTracker sleepTracker;

    public SetTimeToDay (List<World> worlds, Management management, SleepTracker sleepTracker)
    {
        this.worlds = new HashSet<World>();

        this.management = management;
        this.sleepTracker = sleepTracker;

        for (World world : worlds)
        {
            this.worlds.add(world);
        }
    }

    /**
     * Get the world(s) of which the time will be set to day
     * @return
     */
    public Set<World> getWorlds()
    {
        return worlds;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (worlds.contains(player.getLocation().getWorld()))
            {
                management.sendMessage("good_morning", player);

                if (management.areBuffsEnabled()) {
                    if (player.isSleeping()) {
                        management.addEffects(player);
                        Map<String, String> replace = new HashMap<String, String>();
                        replace.put("<amount>", Integer.toString( management.getNumBuffs() ));
                        management.sendMessage("buff_received", player, replace, management.getNumBuffs() == 1);
                    }
                }
            }
        }

        for (World world : worlds)
        {
            world.setTime(1000);
            world.setStorm(false);
            sleepTracker.worldWasSetToDay(world);
        }
    }
}
