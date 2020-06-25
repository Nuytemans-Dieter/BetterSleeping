package be.dezijwegel.bettersleeping.hooks;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class EssentialsHook {


    private final boolean isHooked;
    private final boolean isAfkIgnored;
    private final long minAfkMilliseconds;
    private final boolean isVanishedIgnored;
    private final @Nullable Essentials essentials;


    /**
     * A class for interaction through a possible Essentials hook
     * This can be safely used when Essentials is not installed
     */
    public EssentialsHook(boolean isAfkIgnored, boolean isVanishedIgnored, int minAfkSeconds)
    {
        essentials = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
        isHooked = essentials != null;

        this.isAfkIgnored = isAfkIgnored;
        this.isVanishedIgnored = isVanishedIgnored;
        this.minAfkMilliseconds = minAfkSeconds * 1000;
    }


    /**
     * Check if this player's afk time is equal or bigger than the one specified in the settings
     *
     * @param player the player to be checked
     * @return true if the player has been afk for long enough, false otherwise
     */
    private boolean isMinAfkTimeOkay(Player player)
    {
        long since = essentials.getUser(player).getAfkSince();
        long now = System.currentTimeMillis();

        // Since == 0 means that the user is not afk!
        return since != 0 && (now - since >= minAfkMilliseconds);
    }


    /**
     * Check whether Essentials is installed on this server
     * @return true if Essentials is installed and hooked, false otherwise
     */
    public boolean isHooked()
    {
        return isHooked;
    }


    /**
     * Get whether or not a player is afk
     * Will always return false if Essentials is not hooked
     * @param player the player to be checked
     * @return whether the player is afk
     */
    public boolean isAfk(Player player)
    {
        return isHooked && isAfkIgnored && essentials!=null && essentials.getUser(player).isAfk() && isMinAfkTimeOkay(player);
    }


    /**
     * Gets whether or not a player is hidden according to Essentials
     * @param player the player to be checked
     * @return true if the player is vanished, false if not or if Essentials is not installed
     */
    public boolean isVanished(Player player)
    {
        return isHooked && isVanishedIgnored && essentials!=null && essentials.getUser(player).isHidden();
    }

}
