//package be.dezijwegel.bettersleeping.listeners;
//
//import be.dezijwegel.bettersleeping.api.TimeSetToDayEvent;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//
//public class TimeSetToDayCounter implements Listener {
//
//    private int counter = 0;
//
//    @EventHandler
//    public void onTimeSetToDay(TimeSetToDayEvent timeSetToDayEvent)
//    {
//        if (timeSetToDayEvent.getCause() == TimeSetToDayEvent.Cause.SLEEPING)
//            counter++;
//    }
//
//    public int resetCounter()
//    {
//        int temp = counter;
//        counter = 0;
//        return temp;
//    }
//
//}
