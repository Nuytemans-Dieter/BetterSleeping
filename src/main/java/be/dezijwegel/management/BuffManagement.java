package be.dezijwegel.management;

import be.dezijwegel.bettersleeping.BetterSleeping;
import be.dezijwegel.files.Buffs;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class BuffManagement {

    private Buffs config;

    public BuffManagement(BetterSleeping plugin)
    {
        config = new Buffs(plugin);
    }

    /**
     * Add all buffs to a given Player
     * @param player
     */
    public void addEffects(Player player)
    {
        for (PotionEffect buff : config.getBuffs())
        {
            player.addPotionEffect(buff);
        }
    }

}
