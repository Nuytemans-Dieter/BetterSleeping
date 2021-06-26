package be.betterplugins.bettersleeping.animation;

import be.betterplugins.bettersleeping.animation.location.IVariableLocation;

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