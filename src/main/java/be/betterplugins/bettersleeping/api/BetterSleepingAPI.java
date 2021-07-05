package be.betterplugins.bettersleeping.api;

import be.betterplugins.bettersleeping.model.SleepStatus;
import be.betterplugins.bettersleeping.model.sleeping.SleepWorldManager;
import be.betterplugins.bettersleeping.util.TimeUtil;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

public class BetterSleepingAPI
{

    private final SleepWorldManager sleepWorldManager;

    @Inject
    public BetterSleepingAPI(SleepWorldManager sleepWorldManager)
    {
        this.sleepWorldManager = sleepWorldManager;
    }


    /**
     * Get sleeping information about a specific world.
     * Sleeping information contains acceleration settings, amount of sleeping players, ...
     *
     * @param world the world for which you need the sleep status
     * @return null if sleeping is not enabled in this world. Otherwise, the SleepStatus of the given world
     */
    public @Nullable SleepStatus getSleepStatus(World world)
    {
        return sleepWorldManager.getSleepStatus( world );
    }


    /**
     * Set whether or not a player should be marked as sleeping
     * @param player the player we want to make sleep/wake up
     * @param isSleeping whether or not the player should be marked as sleeping after this change
     */
    public void setSleeping(Player player, boolean isSleeping)
    {
        if (isSleeping)
            sleepWorldManager.addSleeper( player );
        else
            sleepWorldManager.removeSleeper( player );
    }


    /**
     * Make BetterSleeping think this player is sleeping
     * @param player the player that should fake sleep
     */
    public void playerEnterBed (Player player)
    {
        this.sleepWorldManager.addSleeper( player );
    }


    /**
     * Make BetterSleeping think this player got out of bed / woke up
     * @param player the player in question
     */
    public void playerLeavebed (Player player)
    {
        this.sleepWorldManager.addSleeper( player );
    }


    /**
     * Check whether or not players can enter their bed at this time
     * This checks vanilla mechanics based on the world time and current weather
     * @param world the world to be checked
     * @return true if Minecraft allows players to enter their bed, false otherwise
     */
    public boolean isSleepPossible(World world)
    {
        return TimeUtil.isSleepPossible( world );
    }


    /**
     * Check whether or not sleeping is enabled in this world
     * Marking players as sleeping
     * @param world the world to be checked
     * @return true if bettersleeping is enabled in this world, false otherwise
     */
    public boolean isSleepingEnabled(World world)
    {
        return this.sleepWorldManager.isWorldEnabled( world );
    }
}
