package be.betterplugins.bettersleeping.model;

import be.betterplugins.core.messaging.logging.BPLogger;
import be.dezijwegel.betteryaml.OptionalBetterYaml;
import be.dezijwegel.betteryaml.validation.ValidationHandler;
import be.dezijwegel.betteryaml.validation.validator.ChainedValidator;
import be.dezijwegel.betteryaml.validation.validator.numeric.Min;
import be.dezijwegel.betteryaml.validation.validator.string.StringWhiteList;
import be.dezijwegel.betteryaml.validation.validator.string.ToLowerCase;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.logging.Level;

@Singleton
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
        ValidationHandler configValidation = new ValidationHandler()
            .addValidator("language", new StringWhiteList
            (
                "en-US",
                true,
                "de-DE", "en-US", "es-ES", "fr-FR", "it-IT", "ja-JP", "nl-BE", "pt-PT", "ru-RU", "silent", "zh-CN", "zh-HK", "zh-TW")
            )
            .addValidator(
                "logging_level", new StringWhiteList("Default", true, "Default", "Config", "All")
            );

        ValidationHandler buffsValidation = new ValidationHandler()
            .addOptionalSection("sleeper_buffs")
            .setOptionalValue("sleeper_buffs.speed.time", 20)
            .setOptionalValue("sleeper_buffs.speed.level", 1)
            .addOptionalSection("non_sleeper_debuffs")
            .setOptionalValue("non_sleeper_debuffs.slow.time", 3)
            .setOptionalValue("non_sleeper_debuffs.slow.level", 1)
            .addOptionalSection("sleeper_commands")
            .addOptionalSection("non_sleeper_commands");

        ValidationHandler hooksValidation = new ValidationHandler()
                .addValidator("minimum_afk_time", new Min(-1));

        ValidationHandler sleeping_settingsValidation = new ValidationHandler()
            .addValidator("sleeper_calculator", new ChainedValidator(
                new StringWhiteList("percentage", true, "percentage", "absolute"),
                new ToLowerCase()
            ))
            .addValidator("needed", new Min(0))
            .addValidator("night_skip_length", new Min(0))
            .addValidator("day_length", new Min(0))
            .addValidator("night_length", new Min(0))
            .addValidator("bed_enter_cooldown", new Min(0))
            .addOptionalSection("world_settings")
            .setOptionalValue("world_settings.worldname.enabled", true)
            .setOptionalValue("world_settings.worldname.day_length", 700)
            .setOptionalValue("world_settings.worldname.night_length", 500)
            .setOptionalValue("world_settings.worldname.night_skip_length", 10);

        OptionalBetterYaml configBY = new OptionalBetterYaml("config.yml", configValidation, plugin, true);
        OptionalBetterYaml buffsBY = new OptionalBetterYaml("buffs.yml", buffsValidation, plugin, true);
        OptionalBetterYaml bypassingBY = new OptionalBetterYaml("bypassing.yml", plugin, true);
        OptionalBetterYaml hooksBY = new OptionalBetterYaml("hooks.yml", hooksValidation, plugin, true);
        OptionalBetterYaml sleeping_settingsBY = new OptionalBetterYaml("sleeping_settings.yml", sleeping_settingsValidation, plugin, true);

        try
        {
            config = configBY.getYamlConfiguration().get();
            buffs = buffsBY.getYamlConfiguration().get();
            bypassing = bypassingBY.getYamlConfiguration().get();
            hooks = hooksBY.getYamlConfiguration().get();
            sleeping_settings = sleeping_settingsBY.getYamlConfiguration().get();
        }
        catch (NoSuchElementException ignored)
        {
            logger.log(Level.SEVERE, "This is a BetterYaml issue.");
            logger.log(Level.SEVERE, "BetterSleeping cannot enable due to an error in your jar file, please contact the developer!");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    public @NotNull YamlConfiguration getConfig()
    {
        return config;
    }

    public @NotNull YamlConfiguration getBuffs()
    {
        return buffs;
    }

    public @NotNull YamlConfiguration getBypassing()
    {
        return bypassing;
    }

    public @NotNull YamlConfiguration getHooks()
    {
        return hooks;
    }

    public @NotNull YamlConfiguration getSleeping_settings()
    {
        return sleeping_settings;
    }
}
