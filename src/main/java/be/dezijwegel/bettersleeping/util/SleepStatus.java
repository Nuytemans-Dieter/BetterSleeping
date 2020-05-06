package be.dezijwegel.bettersleeping.util;

import be.dezijwegel.bettersleeping.timechange.TimeChanger;
import org.bukkit.World;

public class SleepStatus {

    private final int numSleeping;
    private final int totalNeeded;
    private final int numLeft;

    private final World world;

    private final TimeChanger.TimeChangeType type;
    private final String settingMessage;


    public SleepStatus(int numSleeping, int totalNeeded, World world, TimeChanger.TimeChangeType type, String settingMessage)
    {
        this.numSleeping = numSleeping;
        this.totalNeeded = totalNeeded;
        this.numLeft = (totalNeeded > numSleeping) ? totalNeeded - numSleeping : 0;

        this.world = world;

        this.type = type;
        this.settingMessage = settingMessage;
    }


    /**
     * Get the amount of current sleeping players
     * @return the amount of required sleep
     */
    public int getNumSleeping()
    {
        return numSleeping;
    }


    /**
     * Get the total amount of required sleeping players
     * @return the total needed in this world
     */
    public int getTotalNeeded()
    {
        return totalNeeded;
    }

    /**
     * Get the amount of extra required sleeping players
     * @return the extra sleepers requirement
     */
    public int getNumLeft()
    {
        return numLeft;
    }

    /**
     * Get the world of which this is the sleep status
     * @return the World
     */
    public World getWorld()
    {
        return world;
    }

    /**
     * The type of time change: smooth or setter
     * @return TimeChangeType from the TimeChanger class
     */
    public TimeChanger.TimeChangeType getType()
    {
        return type;
    }

    /**
     * Get the percentage, or absolute amount of required sleepers in a message. Ready-made for user visibility
     * @return a string explaining the current setting
     */
    public String getSettingMessage()
    {
        return settingMessage;
    }

}
