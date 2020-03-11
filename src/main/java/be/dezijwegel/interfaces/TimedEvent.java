package be.dezijwegel.interfaces;

import java.util.Date;

public interface TimedEvent {

    Date getStartDate();
    Date getEndDate();

    void startEvent();
    void stopEvent();
}
