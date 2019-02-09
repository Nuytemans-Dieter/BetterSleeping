package be.dezijwegel.OLD;

import be.dezijwegel.bettersleeping.BetterSleeping;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

/**
 *
 * @author Dieter Nuytemans
 */
public class OnSleepEventGlobal extends OnSleepEvent implements Listener{

    private BetterSleeping plugin;

    private int playersSleeping;
    private long lastSkipped;

    public OnSleepEventGlobal(FileManagement configFile, FileManagement langFile, BetterSleeping plugin)
    {
        super(configFile, langFile, plugin);
        this.plugin = plugin;

        playersSleeping = 0;
        lastSkipped = 0;
    }

    @EventHandler
    public void onPlayerEnterBed(PlayerBedEnterEvent e)
    {
        World worldObj = e.getPlayer().getWorld();
        if (worldObj.getTime() > 12500 || worldObj.hasStorm() || worldObj.isThundering())
        {
            if (super.PlayerMaySleep(e.getPlayer().getUniqueId()))
            {
                playersSleeping++;
                float numNeeded = playersNeeded();

                if (playersSleeping >= numNeeded)
                {
                    if (playersSleeping == numNeeded) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (!enough_sleeping.equalsIgnoreCase("ignored"))
                                p.sendMessage(prefix + enough_sleeping);
                        }
                    }

                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                        if(lastSkipped < System.currentTimeMillis() - 30000) {
                            if (playersSleeping >= numNeeded) {
                                for (World world : Bukkit.getWorlds()) {
                                    world.setStorm(false);
                                    world.setTime(1000);
                                }

                                lastSkipped = System.currentTimeMillis();

                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    if (!good_morning.equalsIgnoreCase("ignored"))
                                        p.sendMessage(prefix + good_morning);
                                }
                            } else {
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    if (!cancelled.equalsIgnoreCase("ignored"))
                                        p.sendMessage(prefix + cancelled);
                                }
                            }
                        }
                    }, sleepDelay);
                } else {
                    float numLeft = numNeeded - playersSleeping;
                    if (numLeft > 0 ) {

                        String msg = amount_left.replaceAll("<amount>", Integer.toString((int) Math.round(numLeft)));

                        for (Player p : Bukkit.getOnlinePlayers())
                        {
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
        playersSleeping--;
    }

    /**
     * Calculate the amount of players needed according to the settings and current online players
     * @return float
     */
    public float playersNeeded()
    {
        int numOnline = Bukkit.getOnlinePlayers().size();
        return (playersNeeded * numOnline / 100.0f);
    }
}