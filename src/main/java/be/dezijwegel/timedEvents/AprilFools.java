package be.dezijwegel.timedEvents;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AprilFools extends Timed {

    public AprilFools()
    {
        super(  new GregorianCalendar(2020, Calendar.APRIL,1),   // Start date
                new GregorianCalendar(2013, Calendar.APRIL,8));  // Stop date
    }

    @Override
    public void startEvent() {

    }

    @Override
    public void stopEvent() {

    }
}
