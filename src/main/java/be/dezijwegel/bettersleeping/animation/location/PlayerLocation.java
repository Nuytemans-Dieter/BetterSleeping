package be.dezijwegel.bettersleeping.animation.location;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerLocation implements IVariableLocation{

    private final Player player;

    public PlayerLocation(Player player)
    {
        this.player = player;
    }

    @Override
    public Location getLocation()
    {
        return player.getLocation();
    }
}