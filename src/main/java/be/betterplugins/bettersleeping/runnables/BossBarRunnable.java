package be.betterplugins.bettersleeping.runnables;

import be.betterplugins.bettersleeping.model.ConfigContainer;
import be.betterplugins.bettersleeping.model.SleepStatus;
import be.betterplugins.bettersleeping.model.sleeping.SleepWorldManager;
import be.betterplugins.bettersleeping.util.TimeUtil;
import be.betterplugins.core.messaging.messenger.Messenger;
import be.betterplugins.core.messaging.messenger.MsgEntry;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@Singleton
public class BossBarRunnable extends BukkitRunnable
{
    private final SleepWorldManager sleepWorldManager;
    private final Messenger messenger;
    private final Map<World, BossBar> bossBarMap;

    @Inject
    public BossBarRunnable(SleepWorldManager sleepWorldManager, Messenger messenger)
    {
        this.sleepWorldManager = sleepWorldManager;
        this.messenger = messenger;
        this.bossBarMap = new HashMap<>();
    }

    private void updateBossBar(@NotNull BossBar bossBar, @Nullable SleepStatus sleepStatus)
    {
        if (sleepStatus == null)
            return;

        bossBar.setProgress(
                (double) sleepStatus.getNumSleepers() / (double) sleepStatus.getNumNeeded()
        );

        bossBar.setTitle(
            messenger.composeMessage(
                "bossbar_title",
                false,
                new MsgEntry("<num_sleeping>", sleepStatus.getNumSleepers()),
                new MsgEntry("<needed_sleeping>", sleepStatus.getNumNeeded()),
                new MsgEntry("<remaining_sleeping>", sleepStatus.getNumMissing())
            )
        );
    }

    @Override
    public void run()
    {
        // Update current bossbars
        for (Entry<World, BossBar> entry : bossBarMap.entrySet())
        {
            BossBar bossBar = entry.getValue();
            World world = entry.getKey();

            // Update bossbar visualisation
            SleepStatus sleepStatus = this.sleepWorldManager.getSleepStatus( world );
            updateBossBar(bossBar, sleepStatus);

            // Remove all players in a wrong world
            for (Player player : bossBar.getPlayers())
            {
                if (!player.isOnline() || !player.getWorld().getName().equals( world.getName() ))
                {
                    bossBar.removePlayer( player );
                }
            }

            // Only enable the bossbar during the night
            bossBar.setVisible(!TimeUtil.isDayTime( world ));
        }

        // Update players bossbar visualisation
        for(Player player : Bukkit.getOnlinePlayers())
        {
            World world = player.getWorld();

            // Skip players in disabled worlds
            SleepStatus sleepStatus = this.sleepWorldManager.getSleepStatus( world );
            if ( sleepStatus == null )
                continue;;

            BossBar bossBar;
            if (!bossBarMap.containsKey( world ))
            {
                bossBar = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SEGMENTED_12);
                bossBar.setVisible(true);
                updateBossBar(bossBar, sleepStatus);
                bossBarMap.put( world, bossBar);
            }
            else
            {
                bossBar = bossBarMap.get( world );
            }

            bossBar.addPlayer( player );
        }
    }

    /**
     * Stop all BossBars from updating and remove all current visualisations
     */
    public void stopBossBars()
    {
        this.cancel();

        for (BossBar bossBar : bossBarMap.values())
        {
            bossBar.removeAll();
        }
        bossBarMap.clear();
    }
}
