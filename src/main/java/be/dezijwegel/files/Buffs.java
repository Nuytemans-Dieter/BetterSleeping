package be.dezijwegel.files;

import be.dezijwegel.bettersleeping.BetterSleeping;
import be.dezijwegel.bettersleeping.Reloadable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.LinkedList;

public class Buffs implements Reloadable {

    private BetterSleeping plugin;
    private ConfigAPI configAPI;

    private LinkedList<PotionEffect> buffs;

    public Buffs(BetterSleeping plugin)
    {
        this.plugin = plugin;
        reload();
    }

    /**
     * Get the list of loaded buffs and return it
     * @return
     */
    public LinkedList<PotionEffect> getBuffs()
    {
        return buffs;
    }

    /**
     * Gets the PotionEffectType that relates to the given name
     * @param effect name
     * @return PotionEffectType
     */
    private PotionEffectType getPotionType(String effect) {
        if (effect.equalsIgnoreCase("regeneration")) {
            return PotionEffectType.REGENERATION;
        } else if (effect.equalsIgnoreCase("absorption")) {
            return PotionEffectType.ABSORPTION;
        } else if (effect.equalsIgnoreCase("jump")) {
            return PotionEffectType.JUMP;
        } else if (effect.equalsIgnoreCase("speed")) {
            return PotionEffectType.SPEED;
        } else if (effect.equalsIgnoreCase("damage_resistance")) {
            return PotionEffectType.DAMAGE_RESISTANCE;
        } else if (effect.equalsIgnoreCase("fast_digging")) {
            return PotionEffectType.FAST_DIGGING;
        } else if (effect.equalsIgnoreCase("increase_damage")) {
            return PotionEffectType.INCREASE_DAMAGE;
        } else {
            return null;
        }
    }

    @Override
    public void reload() {
        //Initialize the start
        configAPI = new ConfigAPI(ConfigAPI.FileType.BUFFS, plugin);
        buffs = new LinkedList<PotionEffect>();

        //Find all buffs and put them in the list
        String basePath = "positive_effects";
        ConfigurationSection section = configAPI.getConfigurationSection(basePath);
        if (section != null)
        {
            for (String key : section.getKeys(false)) {
                //If time and level exist
                if (configAPI.contains(basePath + "." + key + ".time") && configAPI.contains(basePath + "." + key + ".level")) {
                    //If time and level are larger than 0
                    if (configAPI.getInt(basePath + "." + key + ".time") > 0 && configAPI.getInt(basePath + "." + key + ".level") > 0) {
                        PotionEffectType effect = getPotionType(key);
                        //If the effect exists
                        if (effect != null)
                            buffs.add(new PotionEffect(
                                    effect,
                                    configAPI.getInt(basePath + "." + key + ".time"),
                                    configAPI.getInt(basePath + "." + key + ".level"))
                            );
                    }
                }
            }
        }
    }
}
