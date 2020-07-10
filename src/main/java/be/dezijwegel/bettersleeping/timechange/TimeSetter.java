package be.dezijwegel.bettersleeping.timechange;

import org.bukkit.World;

public class TimeSetter extends TimeChanger {
    private final int sleepDelay;
    private int counter = 0;
    private long oldTime;

    public TimeSetter(World world, int delay) {
        super(world);
        this.oldTime = world.getTime();

        // * 20 to convert seconds into ticks
        this.sleepDelay = 20 * delay;
    }

    @Override
    public TimeChangeType getType() {
        return TimeChangeType.SETTER;
    }

    @Override
    public void tick(int numSleeping, int numNeeded) {
        long currentTime = world.getTime();

        // Only increment counter when this tick is at most 5 ticks from the previous one. Reset otherwise.
        this.counter = (currentTime < this.oldTime + 5) ? this.counter + 1 : 0;
        this.oldTime = currentTime;

        // Wait for the sleep delay before doing anything.
        if (this.counter < this.sleepDelay) {
            return;
        }

        // Reset the counter.
        this.counter = 0;

        // Perform night skip.
        world.setStorm(false);
        world.setTime(TimeChanger.TIME_MORNING);

        // Mark time as set to day for parent
        this.wasSetToDay = true;
    }
}
