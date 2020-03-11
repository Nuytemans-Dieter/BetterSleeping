package be.dezijwegel.Runnables;

import be.dezijwegel.files.EventsConfig;
import be.dezijwegel.interfaces.TimedEvent;
import be.dezijwegel.timedEvents.AprilFools;
import be.dezijwegel.timedEvents.Easter;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Month;
import java.util.*;

/**
 * This class handles starting and stopping timed events like April Fools, Easter, etc.
 */
public class DateChecker extends BukkitRunnable {

    private EventsConfig config;
    private Map<EventType, TimedEvent> events = new HashMap<>();

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

        Calendar today = Calendar.getInstance(TimeZone.getDefault());

        for (Map.Entry<EventType, TimedEvent> entry : events.entrySet())
        {
            // Read the Entry
            EventType type   = entry.getKey();
            TimedEvent event = entry.getValue();

            // Get event start and end date
            Calendar start = event.getStartDate();
            Calendar end   = event.getEndDate();

            // See if today is in the right window
            // date1.CompareTo(date2):
            // <0 : Date1 is before date2
            // =0 : Date1 is at the same time as date2
            // >0 : Date1 is after date2
            if (start.compareTo(today) <= 0 && end.compareTo(today) >= 0)
            {
                if ( ! event.getIsActive()) {   // Activate if the event is not active yet
                    event.startEvent();
                }
            }
            else if (event.getIsActive())       // Not in the right window! Stop event if currently active
            {
                event.stopEvent();
            }
        }
    }
}
