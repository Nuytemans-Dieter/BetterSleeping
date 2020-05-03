package be.dezijwegel.timechange;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class TimeSetter extends TimeChanger {

    private final static int SLEEP_DELAY = 20;

    private int counter = 0;
    private long oldTime;


    public TimeSetter(World world)
    {
        super(world);
        oldTime = world.getTime();
    }

    @Override
    public void tick(int numSleeping, int numNeeded) {

        // Handle the counter
        long newTime = world.getTime();
        counter = (newTime < oldTime + 5) ? counter + 1 : 0;
        oldTime = newTime;

        // Handle the time set mechanic when the counter reaches their set value
        if (counter >= SLEEP_DELAY) {

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
