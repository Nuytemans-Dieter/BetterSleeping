package be.dezijwegel.Runnables;

import be.dezijwegel.files.EventsConfig;
import be.dezijwegel.interfaces.TimedEvent;
import be.dezijwegel.timedEvents.AprilFools;
import be.dezijwegel.timedEvents.Easter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class handles starting and stopping timed events like April Fools, Easter, etc.
 */
public class DateChecker extends BukkitRunnable {

    private EventsConfig config;
    private HashMap<EventType, TimedEvent> events = new HashMap<>();

    public enum EventType {
        APRIL_FOOLS,
        EASTER
    }

    public DateChecker(EventsConfig eventsConfig)
    {
        this.config = eventsConfig;
        
        // Create all types of events and add them to the list
        events.put(EventType.APRIL_FOOLS, new AprilFools());
        events.put(EventType.EASTER,      new Easter());
    }


    /**
     * This should not be ran more than once an hour, events should remain extremely lightweight
     */
    @Override
    public void run() {

    }
}
