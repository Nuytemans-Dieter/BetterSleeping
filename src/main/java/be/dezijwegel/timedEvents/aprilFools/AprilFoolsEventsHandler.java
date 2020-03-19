package be.dezijwegel.timedEvents.aprilFools;

import be.dezijwegel.customEvents.PlayersDidNotSleepEvent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AprilFoolsEventsHandler implements Listener {


    private Plugin plugin;

    private Set<UUID> creeperPrankedList;   // A Set of player IDs that have already been creeper pranked

    public AprilFoolsEventsHandler(Plugin plugin)
    {
        this.plugin = plugin;



        creeperPrankedList = new HashSet<>();
    }


    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event)
    {
        // Only do the prank once for entering the bed
        if (creeperPrankedList.contains(event.getPlayer().getUniqueId()))
            doCreeperSoundPrank(event.getPlayer());;


    }


    @EventHandler
    public void onDidNotSleep(PlayersDidNotSleepEvent event)
    {
        for (Player p : event.getPlayers())
        {
            doCreeperSoundPrank(p);
        }
    }

    private void doCreeperSoundPrank(Player player)
    {
        BukkitRunnable hiss = new PlaySoundRunnable(player, Sound.ENTITY_CREEPER_PRIMED);
        BukkitRunnable boom = new PlaySoundRunnable(player, Sound.ENTITY_GENERIC_EXPLODE);

        hiss.runTask(plugin);
        boom.runTaskLater(plugin, 25L);
    }

}
