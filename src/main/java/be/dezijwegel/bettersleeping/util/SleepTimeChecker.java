package be.dezijwegel.bettersleeping.util;

import org.bukkit.World;

public class SleepTimeChecker {

    public static boolean isSleepPossible(World world)
    {
        boolean isOverworld = world.getEnvironment() == World.Environment.NORMAL;
        boolean isThundering = world.isThundering();
        long startSleepTime = world.hasStorm() ? 12010 : 12542;
        boolean isNight = world.getTime() >= startSleepTime;

        return isOverworld && (isThundering || isNight);
    }

}
