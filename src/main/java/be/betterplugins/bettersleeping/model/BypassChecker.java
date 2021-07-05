package be.betterplugins.bettersleeping.model;

import be.betterplugins.core.messaging.logging.BPLogger;
import be.betterplugins.bettersleeping.hooks.EssentialsHook;
import be.betterplugins.bettersleeping.configuration.ConfigContainer;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

@Singleton
public class BypassChecker
{

    private final EssentialsHook essentialsHook;
    private final boolean isEnabled;
    private final Set<GameMode> bypassedGameModes;

    @Inject
    public BypassChecker(ConfigContainer config, EssentialsHook essentialsHook, BPLogger logger)
    {
        YamlConfiguration bypassConfig = config.getBypassing();
        this.isEnabled = bypassConfig.getBoolean("enable_bypassing");

        // Get all bypassed gamemodes from the config file
        Set<GameMode> bypassedGameModesTemp = new HashSet<>();
        for (GameMode gameMode : GameMode.values())
        {
            String lowerCaseGameMode = gameMode.toString().toLowerCase();
            if (bypassConfig.getBoolean("ignore_" + lowerCaseGameMode))
            {
                bypassedGameModesTemp.add( gameMode );
            }
        }
        this.bypassedGameModes = Collections.unmodifiableSet( bypassedGameModesTemp );

        logger.log(Level.CONFIG, "Is bypassing enabled? " + isEnabled);
        logger.log(Level.CONFIG, "Ignoring " + bypassedGameModes.size() + " game modes");

        this.essentialsHook = essentialsHook;
    }


    /**
     * Get whether or not bypass permissions are enabled or disabled
     *
     * @return true of they are enabled
     */
    public boolean isBypassingEnabled()
    {
        return isEnabled;
    }


    /**
     * Get the Set of bypassed gamemodes
     *
     * @return the gamemodes that are bypassed
     */
    public Set<GameMode> getBypassedGameModes()
    {
        return new HashSet<>(bypassedGameModes);
    }


    /**
     * Returns whether a player has bypass permissions or not
     * Can be based on a permission node or game mode
     *
     * @param player the player to be checked
     * @return whether this player should be bypassed or not
     */
    public boolean isPlayerBypassed(Player player)
    {
        // When a player is marked as sleeping ignored
        boolean isSleepingIgnored = isEnabled && player.isSleepingIgnored();

        // Permission based bypassing
        boolean hasPermission       = isEnabled && player.hasPermission("bettersleeping.bypass");
        boolean essentialsBypass    = isEnabled && player.hasPermission("essentials.sleepingignored") && essentialsHook.isHooked();

        // Gamemode based bypassing
        boolean gamemodeBypass      = bypassedGameModes.contains( player.getGameMode() );

        // State based bypassing (internally handles the config settings)
        boolean isAfk       = essentialsHook.isAfk( player );
        boolean isVanished  = essentialsHook.isVanished( player );

        return isSleepingIgnored || hasPermission || essentialsBypass || gamemodeBypass || isAfk || isVanished;
    }

}
