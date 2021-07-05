package be.betterplugins.bettersleeping.animation.location;

import org.bukkit.Location;

public class StaticLocation implements IVariableLocation
{

    private final Location location;

    public StaticLocation(Location location)
    {
        this.location = location;
    }

    @Override
    public Location getLocation() {
        return location;
    }
}