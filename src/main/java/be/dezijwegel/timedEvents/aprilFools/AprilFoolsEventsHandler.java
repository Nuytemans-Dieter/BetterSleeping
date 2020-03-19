package be.dezijwegel.timedEvents.aprilFools;

import be.dezijwegel.customEvents.PlayersDidNotSleepEvent;
import org.bukkit.Bukkit;
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
    private ColorBedsRunnable colorBeds;

    private Set<UUID> creeperPrankedList;   // A Set of player IDs that have already been creeper pranked

    public AprilFoolsEventsHandler(Plugin plugin)
    {
        this.plugin = plugin;

        colorBeds = new ColorBedsRunnable();
        colorBeds.runTaskTimer(plugin, 100, 100);

        creeperPrankedList = new HashSet<>();
    }


    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event)
    {
        doCreeperSoundPrank(event.getPlayer());
        colorBeds.addBed(event.getBed());
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
        if (creeperPrankedList.contains(player.getUniqueId()))
            return;
        else
            creeperPrankedList.add(player.getUniqueId());

        BukkitRunnable hiss = new PlaySoundRunnable(player, Sound.ENTITY_CREEPER_PRIMED);
        BukkitRunnable boom = new PlaySoundRunnable(player, Sound.ENTITY_GENERIC_EXPLODE);

        hiss.runTask(plugin);
        boom.runTaskLater(plugin, 25L);
    }

}
