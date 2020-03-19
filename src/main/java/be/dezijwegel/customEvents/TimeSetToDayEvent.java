package be.dezijwegel.customEvents;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TimeSetToDayEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private World world;

    public TimeSetToDayEvent(World world)
    {
        this.world = world;
    }


    /**
     * Get the world in which the time was set to day
     * @return a World instance
     */
    public World getWorld()
    {
        return world;
    }


    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
