package be.dezijwegel.customEvents;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TimeSetToDayEvent extends BetterSleepingEvent {

    private World world;

    public TimeSetToDayEvent(World world)
    {
        super();
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

}
