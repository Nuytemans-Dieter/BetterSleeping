package be.dezijwegel.Runnables;

import be.dezijwegel.bettersleeping.Management;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetTimeToDay extends BukkitRunnable {

    Set<World> worlds;
    Management management;

    public SetTimeToDay (List<World> worlds, Management management)
    {
        this.worlds = new HashSet<World>();
        this.management = management;

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
        for (World world : worlds)
        {
            world.setTime(1000);
            world.setStorm(false);
        }

        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (worlds.contains(player.getLocation().getWorld()))
            {
                management.sendMessage("enough_sleeping", player);
            }
        }
    }
}
