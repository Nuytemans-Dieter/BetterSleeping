package be.dezijwegel.timedEvents.easter;

import be.dezijwegel.Runnables.PlaySoundRunnable;
import be.dezijwegel.management.Management;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

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
        if (time >= 0 && time <= 5)
        {
            poopEgg(player);
        }
    }


    /**
     * Let this player poop an egg, hears the egg poop sound and gets a message
     * @param player the player that will poop the egg
     */
    private void poopEgg(Player player)
    {
        ItemStack poopedEgg = new ItemStack(Material.EGG);

        player.getWorld().dropItemNaturally(player.getLocation(), poopedEgg);
        player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
        management.sendMessage("easter_poopegg", player);
    }

}
