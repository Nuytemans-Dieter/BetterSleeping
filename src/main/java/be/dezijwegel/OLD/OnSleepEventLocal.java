/*
 * Geen license header toegevoegd
 * Dieter Nuytemans
 */
package be.dezijwegel.OLD;

import be.dezijwegel.bettersleeping.BetterSleeping;

import java.util.HashMap;

import javafx.scene.paint.Color;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

/**
 *
 * @author Dieter Nuytemans
 * OnSleepEventLocal will consider each world seperate from eachother
 */
public class OnSleepEventLocal extends OnSleepEvent {
    
    private BetterSleeping plugin;
    
    private HashMap<String, Integer> playersSleeping;
    private HashMap<String, Long> lastSkipped;
    
    public OnSleepEventLocal(FileManagement configFile, FileManagement langFile, BetterSleeping plugin) {
        super(configFile, langFile, plugin);
        
        this.plugin = plugin;
        
        playersSleeping = new HashMap<String, Integer>();
        lastSkipped = new HashMap<String, Long>();

        for (World world : Bukkit.getWorlds())
        {
            playersSleeping.put(world.getName(), 0);
            lastSkipped.put(world.getName(), 1L);
        }
    }
    
    @EventHandler
    public void onPlayerEnterBed(PlayerBedEnterEvent e)
    {
        World worldObj = e.getPlayer().getWorld();
        if (worldObj.getTime() > 12500 || worldObj.hasStorm() || worldObj.isThundering()) {
            if (super.PlayerMaySleep(e.getPlayer().getUniqueId()))
            {
                int numSleeping;
                if (playersSleeping.get(worldObj.getName()) != null)
                    numSleeping = playersSleeping.get(worldObj.getName()) + 1;
                else numSleeping = 1;

                playersSleeping.put(worldObj.getName(), numSleeping);

                float numNeeded = playersNeeded(worldObj.getName());

                if (numSleeping >= numNeeded)
                {
                    if (numSleeping == numNeeded) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.getWorld().getName().equals(worldObj.getName()))
                                if (!enough_sleeping.equalsIgnoreCase("ignored"))
                                    p.sendMessage(prefix + enough_sleeping);
                        }
                    }

                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                        if (lastSkipped.get(worldObj.getName()) == null || lastSkipped.get(worldObj.getName()) < System.currentTimeMillis() - 30000) {
                            if (playersSleeping.get(worldObj.getName()) != null) {
                                if (playersSleeping.get(worldObj.getName()) >= numNeeded) {
                                    worldObj.setStorm(false);
                                    worldObj.setTime(1000);
                                    lastSkipped.put(worldObj.getName(), System.currentTimeMillis());

                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        if (p.getWorld().getName().equals(worldObj.getName()))
                                            if (!good_morning.equalsIgnoreCase("ignored"))
                                                p.sendMessage(prefix + good_morning);
                                    }
                                } else {
                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        if (p.getWorld().getName().equals(worldObj.getName()))
                                            if (!cancelled.equalsIgnoreCase("ignored"))
                                                p.sendMessage(prefix + cancelled);
                                    }
                                }
                            } else {
                                Bukkit.getConsoleSender().sendMessage("[BetterSleeping] " + Color.RED + "An unexpected error has occurred, and the night will not be skipped as a result. Please let me know (vallas) on Spigot and I will look into this right away!");
                            }
                        }
                    }, sleepDelay);
                } else if (numSleeping < playersNeeded(worldObj.getName())) {
                    float numLeft = numNeeded - numSleeping;
                    if (numLeft > 0 ) {

                        String msg = amount_left.replaceAll("<amount>", Integer.toString((int) numLeft));

                        for (Player p : Bukkit.getOnlinePlayers())
                        {
                            if (p.getWorld().getName().equals(worldObj.getName()))
                                if (!msg.equalsIgnoreCase("ignored"))
                                    p.sendMessage(prefix + msg);
                        }
                    }
                }
            } else {
                e.getPlayer().sendMessage(prefix + sleep_spam);
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onPlayerLeaveBed(PlayerBedLeaveEvent e)
    {
        String world = e.getPlayer().getWorld().getName();

        if (playersSleeping.get(world) != null)
            playersSleeping.put(world, playersSleeping.get(world) - 1);
        else {
            Bukkit.getServer().getConsoleSender().sendMessage("[BetterSleeping] " + Color.RED + "The amount of sleeping players got lost when a player left their bed (my fault, not yours!). Now counting sleeping players to resolve this issue.");

            int count = 0;
            for (Player p : Bukkit.getOnlinePlayers())
            {
                if (p.isSleeping())
                {
                    if (p.getWorld().getName().equals(world))
                    {
                        count++;
                    }
                }
            }

            playersSleeping.put(world, count);
            Bukkit.getServer().getConsoleSender().sendMessage("[BetterSleeping] " + count + " players were found sleeping in world " + world);
        }
    }
    
    /**
     * Calculate the amount of players needed to sleep in a specific world
     * @param world
     * @return float
     */
    public float playersNeeded(String world)
    {
        int numInWorld = 0;
        for (Player p : Bukkit.getOnlinePlayers())
        {
            if (p.getWorld().getName().equals(world)) numInWorld++;
        }

        float num = playersNeeded * numInWorld / 100.0f;
        return Math.round(num);
    }
    
}
