package model;

import be.betterplugins.bettersleeping.configuration.ConfigContainer;
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

    private BypassChecker setupBypassChecker(boolean isEnabled, boolean hasEssentials, Set<GameMode> bypassedModes, boolean isAfk, boolean isVanished)
    {
        // Setup the desired configuration
        ConfigContainer container = mock(ConfigContainer.class);
        YamlConfiguration config = mock(YamlConfiguration.class);
        when(config.getBoolean("enable_bypassing")).thenReturn( isEnabled );
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

    @Test
    public void testNotBypassed()
    {
        BypassChecker bypassChecker = setupBypassChecker(true, true, new HashSet<>(), false, false);
        Player player = setupPlayer(false, false, false);
        assert !bypassChecker.isPlayerBypassed( player );
    }

    @Test
    public void testSleepingIgnored()
    {
        BypassChecker bypassChecker = setupBypassChecker(true, true, new HashSet<>(), false, false);
        Player player = setupPlayer(true, false, false);
        assert bypassChecker.isPlayerBypassed( player );
    }

    @Test
    public void testBSBypass()
    {
        BypassChecker bypassChecker = setupBypassChecker(true, true, new HashSet<>(), false, false);
        Player player = setupPlayer(false, true, false);
        assert bypassChecker.isPlayerBypassed( player );
    }

    @Test
    public void testEssentialsBypass()
    {
        BypassChecker bypassChecker = setupBypassChecker(true, true, new HashSet<>(), false, false);
        Player player = setupPlayer(false, false, true);
        assert bypassChecker.isPlayerBypassed( player );
    }

    @Test
    public void testAfk()
    {
        BypassChecker bypassChecker = setupBypassChecker(true, true, new HashSet<>(), true, false);
        Player player = setupPlayer(false, false, false);
        assert bypassChecker.isPlayerBypassed( player );
    }

    @Test
    public void testVanished()
    {
        BypassChecker bypassChecker = setupBypassChecker(true, true, new HashSet<>(), false, true);
        Player player = setupPlayer(false, false, false);
        assert bypassChecker.isPlayerBypassed( player );
    }

    @Test
    public void testGamemodeBypassed()
    {
        BypassChecker bypassChecker = setupBypassChecker(true, true, new HashSet<>(Collections.singleton(GameMode.CREATIVE)), false, false);
        Player player = setupPlayer(false, false, false);
        when(player.getGameMode()).thenReturn(GameMode.CREATIVE);
        assert bypassChecker.isPlayerBypassed( player );
    }

    @Test
    public void testGamemodeNotBypassed()
    {
        BypassChecker bypassChecker = setupBypassChecker(true, true, new HashSet<>(Collections.singleton(GameMode.CREATIVE)), false, false);
        Player player = setupPlayer(false, false, false);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        assert !bypassChecker.isPlayerBypassed( player );
    }

    @Test
    public void testDisabledBypass()
    {
        BypassChecker bypassChecker = setupBypassChecker(false, true, new HashSet<>(), false, false);
        Player player = setupPlayer(true, true, true);
        assert !bypassChecker.isPlayerBypassed( player );
    }

    @Test
    public void testDisabledAfkVanished()
    {
        BypassChecker bypassChecker = setupBypassChecker(false, true, new HashSet<>(), true, true);
        Player player = setupPlayer(false, false, false);
        assert bypassChecker.isPlayerBypassed( player );
    }

}
