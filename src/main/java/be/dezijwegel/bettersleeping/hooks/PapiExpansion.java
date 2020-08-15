package be.dezijwegel.bettersleeping.hooks;

import be.dezijwegel.bettersleeping.BetterSleeping;
import be.dezijwegel.bettersleeping.interfaces.SleepersNeededCalculator;
import be.dezijwegel.bettersleeping.sleepersneeded.AbsoluteNeeded;
import be.dezijwegel.bettersleeping.sleepersneeded.PercentageNeeded;
import be.dezijwegel.bettersleeping.util.ConfigLib;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PapiExpansion extends PlaceholderExpansion {


    private final BetterSleeping plugin;
    private ConfigLib sleeping;

    /**
     * @param plugin instance of BetterSleeping
     */
    public PapiExpansion(BetterSleeping plugin,ConfigLib lib)
    {
        this.plugin = plugin;
        this.lib = lib;
    }


    /**
     * Makes sure this expansion is supported by PAPI
     * @return true to persist through reloads
     */
    @Override
    public boolean persist(){
        return true;
    }


    /**
     * @return Always true since it's an internal class
     */
    @Override
    public boolean canRegister(){
        return true;
    }


    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }


    /**
     * The unique ID for BetterSleeping placeholders
     * @return always "bettersleeping"
     */
    @Override
    public String getIdentifier() {
        return "bettersleeping";
    }

    /**
     * @return the plugin version as a String
     */
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    /**
     * This is the method called when a placeholder with our identifier
     * is found and needs a value.
     * @param  player The involved player
     * @param  identifier The requested replacement
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        // Total Sleep percentage.
        if(identifier.equals("total_sleep_percentage_needed")){
            String counter = sleeping.getConfiguration().getString("sleeper_counter");
            if (counter != null && counter.equalsIgnoreCase("percentage"))
            {
                int needed = sleeping.getConfiguration().getInt("percentage.needed");
                return Integer.toString(needed);
            }
        }

        return null;
    }
}
