package be.dezijwegel.placeholderAPI;

import be.dezijwegel.BetterSleeping;
import be.dezijwegel.events.SleepTracker;
import be.dezijwegel.management.Management;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class BetterSleepingExpansion extends PlaceholderExpansion {

    private BetterSleeping plugin;
    private Management management;
    private SleepTracker sleepTracker;

    /**
     * @param plugin instance of BetterSleeping
     */
    public BetterSleepingExpansion(BetterSleeping plugin, Management management, SleepTracker sleepTracker)
    {
        this.plugin = plugin;
        this.management = management;
        this.sleepTracker = sleepTracker;
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

        if(player == null){
            return "";
        }

        if(identifier.equals("current_sleepers")){
            return Integer.toString( sleepTracker.getNumSleepingPlayers( player.getWorld() ));
        }

        if(identifier.equals("remaining_sleepers")){
            int numNeeded = sleepTracker.getTotalSleepersNeeded(player.getWorld());
            int numCurrent = sleepTracker.getNumSleepingPlayers(player.getWorld());
            return Integer.toString( numNeeded - numCurrent );
        }

        if(identifier.equals("total_needed_sleepers")){
            return Integer.toString( sleepTracker.getTotalSleepersNeeded( player.getWorld() ));
        }

        if(identifier.equals("buffs_amount")){
            return Integer.toString( management.getNumBuffs() );
        }

        if(identifier.equals("night_skip_time")){
            return Integer.toString( management.getConfig().getInt("sleep_delay")/20 );
        }

        if(identifier.equals("sleep_spam_time")){
            return Long.toString( sleepTracker.whenCanPlayerSleep( player.getUniqueId() ));
        }

        if(identifier.equals("receiver")){
            return ChatColor.stripColor( player.getName() );
        }

        return null;
    }
}
