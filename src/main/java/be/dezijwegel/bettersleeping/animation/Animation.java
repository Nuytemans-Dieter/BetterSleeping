package be.dezijwegel.bettersleeping.animation;

import be.dezijwegel.bettersleeping.animation.location.IVariableLocation;
import org.bukkit.Location;

public abstract class Animation {

    private final IVariableLocation location;
    protected boolean isPlaying = false;

    public Animation(IVariableLocation location)
    {
        this.location = location;
    }

    public Location getOrigin()
    {
        return location.getLocation();
    }

    public void startAnimation()
    {
        this.isPlaying = true;
    }

    public void stopAnimation()
    {
        this.isPlaying = false;
    }

}