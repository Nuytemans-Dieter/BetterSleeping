package be.dezijwegel.bettersleeping.timechange;

import org.bukkit.World;

public class TimeSmooth extends TimeChanger {


    private final int baseSpeedup;      // Amount of extra executed every tick
    private final int speedupPerPlayer; // How much extra speedup there is per extra sleeping player
    private final int maxSpeedup;       // The highest allowed speedup

    public TimeSmooth(World world, int baseSpeedup, int speedupPerPlayer, int maxSpeedup) {
        super(world);

        this.baseSpeedup      = baseSpeedup;
        this.speedupPerPlayer = speedupPerPlayer;
        this.maxSpeedup       = maxSpeedup;
    }


    @Override
    public TimeChangeType getType()
    {
        return TimeChangeType.SMOOTH;
    }

    @Override
    public void tick(int numSleeping, int numNeeded) {

        // Redundant negative check, just to be safe
        int extraSleeping = Math.max(numSleeping - numNeeded, 0);
        long newTime = world.getTime() + baseSpeedup + (extraSleeping * speedupPerPlayer);

        // Reset any number exceeding the max number of ticks
        newTime = newTime % 24000;

        // If time difference exceeds the max speedup
        if (Math.abs(newTime - world.getTime()) > maxSpeedup)
            newTime = world.getTime() + maxSpeedup;

        // make sure no part of the day is skipped
        if (newTime < world.getTime())
            newTime = 0;

        // Sometimes players are kicked out of bed before time == 0, this happens around time 23460
        if (newTime > 23450)
            newTime = 0;

        wasSetToDay = newTime == 0;

        world.setTime(newTime);
    }
}
