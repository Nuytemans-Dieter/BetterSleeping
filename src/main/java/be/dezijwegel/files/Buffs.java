package be.dezijwegel.files;

import be.dezijwegel.BetterSleeping;
import be.dezijwegel.interfaces.Reloadable;
import be.dezijwegel.util.ConsoleLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
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
     * Gets the amount of buffs that will be applied after sleeping
     * @return
     */
    public int getNumBuffs()
    {
        return buffs.size();
    }

    /**
     * Gets the PotionEffectType that relates to the given name
     * @param effect name
     * @return PotionEffectType
     */
    private PotionEffectType getPotionType(String effect) {
        PotionEffectType.getByName(effect);
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
        } else if (effect.equalsIgnoreCase("saturation")) {
            return PotionEffectType.SATURATION;
        } else {
            return null;
        }
    }

    @Override
    public void reload() {
        //Initialize the start
        configAPI = new ConfigAPI(ConfigAPI.FileType.BUFFS, true, plugin);
        buffs = new LinkedList<PotionEffect>();

        //Find all buffs and put them in the list
        String basePath = "positive_effects";
        ConfigurationSection section = configAPI.getConfigurationSection(basePath);
        if (section != null)
        {
            int numLoadedEffects = 0;

            ConsoleCommandSender console = Bukkit.getConsoleSender();

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
                                    configAPI.getInt(basePath + "." + key + ".level") - 1)
                            );
                            numLoadedEffects++;
                    }
                }
            }

            if (numLoadedEffects > 0) {
                if (numLoadedEffects == 1)
                    ConsoleLogger.logPositive("One sleeping buff was found and loaded!", ChatColor.GREEN);
                else
                    ConsoleLogger.logPositive(numLoadedEffects + " Sleeping buffs were found and loaded!", ChatColor.GREEN);
            } else {
                ConsoleLogger.logPositive("No enabled sleeping buffs were found!", ChatColor.GREEN);
            }

        }
    }
}
