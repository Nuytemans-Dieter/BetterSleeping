package be.dezijwegel.timechange;

import org.bukkit.World;

public class TimeSmooth extends TimeChanger {


    private final static int SPEEDUP_PER_PLAYER = 1;   // How much extra speedup there is per extra sleeping player
    private final static int BASE_SPEEDUP = 5;         // Amount of extra executed every tick


    public TimeSmooth(World world) {
        super(world);
    }


    @Override
    public void tick(int numSleeping, int numNeeded) {
        // Redundant negative check, just to be safe
        int extraSleeping = Math.max(numSleeping - numNeeded, 0);
        world.setTime(world.getTime() + BASE_SPEEDUP + (extraSleeping * SPEEDUP_PER_PLAYER));
    }
}
