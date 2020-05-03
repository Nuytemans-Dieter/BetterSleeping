package be.dezijwegel.timechange;

import org.bukkit.World;

public class TimeSmooth extends TimeChanger {


    private final static int SPEEDUP_PER_PLAYER = 1;   // How much extra speedup there is per extra sleeping player
    private final static int BASE_SPEEDUP = 5;         // Amount of extra executed every tick
    private final static int MAX_SPEEDUP = 15;         // The highest allowed speedup


    public TimeSmooth(World world) {
        super(world);
    }


    @Override
    public void tick(int numSleeping, int numNeeded) {

        // Redundant negative check, just to be safe
        int extraSleeping = Math.max(numSleeping - numNeeded, 0);
        long newTime = world.getTime() + BASE_SPEEDUP + (extraSleeping * SPEEDUP_PER_PLAYER);

        // Reset any number exceeding the max number of ticks
        newTime = newTime % 24000;

        // If time difference exceeds the max speedup
        if (Math.abs(newTime - world.getTime()) > MAX_SPEEDUP)
            newTime = world.getTime() + MAX_SPEEDUP;

        // make sure no part of the day is skipped
        if (newTime < world.getTime())
            newTime = 0;

        world.setTime(newTime);
    }
}
