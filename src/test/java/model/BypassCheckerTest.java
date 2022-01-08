package model;

import be.betterplugins.bettersleeping.hooks.EssentialsHook;
import be.betterplugins.bettersleeping.model.ConfigContainer;
import be.betterplugins.bettersleeping.model.permissions.BypassChecker;
import be.betterplugins.core.messaging.logging.BPLogger;
import mock.MockPermissionsCache;
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

    private BypassChecker setupBypassChecker(boolean hasEssentials, Set<GameMode> bypassedModes, HashSet<Player> playersWithBypassPermission, boolean isAfk, boolean isVanished)
    {
        // Setup the desired configuration
        ConfigContainer container = mock(ConfigContainer.class);
        YamlConfiguration config = mock(YamlConfiguration.class);
        for (GameMode mode : GameMode.values())
        {
            String lowerCaseGameMode = mode.toString().toLowerCase();
            when(config.getBoolean("ignore_" + lowerCaseGameMode)).thenReturn(bypassedModes.contains(mode));
        }
        when(container.getBypassing()).thenReturn(config);

        // Setup the EssentialsHook
        EssentialsHook essentialsHook = mock(EssentialsHook.class);
        when(essentialsHook.isHooked()).thenReturn(hasEssentials);
        when(essentialsHook.isAfk(any())).thenReturn(isAfk);
        when(essentialsHook.isVanished(any())).thenReturn(isVanished);

        return new BypassChecker(container, new MockPermissionsCache(null, playersWithBypassPermission), essentialsHook, mock(BPLogger.class));
    }

    private Player setupPlayer(boolean isSleepingIgnored, boolean hasBSBypass, boolean hasEssentialsBypass)
    {
        Player player = mock(Player.class);
        when(player.isSleepingIgnored()).thenReturn(isSleepingIgnored);
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
        Player player = setupPlayer(false, false, false);
        BypassChecker bypassChecker = setupBypassChecker(true, new HashSet<>(), new HashSet<>(), false, false);
        assert !bypassChecker.isPlayerBypassed(player);
    }

    /**
     * Sleepingignored players are NOT considered to be bypassed
     */
    @Test
    public void testSleepingIgnored()
    {
        Player player = setupPlayer(true, false, false);
        BypassChecker bypassChecker = setupBypassChecker(true, new HashSet<>(), new HashSet<>(), false, false);
        assert !bypassChecker.isPlayerBypassed(player);
    }

    /**
     * Bettersleeping.bypass permission holders should be able to bypass
     */
    @Test
    public void testBSBypass()
    {
        Player player = setupPlayer(false, true, false);
        BypassChecker bypassChecker = setupBypassChecker(true, new HashSet<>(), new HashSet<Player>()
        {{
            add(player);
        }}, false, false);
        assert bypassChecker.isPlayerBypassed(player);
    }

    /**
     * Essentials bypassed players are NOT ignored by BetterSleeping -> NOT bypassed
     */
    @Test
    public void testEssentialsBypass()
    {
        Player player = setupPlayer(false, false, true);
        BypassChecker bypassChecker = setupBypassChecker(true, new HashSet<>(), new HashSet<>(), false, false);
        assert !bypassChecker.isPlayerBypassed(player);
    }

    /**
     * AFK players should be bypassed
     */
    @Test
    public void testAfk()
    {
        Player player = setupPlayer(false, false, false);
        BypassChecker bypassChecker = setupBypassChecker(true, new HashSet<>(), new HashSet<>(), true, false);
        assert bypassChecker.isPlayerBypassed(player);
    }

    /**
     * Vanished players should be bypassed
     */
    @Test
    public void testVanished()
    {
        Player player = setupPlayer(false, false, false);
        BypassChecker bypassChecker = setupBypassChecker(true, new HashSet<>(), new HashSet<>(), false, true);
        assert bypassChecker.isPlayerBypassed(player);
    }

    @Test
    public void testGamemodeBypassed()
    {
        Player player = setupPlayer(false, false, false);
        when(player.getGameMode()).thenReturn(GameMode.CREATIVE);
        BypassChecker bypassChecker = setupBypassChecker(true, new HashSet<>(Collections.singleton(GameMode.CREATIVE)), new HashSet<>(), false, false);
        assert bypassChecker.isPlayerBypassed(player);
    }

    @Test
    public void testGamemodeNotBypassed()
    {
        Player player = setupPlayer(false, false, false);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        BypassChecker bypassChecker = setupBypassChecker(true, new HashSet<>(Collections.singleton(GameMode.CREATIVE)), new HashSet<>(), false, false);
        assert !bypassChecker.isPlayerBypassed(player);
    }

}
