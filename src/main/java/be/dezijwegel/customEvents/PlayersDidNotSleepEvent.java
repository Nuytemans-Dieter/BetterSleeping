package be.dezijwegel.customEvents;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

public class PlayersDidNotSleepEvent extends Event {


    private List<Player> players;
    private static final HandlerList HANDLERS = new HandlerList();


    /**
     * Get a list of players
     * @param players the list of players that did not sleep
     */
    public PlayersDidNotSleepEvent(List<Player> players)
    {
        this.players = players;
        Bukkit.getPluginManager().callEvent(this);
    }


    /**
     * Get the list of players that didn't sleep
     * @return a list of players
     */
    public List<Player> getPlayers()
    {
        return players;
    }


    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }


    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
