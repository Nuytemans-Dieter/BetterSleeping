package be.dezijwegel.bettersleeping.animation;

import be.dezijwegel.bettersleeping.BetterSleeping;
import be.dezijwegel.bettersleeping.animation.location.IVariableLocation;
import org.bukkit.Bukkit;

public class SleepingAnimation extends Animation {

    private final Animation animation;
    private final long delay;

    public SleepingAnimation(Animation animation, long delayMilliseconds)
    {
        this.animation = animation;
        this.delay = delayMilliseconds;
    }

    @Override
    public void startAnimation(IVariableLocation variableLocation) {
        super.startAnimation(variableLocation);

        Bukkit.getScheduler().runTaskAsynchronously(BetterSleeping.getInstance(), () -> {

            while (super.isPlaying)
            {

                this.animation.startAnimation( variableLocation );

                try {
                    Thread.sleep( delay );
                } catch (InterruptedException e) {
                    super.isPlaying = false;
                }
            }
        });
    }
}
