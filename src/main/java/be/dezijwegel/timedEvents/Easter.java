package be.dezijwegel.timedEvents;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Easter extends Timed {

    public Easter()
    {
        super(  new GregorianCalendar(2020, Calendar.APRIL, 12),    // Start date
                new GregorianCalendar(2020, Calendar.APRIL, 19));   // End date
    }

    @Override
    public void startEvent() {

    }

    @Override
    public void stopEvent() {

    }
}
