package be.dezijwegel.bettersleeping.api;

import be.dezijwegel.bettersleeping.runnables.SleepersRunnable;
import be.dezijwegel.bettersleeping.util.SleepTimeChecker;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Map;

public class Api {


    private final Map<World, SleepersRunnable> sleepHandlers;


    public Api(Map<World, SleepersRunnable> sleepHandlers) {
        this.sleepHandlers = sleepHandlers;
    }


    /**
     * Set whether or not a player should be marked as sleeping
     * @param player the player we want to make sleep/wake up
     * @param isSleeping whether or not the player should be marked as sleeping after this change
     */
    public void setSleeping(Player player, boolean isSleeping)
    {
        if (isSleeping)
            playerEnterBed( player );
        else
            playerLeavebed( player );
    }


    /**
     * Make BetterSleeping think this player is sleeping
     * @param player the player that should fake sleep
     */
    public void playerEnterBed (Player player)
    {
        World world = player.getWorld();
        if ( this.sleepHandlers.containsKey( world ) )
        {
            this.sleepHandlers.get( world ).playerEnterBed( player );
        }
    }


    /**
     * Make BetterSleeping think this player got out of bed / woke up
     * @param player the player in question
     */
    public void playerLeavebed (Player player)
    {
        World world = player.getWorld();
        if ( this.sleepHandlers.containsKey( world ) )
        {
            this.sleepHandlers.get( world ).playerCustomLeaveBed( player );
        }
    }


    /**
     * Check whether or not players can enter their bed at this time
     * This checks vanilla mechanics based on the world time and current weather
     * @param world the world to be checked
     * @return true if Minecraft allows players to enter their bed, false otherwise
     */
    public boolean isSleepPossible(World world)
    {
        return SleepTimeChecker.isSleepPossible( world );
    }


    /**
     * Check whether or not sleeping is enabled in this world
     * Marking players as sleeping
     * @param world the world to be checked
     * @return true if bettersleeping is enabled in this world, false otherwise
     */
    public boolean isSleepingEnabled(World world)
    {
        return this.sleepHandlers.containsKey( world );
    }
}
