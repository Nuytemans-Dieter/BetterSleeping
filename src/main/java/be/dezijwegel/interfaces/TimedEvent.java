package be.dezijwegel.interfaces;

import java.util.Calendar;
import java.util.Date;

public interface TimedEvent {

    Calendar getStartDate();    // Get the date on which this event starts
    Calendar getEndDate();      // Get the date on which this event ends

    void startEvent();          // Start the current event
    void stopEvent();           // Stop the current event

    boolean getIsActive();      // Check whether or not the event is currently active
}
