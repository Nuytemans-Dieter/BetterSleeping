package be.betterplugins.bettersleeping.hooks;

import be.betterplugins.bettersleeping.BetterSleeping;
import be.betterplugins.bettersleeping.listeners.BuffsHandler;
import be.betterplugins.bettersleeping.model.SleepStatus;
import be.betterplugins.bettersleeping.model.sleeping.SleepWorldManager;
import com.google.inject.Inject;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PapiExpansion extends PlaceholderExpansion
{

    private final BetterSleeping plugin;
    private final SleepWorldManager sleepWorldManager;
    private final BuffsHandler buffsHandler;

    /**
     * @param plugin instance of BetterSleeping
     */
    @Inject
    public PapiExpansion(BetterSleeping plugin, SleepWorldManager sleepWorldManager, BuffsHandler buffsHandler)
    {
        this.plugin = plugin;
        this.sleepWorldManager = sleepWorldManager;
        this.buffsHandler = buffsHandler;
    }


    /**
     * Makes sure this expansion is supported by PAPI
     * @return true to persist through reloads
     */
    @Override
    public boolean persist()
    {
        return true;
    }


    /**
     * @return Always true since it's an internal class
     */
    @Override
    public boolean canRegister()
    {
        return true;
    }


    @Override
    public String getAuthor()
    {
        return plugin.getDescription().getAuthors().toString();
    }


    /**
     * The unique ID for BetterSleeping placeholders
     * @return always "bettersleeping"
     */
    @Override
    public String getIdentifier()
    {
        return "bettersleeping";
    }

    /**
     * @return the plugin version as a String
     */
    @Override
    public String getVersion()
    {
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
    public String onPlaceholderRequest(Player player, @NotNull String identifier)
    {

        // Only allow calculating values if a Player object is provided
        if (player == null)
            return null;

        // Get an up-to-date status on this world (if enabled)
        SleepStatus sleepStatus = sleepWorldManager.getSleepStatus(player.getWorld());

        // BetterSleeping is disabled in this world so returning null to indicate a faulty identifier
        if (sleepStatus == null)
            return null;

        // Make sure the identifier matches, regardless of capitalization used
        identifier = identifier.toLowerCase();
        switch (identifier)
        {
            case "bs_num_sleeping":
                return "" + sleepStatus.getNumSleepers();
            case "bs_total_needed":
                return "" + sleepStatus.getNumNeeded();
            case "bs_extra_needed":
                return "" + Math.max(sleepStatus.getNumNeeded() - sleepStatus.getNumSleepers(),0);
            case "bs_num_in_world":
                return "" + sleepStatus.getNumPlayersInWorld();
            case "bs_buffs_amount":
                return "" + buffsHandler.getBuffs().size();
            case "bs_debuffs_amount":
                return "" + buffsHandler.getDebuffs().size();
            case "bs_dayspeed":
                return "" + sleepStatus.getDaySpeedup();
            case "bs_nightspeed":
                return "" + sleepStatus.getNightSpeedup();
            case "bs_sleepspeed":
                return "" + sleepStatus.getSleepSpeedup();
        }

        // PAPI documentation suggests returning null for faulty identifiers
        return null;
    }
}
