package be.dezijwegel.bettersleeping.timechange;

import org.bukkit.World;

public class TimeSetter extends TimeChanger {

    private final int sleepDelay;

    private int counter = 0;
    private long oldTime;


    public TimeSetter(World world, int delay)
    {
        super(world);
        oldTime = world.getTime();

        // * 20 to convert seconds into ticks
        sleepDelay = 20 * delay;
    }

    @Override
    public TimeChangeType getType()
    {
        return TimeChangeType.SETTER;
    }

    @Override
    public void tick(int numSleeping, int numNeeded) {

        // Handle the counter
        long newTime = world.getTime();
        counter = (newTime < oldTime + 5) ? counter + 1 : 0;
        oldTime = newTime;

        // Handle the time set mechanic when the counter reaches their set value
        if (counter >= sleepDelay) {

            // Reset the counter
            counter = 0;

            if (world.isThundering())
            {
                world.setThundering(false);
                world.setStorm(false);
            }

            if(newTime >= TIME_NIGHT)
            {
                world.setTime(TIME_MORNING);
            }
            else if (newTime >= TIME_RAIN_NIGHT && world.hasStorm())
            {
                world.setTime(TIME_MORNING);
                world.setStorm(false);
            }
        }

    }
}
