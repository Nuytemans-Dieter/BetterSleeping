package be.dezijwegel.bettersleeping.hooks;

import be.dezijwegel.bettersleeping.BetterSleeping;
import be.dezijwegel.bettersleeping.events.handlers.BedEventHandler;
import be.dezijwegel.bettersleeping.events.handlers.BuffsHandler;
import be.dezijwegel.bettersleeping.interfaces.SleepersNeededCalculator;
import be.dezijwegel.bettersleeping.sleepersneeded.AbsoluteNeeded;
import be.dezijwegel.bettersleeping.sleepersneeded.PercentageNeeded;
import be.dezijwegel.bettersleeping.util.ConfigLib;
import be.dezijwegel.bettersleeping.util.SleepStatus;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PapiExpansion extends PlaceholderExpansion{

    private final BetterSleeping plugin;
    private final BedEventHandler bedEventHandler;
    private final BuffsHandler buffsHandler;

    /**
     * @param plugin instance of BetterSleeping
     */
    public PapiExpansion(BetterSleeping plugin, BedEventHandler bedEventHandler, BuffsHandler buffsHandler)
    {
        this.plugin = plugin;
        this.bedEventHandler = bedEventHandler;
        this.buffsHandler = buffsHandler;
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

        // Only allow calculating values if a Player object is provided
        if (player == null)
            return null;

        SleepStatus sleepStatus = bedEventHandler.getSleepStatus(player.getWorld());

        // BetterSleeping is disabled in this world so returning null to indicate a faulty identifier
        if (sleepStatus == null)
            return null;

        // Make sure the identifier matches, regardless of capitalization used
        identifier = identifier.toLowerCase();

        switch (identifier)
        {
            case "bs_time_changing_type":
                return "" + sleepStatus.getType().name().toLowerCase();
            case "bs_num_sleeping":
                return "" + sleepStatus.getNumSleeping();
            case "bs_total_needed":
                return "" + sleepStatus.getTotalNeeded();
            case "bs_extra_needed":
                return "" + sleepStatus.getNumLeft();
            case "bs_buffs_amount":
                return "" + buffsHandler.getBuffs().size();
            case "bs_debuffs_amount":
                return "" + buffsHandler.getDebuffs().size();
        }

        // PAPI documentation suggests returning null for faulty identifiers
        return null;
    }
}
