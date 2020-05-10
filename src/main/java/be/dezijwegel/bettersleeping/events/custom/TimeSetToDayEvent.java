package be.dezijwegel.bettersleeping.events.custom;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TimeSetToDayEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final World world;              // The world in which the time was set to day
    private final Cause cause;              // The cause of time being set to day
    private final List<Player> sleepers;    // List of players that slept
    private final List<Player> nonSleepers; // List of players that did not sleep


    public enum Cause {
        NATURAL,
        SLEEPING,
        OTHER
    }


    public TimeSetToDayEvent (World world, Cause cause, List<Player> sleepers, List<Player> nonSleepers)
    {
        super();

        this.world = world;
        this.cause = cause;
        this.sleepers = sleepers;
        this.nonSleepers = nonSleepers;
    }


    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }


    /**
     * Get the world in which the time was set to day
     * @return the world
     */
    public World getWorld()
    {
        return world;
    }


    /**
     * Get the cause of time being set to day
     * @return the cause
     */
    public Cause getCause()
    {
        return cause;
    }


    /**
     * Get a List of players that slept this night
     * @return the list of Players
     */
    public List<Player> getPlayersWhoSlept()
    {
        return sleepers;
    }


    /**
     * Get the List of players that did not sleep last night
     * @return the list of players
     */
    public List<Player> getPlayersWhoDidNotSleep()
    {
        return nonSleepers;
    }

}
