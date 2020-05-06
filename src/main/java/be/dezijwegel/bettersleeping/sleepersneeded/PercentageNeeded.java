package be.dezijwegel.bettersleeping.sleepersneeded;

import be.dezijwegel.bettersleeping.interfaces.SleepersNeededCalculator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class PercentageNeeded implements SleepersNeededCalculator {


    // Constants
    private final int percentage;

    public PercentageNeeded (int percentage)
    {
        if (percentage > 100)
            percentage = 100;
        else if (percentage < 0)
            percentage = 0;

        this.percentage = percentage;
    }


    /**
     * Get the required amount of sleeping players in this world
     *
     * @return the absolute amount of required sleepers
     */
    @Override
    public int getNumNeeded(World world)
    {
        int numPlayers = 0;     // Num players in the world
        for (Player player : Bukkit.getOnlinePlayers())
        {
            //TODO add afk and bypassed check
            World playerWorld = player.getWorld();
            if (playerWorld.equals(world) && world.getEnvironment() == World.Environment.NORMAL)
                numPlayers++;
        }

        return Math.max((int) Math.ceil(percentage * numPlayers / 100f), 1);
    }

    @Override
    public int getSetting()
    {
        return percentage;
    }
}