package be.dezijwegel.customEvents;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BetterSleepingEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public BetterSleepingEvent()
    {
        Bukkit.getPluginManager().callEvent(this);
    }


    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
