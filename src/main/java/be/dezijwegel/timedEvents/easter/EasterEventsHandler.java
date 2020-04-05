package be.dezijwegel.timedEvents.easter;

import be.dezijwegel.Runnables.PlaySoundRunnable;
import be.dezijwegel.Runnables.SendMessageRunnable;
import be.dezijwegel.management.Management;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class EasterEventsHandler implements Listener {

    Management management;
    Plugin plugin;

    public EasterEventsHandler(Management management, Plugin plugin)
    {
        this.management = management;
        this.plugin = plugin;
    }


    @EventHandler
    public void onWakeEvent(PlayerBedLeaveEvent wake)
    {
        if (!management.getEventsConfig().getBoolean("easter.enable_poop_egg"))
            return;

        Player player = wake.getPlayer();
        long time = player.getWorld().getTime();
        if (time >= 0 && time <= 20)
        {
            poopEgg(player);
        }
    }


    @EventHandler
    public void onSleepEvent(PlayerBedEnterEvent event)
    {
        if (!management.getEventsConfig().getBoolean("easter.enable_jingle"))
            return;

        playBellsJingle(event.getPlayer());
    }

    /**
     * Let this player poop an egg, hears the egg poop sound and gets a message
     * @param player the player that will poop the egg
     */
    private void poopEgg(Player player)
    {
        // Create egg
        ItemStack poopedEgg = new ItemStack(Material.EGG);

        // Change egg name
        ItemMeta meta = poopedEgg.getItemMeta();
        String eggName = ChatColor.GOLD + "[BetterSleeping] " + ChatColor.DARK_AQUA + "Easter egg";
        assert meta != null;
        meta.setDisplayName(eggName);
        poopedEgg.setItemMeta(meta);

        // Perform egg pooping actions
        player.getWorld().dropItemNaturally(player.getLocation(), poopedEgg);
        player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
        management.sendMessage("easter_poopegg", player);
    }


    /**
     * Let the clocks sing for this player
     * @param player the player to enjoy the sounds
     */
    private void playBellsJingle(Player player)
    {
        // Pick a sound
        Sound bell = Sound.BLOCK_NOTE_BLOCK_BELL;

        // Create the jingle
        PlaySoundRunnable play;
        play = new PlaySoundRunnable(player, bell, 1, 0.707107f);
        play.runTaskLater(plugin, 0);
        play = new PlaySoundRunnable(player, bell, 1, 0.529732f);
        play.runTaskLater(plugin, 12);
        play = new PlaySoundRunnable(player, bell, 1, 0.707107f);
        play.runTaskLater(plugin, 24);
        play = new PlaySoundRunnable(player, bell, 1, 0.707107f);
        play.runTaskLater(plugin, 50);
        play = new PlaySoundRunnable(player, bell, 1, 0.529732f);
        play.runTaskLater(plugin, 62);
        play = new PlaySoundRunnable(player, bell, 1, 0.707107f);
        play.runTaskLater(plugin, 74);

        // Inform the player
       management.sendMessage("easter_jingle", player);
    }
}
