package be.dezijwegel.timedEvents;

import be.dezijwegel.timedEvents.aprilFools.AprilFoolsEventsHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AprilFools extends Timed {

    AprilFoolsEventsHandler afHandler;

    public AprilFools(Plugin plugin)
    {
        /*super(  plugin,
                new GregorianCalendar(2020, Calendar.APRIL,1, 0, 0),     // Start date
                new GregorianCalendar(2020, Calendar.APRIL,8, 23, 59));  */// Stop date
        super(  plugin,
                new GregorianCalendar(2020, Calendar.MARCH,1, 0, 0),     // Start date
                new GregorianCalendar(2020, Calendar.APRIL,8, 23, 59));  // Stop date
    }

    @Override
    public void startEvent() {
        // Creeper hissing sound prank
        afHandler = new AprilFoolsEventsHandler(plugin);
        plugin.getServer().getPluginManager().registerEvents(afHandler, plugin);
    }

    @Override
    public void stopEvent() {
        // Remove the creeper hissing effect
        HandlerList.unregisterAll(afHandler);
    }
}
