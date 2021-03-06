package model;

import be.betterplugins.bettersleeping.model.ConfigContainer;
import be.betterplugins.bettersleeping.hooks.EssentialsHook;
import be.betterplugins.bettersleeping.model.BypassChecker;
import be.betterplugins.core.messaging.logging.BPLogger;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BypassCheckerTest
{

    private BypassChecker setupBypassChecker(boolean hasEssentials, Set<GameMode> bypassedModes, boolean isAfk, boolean isVanished)
    {
        // Setup the desired configuration
        ConfigContainer container = mock(ConfigContainer.class);
        YamlConfiguration config = mock(YamlConfiguration.class);
        for (GameMode mode : GameMode.values())
        {
            String lowerCaseGameMode = mode.toString().toLowerCase();
            when(config.getBoolean("ignore_" + lowerCaseGameMode)).thenReturn( bypassedModes.contains( mode ) );
        }
        when(container.getBypassing()).thenReturn(config);

        // Setup the EssentialsHook
        EssentialsHook essentialsHook = mock(EssentialsHook.class);
        when(essentialsHook.isHooked()).thenReturn(hasEssentials);
        when(essentialsHook.isAfk(any())).thenReturn(isAfk);
        when(essentialsHook.isVanished(any())).thenReturn(isVanished);

        return new BypassChecker(container, essentialsHook, mock(BPLogger.class));
    }

    private Player setupPlayer(boolean isSleepingIgnored, boolean hasBSBypass, boolean hasEssentialsBypass)
    {
        Player player = mock(Player.class);
        when(player.isSleepingIgnored()).thenReturn( isSleepingIgnored );
        when(player.hasPermission("bettersleeping.bypass")).thenReturn(hasBSBypass);
        when(player.hasPermission("essentials.sleepingignored")).thenReturn(hasEssentialsBypass);
        return player;
    }

    /**
     * Default player with no special permission/status -> NOT bypassed
     */
    @Test
    public void testNotBypassed()
    {
        BypassChecker bypassChecker = setupBypassChecker(true, new HashSet<>(), false, false);
        Player player = setupPlayer(false, false, false);
        assert !bypassChecker.isPlayerBypassed( player );
    }

    /**
     * Sleepingignored players are NOT considered to be bypassed
     */
    @Test
    public void testSleepingIgnored()
    {
        BypassChecker bypassChecker = setupBypassChecker(true, new HashSet<>(), false, false);
        Player player = setupPlayer(true, false, false);
        assert !bypassChecker.isPlayerBypassed( player );
    }

    /**
     * Bettersleeping.bypass permission holders should be able to bypass
     */
    @Test
    public void testBSBypass()
    {
        BypassChecker bypassChecker = setupBypassChecker(true, new HashSet<>(), false, false);
        Player player = setupPlayer(false, true, false);
        assert bypassChecker.isPlayerBypassed( player );
    }

    /**
     * Essentials bypassed players are NOT ignored by BetterSleeping -> NOT bypassed
     */
    @Test
    public void testEssentialsBypass()
    {
        BypassChecker bypassChecker = setupBypassChecker(true, new HashSet<>(), false, false);
        Player player = setupPlayer(false, false, true);
        assert !bypassChecker.isPlayerBypassed( player );
    }

    /**
     * AFK players should be bypassed
     */
    @Test
    public void testAfk()
    {
        BypassChecker bypassChecker = setupBypassChecker(true, new HashSet<>(), true, false);
        Player player = setupPlayer(false, false, false);
        assert bypassChecker.isPlayerBypassed( player );
    }

    /**
     * Vanished players should be bypassed
     */
    @Test
    public void testVanished()
    {
        BypassChecker bypassChecker = setupBypassChecker(true, new HashSet<>(), false, true);
        Player player = setupPlayer(false, false, false);
        assert bypassChecker.isPlayerBypassed( player );
    }

    @Test
    public void testGamemodeBypassed()
    {
        BypassChecker bypassChecker = setupBypassChecker(true, new HashSet<>(Collections.singleton(GameMode.CREATIVE)), false, false);
        Player player = setupPlayer(false, false, false);
        when(player.getGameMode()).thenReturn(GameMode.CREATIVE);
        assert bypassChecker.isPlayerBypassed( player );
    }

    @Test
    public void testGamemodeNotBypassed()
    {
        BypassChecker bypassChecker = setupBypassChecker(true, new HashSet<>(Collections.singleton(GameMode.CREATIVE)), false, false);
        Player player = setupPlayer(false, false, false);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        assert !bypassChecker.isPlayerBypassed( player );
    }

}
