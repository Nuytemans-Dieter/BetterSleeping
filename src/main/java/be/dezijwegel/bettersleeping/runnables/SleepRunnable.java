package be.dezijwegel.bettersleeping.runnables;

import be.dezijwegel.bettersleeping.Model.SleepWorld;
import be.dezijwegel.bettersleeping.sleepersneeded.ISleepersCalculator;
import be.dezijwegel.bettersleeping.util.TimeUtil;
import org.bukkit.scheduler.BukkitRunnable;

public class SleepRunnable extends BukkitRunnable
{

    private final SleepWorld sleepWorld;
    private long lastTime;

    public SleepRunnable(SleepWorld sleepWorld)
    {
        this.sleepWorld = sleepWorld;
        lastTime = sleepWorld.getTime();
    }

    @Override
    public void run()
    {
        // Check the passed time
        long currentTime = sleepWorld.getTime();

        // Count sleepers
        int numSleepers = sleepWorld.getSleepingPlayersInWorld().size();

        // Check num needed
        int numNeeded = sleepWorld.getNumNeeded();

        // TODO Handle custom day/night length + night skipping
        //use below + day/night length settings
        TimeUtil.isDayTime( this.sleepWorld.getWorld() );
        // Set the correct time
        this.sleepWorld.setTime( currentTime + 1 );
    }
}
