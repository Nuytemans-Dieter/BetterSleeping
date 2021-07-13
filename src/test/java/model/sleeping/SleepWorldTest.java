package model.sleeping;

import be.betterplugins.bettersleeping.model.ConfigContainer;
import be.betterplugins.bettersleeping.model.sleeping.SleepWorld;
import be.betterplugins.bettersleeping.model.BypassChecker;
import be.betterplugins.core.messaging.logging.BPLogger;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SleepWorldTest
{

    private ServerMock serverMock;

    @Before
    public void before()
    {
        serverMock = MockBukkit.mock();
    }

    @After
    public void tearDown()
    {
        MockBukkit.unmock();
    }

    public ConfigContainer mockConfigContainer()
    {
        YamlConfiguration config = mock(YamlConfiguration.class);
        when(config.getString("sleeper_counter")).thenReturn("percentage");

        ConfigContainer container = mock(ConfigContainer.class);
        when(container.getSleeping_settings()).thenReturn(config);

        return container;
    }

    public void mockGetEnvironment(Player player)
    {
        World world = mock(World.class);
        when(world.getEnvironment()).thenReturn(World.Environment.NORMAL);
        when(player.getWorld()).thenReturn(world);
    }

    @Test
    public void testGetAllPlayersInWorld()
    {
        List<Player> mockPlayerList = new ArrayList<>();
        Player p1 = mock( Player.class );
        Player p2 = mock( Player.class );
        Player p3 = mock( Player.class );

        mockGetEnvironment(p1);
        mockGetEnvironment(p2);
        mockGetEnvironment(p3);

        mockPlayerList.add(p1);
        mockPlayerList.add(p2);
        mockPlayerList.add(p3);

        World world = mock(World.class);
        when(world.getPlayers()).thenReturn( mockPlayerList );

        SleepWorld sleepWorld = new SleepWorld(world, mockConfigContainer(), mock(BypassChecker.class), mock(BPLogger.class));
        assert sleepWorld.getAllPlayersInWorld().equals( mockPlayerList );
    }

    @Test
    public void testGetValidPlayersInWorld()
    {
        List<Player> mockPlayerList = new ArrayList<>();
        Player p1 = mock( Player.class );
        Player p2 = mock( Player.class );
        Player p3 = mock( Player.class );

        mockGetEnvironment(p1);
        mockGetEnvironment(p2);
        mockGetEnvironment(p3);

        mockPlayerList.add(p1);
        mockPlayerList.add(p2);
        mockPlayerList.add(p3);

        BypassChecker checker = mock(BypassChecker.class);
        when(checker.isPlayerBypassed(p1)).thenReturn(false);
        when(checker.isPlayerBypassed(p2)).thenReturn(true);
        when(checker.isPlayerBypassed(p3)).thenReturn(false);

        World world = mock(World.class);
        when(world.getPlayers()).thenReturn( mockPlayerList );

        SleepWorld sleepWorld = new SleepWorld(world, mockConfigContainer(), checker, mock(BPLogger.class));

        List<Player> expectedList = new ArrayList<>();
        expectedList.add(p1);
        expectedList.add(p3);

        assert sleepWorld.getValidPlayersInWorld().equals( expectedList );
    }

    @Test
    public void testTimeChanging()
    {
        World world = new WorldMock();
        world.setTime(100);

        SleepWorld sleepWorld = new SleepWorld(world, mockConfigContainer(), mock(BypassChecker.class), mock(BPLogger.class));

        sleepWorld.addTime(56.3);
        assert world.getTime() == 156;
        assert sleepWorld.getInternalTime() == 156.3;

        sleepWorld.addTime(0.6);
        assert world.getTime() == 156;
        assert sleepWorld.getInternalTime() == 156.9;

        sleepWorld.addTime(0.1);
        assert world.getTime() == 157;
        assert sleepWorld.getInternalTime() == 157;

        sleepWorld.setTime(23999);
        assert world.getTime() == 23999;
        assert sleepWorld.getInternalTime() == 23999;

        sleepWorld.addTime(1);
        assert world.getTime() == 0;
        assert sleepWorld.getInternalTime() == 0;
    }

    @Test
    public void testTimePassedDetection()
    {
        World world = new WorldMock();
        world.setTime(23500);

        SleepWorld sleepWorld = new SleepWorld(world, mockConfigContainer(), mock(BypassChecker.class), mock(BPLogger.class));

        sleepWorld.addTime(500);
        assert sleepWorld.didTimeBecomeDay( 23500 );
        assert sleepWorld.calcPassedTime( 23500 ) == 500;

        sleepWorld.setTime(23999);
        assert !sleepWorld.didTimeBecomeDay(23998);
        assert sleepWorld.calcPassedTime(23500) == 499;

        sleepWorld.addTime(1);
        assert sleepWorld.didTimeBecomeDay(23999);
        assert sleepWorld.calcPassedTime(23999) == 1;
    }
}
