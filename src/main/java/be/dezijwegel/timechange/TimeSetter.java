package be.dezijwegel.timechange;

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
    public void tick() {
        long newTime = world.getTime();

        if (newTime < oldTime + 5)
            counter++;
        else
            counter = 0;

        if (counter == SLEEP_DELAY) {
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
