package be.dezijwegel.bettersleeping.animation;

import be.dezijwegel.bettersleeping.animation.location.IVariableLocation;
import org.bukkit.Location;

public abstract class Animation {

    protected boolean isPlaying = false;

    public Animation() {}

    public void startAnimation(IVariableLocation variableLocation)
    {
        this.isPlaying = true;
    }

    public void stopAnimation()
    {
        this.isPlaying = false;
    }

}