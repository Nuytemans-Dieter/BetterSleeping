package be.betterplugins.bettersleeping.animation;

import be.betterplugins.bettersleeping.animation.location.IVariableLocation;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SleepingAnimation extends Animation
{

    private final Animation animation;
    private final long delay;
    private final JavaPlugin plugin;

    public SleepingAnimation(Animation animation, long delayMilliseconds, JavaPlugin plugin)
    {
        this.animation = animation;
        this.delay = delayMilliseconds;
        this.plugin = plugin;
    }

    @Override
    public void startAnimation(IVariableLocation variableLocation)
    {
        super.startAnimation(variableLocation);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
        {

            while (super.isPlaying)
            {

                this.animation.startAnimation( variableLocation );

                try
                {
                    Thread.sleep( delay );
                }
                catch (InterruptedException e)
                {
                    super.isPlaying = false;
                    this.stopAnimation();
                }
            }
        });
    }
}
