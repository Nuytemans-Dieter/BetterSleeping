package be.dezijwegel.placeholderAPI;

import be.dezijwegel.BetterSleeping;
import be.dezijwegel.management.Management;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class BetterSleepingExpansion extends PlaceholderExpansion {

    private BetterSleeping plugin;
    private Management management;

    /**
     * @param plugin instance of BetterSleeping
     */
    public BetterSleepingExpansion(BetterSleeping plugin, Management management)
    {
        this.plugin = plugin;
        this.management = management;
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
     * @param  player
     *         A {@link org.bukkit.Player Player}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        if(player == null){
            return "";
        }

        // %someplugin_placeholder1%
        if(identifier.equals("placeholder1")){
            return plugin.getConfig().getString("placeholder1", "value doesnt exist");
        }

        // %someplugin_placeholder2%
        if(identifier.equals("placeholder2")){
            return plugin.getConfig().getString("placeholder2", "value doesnt exist");
        }

        // We return null if an invalid placeholder (f.e. %someplugin_placeholder3%)
        // was provided
        return null;
    }
}
