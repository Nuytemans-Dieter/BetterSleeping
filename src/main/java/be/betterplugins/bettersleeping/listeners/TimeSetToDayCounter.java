package be.betterplugins.bettersleeping.listeners;

import be.betterplugins.bettersleeping.api.BecomeDayEvent;
import be.betterplugins.bettersleeping.api.BecomeDayEvent.Cause;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TimeSetToDayCounter implements Listener {

    private int counter = 0;

    @Inject
    @Singleton
    public TimeSetToDayCounter() {}

    @EventHandler
    public void onTimeSetToDay(BecomeDayEvent event)
    {
        if (event.getCause() == Cause.SLEEPING)
            counter++;
    }

    public int resetCounter()
    {
        int temp = counter;
        counter = 0;
        return temp;
    }

}
