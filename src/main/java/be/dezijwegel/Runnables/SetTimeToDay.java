package be.dezijwegel.Runnables;

import be.dezijwegel.BetterSleeping;
import be.dezijwegel.events.SleepTracker;
import be.dezijwegel.management.Management;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SetTimeToDay extends BukkitRunnable {

    private Set<World> worlds;

    private Management management;
    private SleepTracker sleepTracker;
    private boolean giveBuffs;

    public SetTimeToDay (List<World> worlds, Management management, SleepTracker sleepTracker)
    {
        this.worlds = new HashSet<World>();

        this.management = management;
        this.sleepTracker = sleepTracker;
        this.giveBuffs = true;

        for (World world : worlds)
        {
            this.worlds.add(world);
        }
    }

    public SetTimeToDay (List<World> worlds, Management management, SleepTracker sleepTracker, boolean giveBuffs)
    {
        this.worlds = new HashSet<World>();

        this.management = management;
        this.sleepTracker = sleepTracker;
        this.giveBuffs = giveBuffs;

        for (World world : worlds)
        {
            this.worlds.add(world);
        }
    }

    /**
     * Get the world(s) of which the time will be set to day
     * @return
     */
    public Set<World> getWorlds()
    {
        return worlds;
    }

    @Override
    public void run() {
        if (BetterSleeping.debug)
        {
            Bukkit.getLogger().info("-----");
            Bukkit.getLogger().info("[BetterSleeping] SetTimeToDay");
        }

        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (worlds.contains(player.getLocation().getWorld()))
            {
                management.sendMessage("good_morning", player);

                if (this.giveBuffs && management.areBuffsEnabled()) {
                    boolean isAsleep = player.isSleeping();
                    boolean isBypassed = sleepTracker.isPlayerBypassed( player );
                    boolean giveBypassBuffs = management.getBooleanSetting("buffs_for_bypassing_players");

                    if (BetterSleeping.debug)
                    {
                        Bukkit.getLogger().info("[BetterSleeping] ---");
                        Bukkit.getLogger().info("[BetterSleeping] SetTimeToDay");
                        Bukkit.getLogger().info("[BetterSleeping] isAsleep: " + isAsleep);
                        Bukkit.getLogger().info("[BetterSleeping] isBypassed: " + isBypassed);
                        Bukkit.getLogger().info("[BetterSleeping] giveBypassBuffs: " + giveBypassBuffs);
                        Bukkit.getLogger().info("[BetterSleeping] Buffs? " + (isAsleep || ( giveBypassBuffs && isBypassed)));
                    }

                    if ( isAsleep || ( giveBypassBuffs && isBypassed ) ) {
                        management.addEffects(player);

                        Map<String, String> replace = new HashMap<String, String>();
                        replace.put("<amount>", Integer.toString( management.getNumBuffs() ));
                        management.sendMessage("buff_received", player, replace, management.getNumBuffs() == 1);
                    } else {
                        Map<String, String> replace = new HashMap<>();
                        replace.put("<amount>", Integer.toString( management.getNumBuffs() ));
                        management.sendMessage("no_buff_received", player, replace, management.getNumBuffs() == 1);
                    }
                }
            }
        }

        for (World world : worlds)
        {
            world.setTime(1000);
            world.setStorm(false);
            sleepTracker.worldWasSetToDay(world);
        }

        if (BetterSleeping.debug)
        {
            Bukkit.getLogger().info("-----");
        }
    }
}
