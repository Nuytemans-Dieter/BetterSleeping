package be.betterplugins.bettersleeping.animation.location;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerSleepLocation implements IVariableLocation
{

    private final Player player;

    public PlayerSleepLocation(Player player)
    {
        this.player = player;
    }

    @Override
    public Location getLocation()
    {
        Location location = player.getLocation();
        if (!player.isSleeping())
        {
            location.add(0, 1.5, 0);
        }
        return location;
    }
}