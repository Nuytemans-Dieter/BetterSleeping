package be.dezijwegel.bettersleeping.permissions;

import be.betterplugins.core.messaging.logging.BPLogger;
import be.dezijwegel.bettersleeping.hooks.EssentialsHook;
import be.dezijwegel.bettersleeping.configuration.ConfigContainer;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class BypassChecker {

    private final EssentialsHook essentialsHook;
    private final boolean isEnabled;
    private final Set<GameMode> bypassedGamemodes;

    @Inject
    public BypassChecker(ConfigContainer config, EssentialsHook essentialsHook, BPLogger logger)
    {
        YamlConfiguration bypassConfig = config.getBypassing();
        this.isEnabled = bypassConfig.getBoolean("enable_bypassing");

        // Get all bypassed gamemodes from the config file
        this.bypassedGamemodes = new HashSet<>();
        for (GameMode gameMode : GameMode.values())
        {
            String lowerCaseGamemode = gameMode.toString().toLowerCase();
            if (bypassConfig.getBoolean("ignore_" + lowerCaseGamemode))
            {
                this.bypassedGamemodes.add( gameMode );
            }
        }

        logger.log(Level.CONFIG, "Is bypassing enabled? " + isEnabled);
        logger.log(Level.CONFIG, "Ignoring " + bypassedGamemodes.size() + " game modes");

        this.essentialsHook = essentialsHook;
    }


    /**
     * Get whether or not bypass permissions are enabled or disabled
     * @return true of they are enabled
     */
    public boolean isBypassingEnabled()
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
        // Never consider a player bypassed when disabled
        if (!isEnabled)
        {
            return false;
        }

        // Permission based bypassing
        boolean hasPermission       = player.hasPermission("bettersleeping.bypass");
        boolean essentialsBypass    = player.hasPermission("essentials.sleepingignored") && essentialsHook.isHooked();

        // Gamemode based bypassing
        boolean gamemodeBypass      = bypassedGamemodes.contains( player.getGameMode() );

        // State based bypassing (internally handles the config settings)
        boolean isAfk       = essentialsHook.isAfk( player );
        boolean isVanished  = essentialsHook.isVanished( player );

        return hasPermission || essentialsBypass || gamemodeBypass || isAfk || isVanished;
    }

}
