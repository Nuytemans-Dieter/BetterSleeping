package be.dezijwegel.timedEvents.aprilFools;

import be.dezijwegel.files.Lang;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class SetTimeNightRunnable extends BukkitRunnable {

    private final World world;
    private final Lang lang;

    public SetTimeNightRunnable(World world, Lang lang)
    {
        this.world = world;
        this.lang  = lang;
    }


    @Override
    public void run() {
        world.setTime(14000);

        // Get the affected players
        List<Player> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (player.getWorld().equals(world))
            {
                players.add(player);
            }
        }

        // Send message to affected players
        lang.sendMessageToGroup("april_fools_time_prank", players);
    }
}
