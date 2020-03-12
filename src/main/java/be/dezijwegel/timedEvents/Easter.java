package be.dezijwegel.timedEvents;

import org.bukkit.Bukkit;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Easter extends Timed {

    public Easter()
    {
        super(  new GregorianCalendar(2020, Calendar.APRIL, 12, 0, 0),    // Start date
                new GregorianCalendar(2020, Calendar.APRIL, 19, 23, 59));   // End date
    }

    @Override
    public void startEvent() {
        Bukkit.getConsoleSender().sendMessage("EASTER start");
    }

    @Override
    public void stopEvent() {
        Bukkit.getConsoleSender().sendMessage("EASTER stop");
    }
}
