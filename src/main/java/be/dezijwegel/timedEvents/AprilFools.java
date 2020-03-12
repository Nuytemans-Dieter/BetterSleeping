package be.dezijwegel.timedEvents;

import org.bukkit.Bukkit;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AprilFools extends Timed {

    public AprilFools()
    {
        super(  new GregorianCalendar(2020, Calendar.APRIL,1, 0, 0),   // Start date
                new GregorianCalendar(2020, Calendar.APRIL,8, 23, 59));  // Stop date
    }

    @Override
    public void startEvent() {
        Bukkit.getConsoleSender().sendMessage("AF start");
    }

    @Override
    public void stopEvent() {
        Bukkit.getConsoleSender().sendMessage("AF stop");
    }
}
