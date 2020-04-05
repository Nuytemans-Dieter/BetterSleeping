package be.dezijwegel.timedEvents;

import be.dezijwegel.files.Lang;
import be.dezijwegel.management.Management;
import be.dezijwegel.timedEvents.aprilFools.AprilFoolsEventsHandler;
import be.dezijwegel.timedEvents.easter.EasterEventsHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Easter extends Timed {

    EasterEventsHandler eHandler;

    public Easter(Plugin plugin, Management management)
    {
        super(  plugin, management,
                new GregorianCalendar(2020, Calendar.APRIL, 12, 0, 0),    // Start date
                new GregorianCalendar(2020, Calendar.APRIL, 19, 23, 59));   // End date
    }

    @Override
    public void startEvent() {
        eHandler = new EasterEventsHandler(management, plugin);
        plugin.getServer().getPluginManager().registerEvents(eHandler, plugin);
    }

    @Override
    public void stopEvent() {

    }
}
