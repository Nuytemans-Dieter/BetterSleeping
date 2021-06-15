package be.dezijwegel.bettersleeping.util;

import org.bukkit.World;

public class TimeUtil
{

    public final static long DAY_DURATION = 14000;          // Default day duration (in ticks, 20tps * 700s)
    public final static long NIGHT_DURATION = 10000;        // Default night duration (in ticks, 20tps * 500s)

    public final static int TIME_NIGHT_START = 13000;       // Starting time of the night
    public final static int TIME_NIGHT_END = 23000;         // Ending time of the night

    public final static int BED_TIME_MORNING = 0;           // Morning time
    public final static int BED_TIME_RAIN_NIGHT = 12010;    // Time at which players can enter their bed during rain
    public final static int BED_TIME_NIGHT = 12542;         // Time at which players can enter their bed

    public static boolean isSleepPossible(World world)
    {
        boolean isOverworld = world.getEnvironment() == World.Environment.NORMAL;
        boolean isThundering = world.isThundering();
        long startSleepTime = world.hasStorm() ? 12010 : 12542;
        boolean isNight = world.getTime() >= startSleepTime;

        return isOverworld && (isThundering || isNight);
    }

    public static boolean isDayTime(World world)
    {
        return world.getTime() < TIME_NIGHT_START || world.getTime() >= TIME_NIGHT_END;
    }
}
