package be.dezijwegel.Runnables;

import be.dezijwegel.files.Console;
import be.dezijwegel.files.EventsConfig;
import be.dezijwegel.interfaces.TimedEvent;
import be.dezijwegel.timedEvents.AprilFools;
import be.dezijwegel.timedEvents.Easter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * This class handles starting and stopping timed events like April Fools, Easter, etc.
 */
public class DateChecker extends BukkitRunnable {

    private EventsConfig config;
    private Console consoleConfig;
    private Map<EventType, TimedEvent> events = new HashMap<>();

    public enum EventType {
        APRIL_FOOLS,
        EASTER
    }

    public DateChecker(EventsConfig eventsConfig, Console consoleConfig)
    {
        this.config = eventsConfig;
        this.consoleConfig = consoleConfig;
        
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
                if ( config.isEnabled(type) && ! event.getIsActive()) {   // Activate if the event is not active yet

                    // Start the event
                    event.startEvent();

                    // Get the name of the event
                    String eventName = type.toString().toLowerCase();
                    eventName = eventName.replace("_", " ");
                    eventName = WordUtils.capitalize(eventName);

                    // Log to the console
                    String message = "A timed event has just started: " + eventName + "!";
                    if (consoleConfig.isPositiveGreen())
                    {
                        Bukkit.getConsoleSender().sendMessage("[BetterSleeping] " + ChatColor.LIGHT_PURPLE + message);
                    } else {
                        Bukkit.getConsoleSender().sendMessage("[BetterSleeping] " + message);
                    }
                }
            }
            else if (event.getIsActive())       // Not in the right window! Stop event if currently active
            {
                // Stop the event
                event.stopEvent();
            }
        }
    }
}
