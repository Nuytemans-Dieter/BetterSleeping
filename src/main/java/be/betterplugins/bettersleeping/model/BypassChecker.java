package be.betterplugins.bettersleeping.model;

import be.betterplugins.core.messaging.logging.BPLogger;
import be.betterplugins.bettersleeping.hooks.EssentialsHook;
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
    private final Set<GameMode> bypassedGameModes;

    @Inject
    public BypassChecker(ConfigContainer config, EssentialsHook essentialsHook, BPLogger logger)
    {
        YamlConfiguration bypassConfig = config.getBypassing();

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

        logger.log(Level.CONFIG, "Ignoring " + bypassedGameModes.size() + " game modes");

        this.essentialsHook = essentialsHook;
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
        // Permission based bypassing
        boolean hasBSBypass         = player.hasPermission("bettersleeping.bypass");

        // Gamemode based bypassing
        boolean gamemodeBypass      = bypassedGameModes.contains( player.getGameMode() );

        // State based bypassing (internally handles the config settings)
        boolean isAfk       = essentialsHook.isAfk( player );
        boolean isVanished  = essentialsHook.isVanished( player );

        return hasBSBypass || gamemodeBypass || isAfk || isVanished;
    }

}
