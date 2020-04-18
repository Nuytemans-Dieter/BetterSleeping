package be.dezijwegel.runnables;

import be.dezijwegel.files.Console;
import be.dezijwegel.files.EventsConfig;
import be.dezijwegel.interfaces.TimedEvent;
import be.dezijwegel.management.Management;
import be.dezijwegel.timedEvents.AprilFools;
import be.dezijwegel.timedEvents.Easter;
import be.dezijwegel.util.ConsoleLogger;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * This class handles starting and stopping timed events like April Fools, Easter, etc.
 */
public class DateChecker extends BukkitRunnable {

    private EventsConfig config;
    private Console consoleConfig;
    private final Map<EventType, TimedEvent> events = new HashMap<>();
    private final Map<EventType, Boolean> reportedEvents = new HashMap<>();

    public enum EventType {
        APRIL_FOOLS,
        EASTER
    }

    public DateChecker(Plugin plugin, Management management)
    {
        this.config = management.getEventsConfig();
        this.consoleConfig = management.getConsoleConfig();
        
        // Create all types of events and add them to the list
        events.put(EventType.APRIL_FOOLS, new AprilFools(plugin, management));
        events.put(EventType.EASTER,      new Easter(    plugin, management));
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

                // Get the name of the event
                String eventName = type.toString().toLowerCase();
                eventName = eventName.replace("_", " ");
                eventName = WordUtils.capitalize(eventName);

                if ( config.isEnabled(type) && ! event.getIsActive()) {   // Activate if the event is not active yet

                    // Start the event
                    event.startEvent();

                    // Log to the console
                    String message = "A timed event has just started: " + eventName + "!";
                    if (consoleConfig.isPositiveGreen())
                    {
                        Bukkit.getConsoleSender().sendMessage("[BetterSleeping] " + ChatColor.LIGHT_PURPLE + message);
                    } else {
                        Bukkit.getConsoleSender().sendMessage("[BetterSleeping] " + message);
                    }
                } // Only report possible event to console if the event is not active, and has not been reported before
                else if (!event.getIsActive() && ( !reportedEvents.containsKey(type) || !reportedEvents.get(type)))
                {
                    // Log to the console
                    String message = "A timed event (" + eventName + ") is active but disabled in events.yml!";
                    ConsoleLogger.logPositive(message, ChatColor.LIGHT_PURPLE);

                    reportedEvents.put(type, true);
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
