package be.dezijwegel.customEvents;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

public class PlayersDidNotSleepEvent extends BetterSleepingEvent {

    private List<Player> players;

    /**
     * Get a list of players
     * @param players the list of players that did not sleep
     */
    public PlayersDidNotSleepEvent(List<Player> players)
    {
        super();
        this.players = players;
    }


    /**
     * Get the list of players that didn't sleep
     * @return a list of players
     */
    public List<Player> getPlayers()
    {
        return players;
    }

}
