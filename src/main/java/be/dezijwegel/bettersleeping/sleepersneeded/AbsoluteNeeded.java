//package be.dezijwegel.bettersleeping.sleepersneeded;
//
//import be.dezijwegel.bettersleeping.interfaces.SleepersNeededCalculator;
//import org.bukkit.World;
//import org.bukkit.configuration.file.YamlConfiguration;
//
//import javax.inject.Inject;
//import javax.inject.Named;
//
//public class AbsoluteNeeded implements SleepersNeededCalculator
//{
//
//    // Constants
//    private final int numNeeded;
//
//    @Inject
//    public AbsoluteNeeded(@Named("sleeping_settings") YamlConfiguration config)
//    {
//        this.numNeeded = config.getInt("absolute.needed");
//    }
//
//    /**
//     * Get the required amount of sleeping players in this world
//     * @return the absolute amount of required sleepers
//     */
//    @Override
//    public int getNumNeeded(World world)
//    {
//        return numNeeded;
//    }
//
//    @Override
//    public int getSetting()
//    {
//        return numNeeded;
//    }
//
//}
