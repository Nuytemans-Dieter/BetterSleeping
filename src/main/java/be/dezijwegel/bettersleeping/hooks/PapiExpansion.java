package be.dezijwegel.bettersleeping.hooks;

import be.dezijwegel.bettersleeping.BetterSleeping;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PapiExpansion extends PlaceholderExpansion {


    private final BetterSleeping plugin;


    /**
     * @param plugin instance of BetterSleeping
     */
    public PapiExpansion(BetterSleeping plugin)
    {
        this.plugin = plugin;
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

        }

        if(identifier.equals("remaining_sleepers")){

        }

        if(identifier.equals("total_needed_sleepers")){

        }

        if(identifier.equals("buffs_amount")){

        }

        if(identifier.equals("night_skip_time")){

        }

        if(identifier.equals("sleep_spam_time")){

        }

        if(identifier.equals("receiver")){

        }

        return null;
    }
}
