package be.dezijwegel.files;

import be.dezijwegel.bettersleeping.BetterSleeping;
import be.dezijwegel.bettersleeping.Reloadable;
import org.bukkit.potion.PotionEffect;

import java.util.LinkedList;

public class Buffs implements Reloadable {

    private BetterSleeping plugin;
    private ConfigAPI configAPI;

    private LinkedList<PotionEffect> buffs;

    public Buffs(BetterSleeping plugin)
    {
        this.plugin = plugin;
        configAPI = new ConfigAPI(ConfigAPI.FileType.BUFFS, plugin);
    }


    @Override
    public void reload() {

    }
}
