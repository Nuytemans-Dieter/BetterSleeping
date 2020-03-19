package be.dezijwegel.timedEvents.aprilFools;

import be.dezijwegel.Runnables.PlaySoundRunnable;
import be.dezijwegel.Runnables.SendMessageRunnable;
import be.dezijwegel.customEvents.PlayersDidNotSleepEvent;
import be.dezijwegel.customEvents.TimeSetToDayEvent;
import be.dezijwegel.files.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class AprilFoolsEventsHandler implements Listener {


    private Plugin plugin;
    private Lang lang;

    private Set<UUID> creeperPrankedList;   // A Set of player IDs that have already been creeper pranked
    private Set<UUID> explosionPrankedList; // A set of player IDs that have already been pranked with an explosion

    Random random;

    public AprilFoolsEventsHandler(Plugin plugin, Lang lang)
    {
        this.plugin = plugin;
        this.lang = lang;

        creeperPrankedList = new HashSet<>();
        explosionPrankedList = new HashSet<>();

        random = new Random();
    }


    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event)
    {
        UUID playerID = event.getPlayer().getUniqueId();

        // Only do the prank once for entering the bed
        if ( ! creeperPrankedList.contains(playerID)) {
            doCreeperSoundPrank(event.getPlayer());
            creeperPrankedList.add(playerID);
        }
    }


    @EventHandler
    public void onDidNotSleep(PlayersDidNotSleepEvent event)
    {
        for (Player p : event.getPlayers())
        {
            if (explosionPrankedList.contains(p.getUniqueId()))     // If player has been explosion pranked already
            {
                doCreeperSoundPrank(p);                             // Do the creeper prank
            } else {                                                // Player has NOT been explosion pranked
                doExplosionPrank(p);                                // Do explosion prank
                explosionPrankedList.add(p.getUniqueId());          // Add user to the pranked list
            }
        }
    }

    @EventHandler
    public void onTimeSetToDay(TimeSetToDayEvent event)
    {
        int randNum = random.nextInt(100);
        if (randNum < 2) {                          // Approx. 1 in 50 times this prank happens
            doTimePrank(event.getWorld());
        }
    }

    private void doCreeperSoundPrank(Player player)
    {
        BukkitRunnable hiss = new PlaySoundRunnable(player, Sound.ENTITY_CREEPER_PRIMED);
        BukkitRunnable boom = new PlaySoundRunnable(player, Sound.ENTITY_GENERIC_EXPLODE);
        BukkitRunnable msg = new SendMessageRunnable(lang, player, "april_fools_creeper_prank");

        hiss.runTask(plugin);
        boom.runTaskLater(plugin, 25L);
        msg.runTaskLater(plugin, 30L);
    }


    private void doExplosionPrank(Player player)
    {
        // Create explosion
        Location loc = player.getLocation();
        player.getWorld().createExplosion(loc, 1, false, false);    // NO fire or explosion damage!

        // Send message
        lang.sendMessage("april_fools_explosion_prank", player);
    }


    private void doTimePrank(World world)
    {
        // Set the time to night
        world.setTime(20000);

        // Get the affected players
        List<Player> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (player.getWorld().equals(world))
            {
                players.add(player);
            }
        }

        // Send message to affected players
        lang.sendMessageToGroup("april_fools_time_prank", players);
    }
}
