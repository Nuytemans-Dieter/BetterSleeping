package be.betterplugins.bettersleeping.model;

public class SleepStatus
{
    private final int numSleepers;
    private final int numNeeded;
    private final int numMissing;
    private final int numPlayersInWorld;

    private final double daySpeedup;
    private final double nightSpeedup;
    private final double sleepSpeedup;

    public SleepStatus(int numSleepers, int numNeeded, int numPlayersInWorld, double daySpeedup, double nightSpeedup, double sleepSpeedup)
    {
        this.numSleepers = numSleepers;
        this.numNeeded = numNeeded;
        this.numMissing = Math.max(0, numNeeded - numSleepers);
        this.numPlayersInWorld = numPlayersInWorld;
        this.daySpeedup = daySpeedup;
        this.nightSpeedup = nightSpeedup;
        this.sleepSpeedup = sleepSpeedup;
    }

    /**
     * Get the amount of players that are sleeping in this world
     */
    public int getNumSleepers()
    {
        return numSleepers;
    }

    /**
     * Get the amount of required sleepers in this world before the night is skipped
     */
    public int getNumNeeded()
    {
        return numNeeded;
    }

    /**
     * Get the amount of players that are required to meet the criteria to skip the night
     */
    public int getNumMissing()
    {
        return numMissing;
    }

    /**
     * Get the amount of players in this world
     */
    public int getNumPlayersInWorld()
    {
        return numPlayersInWorld;
    }

    /**
     * Get the time speedup factor during the day
     */
    public double getDaySpeedup()
    {
        return daySpeedup;
    }

    /**
     * Get the time speedup factor during the night
     */
    public double getNightSpeedup()
    {
        return nightSpeedup;
    }

    /**
     * Get the time speedup factor when enough players are sleeping
     */
    public double getSleepSpeedup()
    {
        return sleepSpeedup;
    }
}
