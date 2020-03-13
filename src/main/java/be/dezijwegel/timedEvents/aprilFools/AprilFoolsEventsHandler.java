package be.dezijwegel.timedEvents.aprilFools;

import be.dezijwegel.customEvents.PlayersDidNotSleepEvent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class AprilFoolsEventsHandler implements Listener {


    private Plugin plugin;


    public AprilFoolsEventsHandler(Plugin plugin)
    {
        this.plugin = plugin;
    }


    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event)
    {
        doCreeperSoundPrank(event.getPlayer());
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
        boom.runTaskLater(plugin, 20L);
    }

}
