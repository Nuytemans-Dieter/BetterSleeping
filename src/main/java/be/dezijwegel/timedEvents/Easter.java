package be.dezijwegel.timedEvents;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Easter extends Timed {

    public Easter(Plugin plugin)
    {
        super(  plugin,
                new GregorianCalendar(2020, Calendar.APRIL, 12, 0, 0),    // Start date
                new GregorianCalendar(2020, Calendar.APRIL, 19, 23, 59));   // End date
    }

    @Override
    public void startEvent() {

    }

    @Override
    public void stopEvent() {

    }
}
