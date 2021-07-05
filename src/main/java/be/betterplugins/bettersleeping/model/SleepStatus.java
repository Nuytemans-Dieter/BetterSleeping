package be.betterplugins.bettersleeping.model;

public class SleepStatus
{
    private final int numSleepers;
    private final int numNeeded;
    private final int numPlayersInWorld;

    private final double daySpeedup;
    private final double nightSpeedup;
    private final double sleepSpeedup;

    public SleepStatus(int numSleepers, int numNeeded, int numPlayersInWorld, double daySpeedup, double nightSpeedup, double sleepSpeedup)
    {
        this.numSleepers = numSleepers;
        this.numNeeded = numNeeded;
        this.numPlayersInWorld = numPlayersInWorld;
        this.daySpeedup = daySpeedup;
        this.nightSpeedup = nightSpeedup;
        this.sleepSpeedup = sleepSpeedup;
    }

    public int getNumSleepers()
    {
        return numSleepers;
    }

    public int getNumNeeded()
    {
        return numNeeded;
    }

    public int getNumPlayersInWorld()
    {
        return numPlayersInWorld;
    }

    public double getDaySpeedup()
    {
        return daySpeedup;
    }

    public double getNightSpeedup()
    {
        return nightSpeedup;
    }

    public double getSleepSpeedup()
    {
        return sleepSpeedup;
    }
}
