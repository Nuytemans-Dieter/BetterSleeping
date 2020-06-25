package be.dezijwegel.bettersleeping.permissions;

import be.dezijwegel.bettersleeping.hooks.EssentialsHook;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BypassChecker {

    private final EssentialsHook essentialsHook;
    private final boolean isEnabled;
    private final Set<GameMode> bypassedGamemodes;

    /**
     * Creates an object that can check whether or not a player has got bypass permissions
     * @param bypassedGamemodes the list of gamemodes that are bypassed
     */
    public BypassChecker(boolean isEnabled, EssentialsHook essentialsHook, List<GameMode> bypassedGamemodes)
    {
        this.isEnabled = isEnabled;

        // Add all bypassed gamemodes
        this.bypassedGamemodes = new HashSet<>();
        this.bypassedGamemodes.addAll(bypassedGamemodes);

        this.essentialsHook = essentialsHook;
    }


    /**
     * Get whether or not bypass permissions are enabled or disabled
     * @return true of they are enabled
     */
    public boolean isEnabled()
    {
        return isEnabled;
    }


    /**
     * Get the Set of bypassed gamemodes
     * @return the gamemodes that are bypassed
     */
    public Set<GameMode> getBypassedGamemodes()
    {
        return bypassedGamemodes;
    }


    /**
     * Returns whether a player has bypass permissions or not
     * Can be based on a permission node or gamemode
     * @param player the player to be checked
     * @return whether this player should be bypassed or not
     */
    public boolean isPlayerBypassed(Player player)
    {
        // Permission based bypassing
        boolean hasPermission       = player.hasPermission("bettersleeping.bypass") && isEnabled;
        boolean essentialsBypass    = player.hasPermission("essentials.sleepingignored") && essentialsHook.isHooked() && isEnabled;

        // Gamemode based bypassing
        boolean gamemodeBypass      = bypassedGamemodes.contains( player.getGameMode() );

        // State based bypassing (internally handles the config settings)
        boolean isAfk       = essentialsHook.isAfk( player );
        boolean isVanished  = essentialsHook.isVanished( player );

        return hasPermission || essentialsBypass || gamemodeBypass || isAfk || isVanished;
    }

}
