package be.dezijwegel.bettersleeping.util;

import be.betterplugins.core.messaging.logging.BPLogger;
import be.dezijwegel.betteryaml.OptionalBetterYaml;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import java.util.logging.Level;

public class ConfigContainer
{

    private YamlConfiguration config;
    private YamlConfiguration buffs;
    private YamlConfiguration bypassing;
    private YamlConfiguration hooks;
    private YamlConfiguration sleeping_settings;

    @Inject
    public ConfigContainer(JavaPlugin plugin, BPLogger logger)
    {
        OptionalBetterYaml configBY = new OptionalBetterYaml("config.yml", plugin, true);
        OptionalBetterYaml buffsBY = new OptionalBetterYaml("buffs.yml", plugin, true);
        OptionalBetterYaml bypassingBY = new OptionalBetterYaml("bypassing.yml", plugin, true);
        OptionalBetterYaml hooksBY = new OptionalBetterYaml("hooks.yml", plugin, true);
        OptionalBetterYaml sleeping_settingsBY = new OptionalBetterYaml("sleeping_settings.yml", plugin, true);

        try
        {
            config = configBY.getYamlConfiguration().get();
            buffs = buffsBY.getYamlConfiguration().get();
            bypassing = bypassingBY.getYamlConfiguration().get();
            hooks = hooksBY.getYamlConfiguration().get();
            sleeping_settings = sleeping_settingsBY.getYamlConfiguration().get();
        }
        catch (Exception ignored)
        {
            logger.log(Level.SEVERE, "BetterSleeping cannot enable due to an error in your jar file, please contact the developer!");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    public YamlConfiguration getConfig()
    {
        return config;
    }

    public YamlConfiguration getBuffs()
    {
        return buffs;
    }

    public YamlConfiguration getBypassing()
    {
        return bypassing;
    }

    public YamlConfiguration getHooks()
    {
        return hooks;
    }

    public YamlConfiguration getSleeping_settings()
    {
        return sleeping_settings;
    }
}
