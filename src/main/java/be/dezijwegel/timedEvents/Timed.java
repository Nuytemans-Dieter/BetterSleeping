package be.dezijwegel.timedEvents;

import be.dezijwegel.interfaces.TimedEvent;

import java.util.Calendar;

public class Timed implements TimedEvent {

    private Calendar start;
    private Calendar end;

    private boolean isActive;

    public Timed(Calendar start, Calendar end)
    {
        this.start = start;
        this.end   = end;
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
