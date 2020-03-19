package be.dezijwegel.timedEvents;

import be.dezijwegel.files.Lang;
import be.dezijwegel.interfaces.TimedEvent;
import be.dezijwegel.management.Management;
import org.bukkit.plugin.Plugin;

import java.util.Calendar;

public class Timed implements TimedEvent {

    Plugin plugin;          // Shared with subclasses and same package
    Management management;  // Shared with subclasses and same package

    private Calendar start;
    private Calendar end;

    private boolean isActive;

    public Timed(Plugin plugin, Management management, Calendar start, Calendar end)
    {
        this.plugin = plugin;
        this.management = management;

        this.start = start;
        this.end   = end;

        isActive = false;
    }

    @Override
    public Calendar getStartDate() {
        return start;
    }

    @Override
    public Calendar getEndDate() {
        return end;
    }

    @Override
    public void startEvent() {
        isActive = true;
    }

    @Override
    public void stopEvent() {
        isActive = false;
    }

    @Override
    public boolean getIsActive() {
        return isActive;
    }
}
