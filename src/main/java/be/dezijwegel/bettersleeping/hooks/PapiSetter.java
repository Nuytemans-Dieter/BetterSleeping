package be.dezijwegel.bettersleeping.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PapiSetter {

    // Contains whether or not placeholderAPI is installed/enabled on this server
    private final boolean hasPlaceholderAPI;


    public PapiSetter()
    {
        hasPlaceholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }


    /**
     * Replaces all PAPI placeholders if PAPI is installed on this server
     * Simply returns its input when PAPI is not enabled on this server
     * @parma player the player to whom these placeholders are to be computed againts
     * @param message the message where placeholders are to be replaced
     * @return the message but placeholders are replaced in this String (if PAPI is enabled)
     */
    public String replacePlaceholders(Player player, String message)
    {
        return hasPlaceholderAPI ? PlaceholderAPI.setPlaceholders(player, message) : message;
    }

}
