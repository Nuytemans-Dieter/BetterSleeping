package be.betterplugins.bettersleeping.util.migration;

import be.betterplugins.core.messaging.logging.BPLogger;
import be.dezijwegel.betteryaml.OptionalBetterYaml;
import be.dezijwegel.betteryaml.validation.ValidationHandler;
import com.google.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class SettingsMigrator
{

    private final JavaPlugin plugin;
    private final BPLogger logger;
    private final File oldFolder;

    @Inject
    public SettingsMigrator(JavaPlugin plugin, BPLogger logger)
    {
        this.plugin = plugin;
        this.logger = logger;

        File serverFolder = plugin.getServer().getWorldContainer();
        this.oldFolder = new File(serverFolder, File.separator + "plugins" + File.separator + "BetterSleeping3");
        File newFolder = new File(serverFolder, File.separator + "plugins" + File.separator + "BetterSleeping4");

        boolean shouldMigrate = oldFolder.exists() && !newFolder.exists();
        if (shouldMigrate)
        {
            logger.log(Level.INFO, ChatColor.RED + "Starting BetterSleeping3 -> BetterSleeping4 migration");
            logger.log(Level.INFO, "Thank you for upgrading!");
            logger.log(Level.INFO, "This console spam will only happen once, most of your old settings will be automatically converted to the new settings");

            handleConfig();
            handleSleepingSettings();
            handleBypassing();
            handleHooks();
            handleBuffs();

            logger.log(Level.INFO, "Your BetterSleeping3 settings have now been migrated to BetterSleeping4. Thank you for updating, and happy sleeping!!");
            if (this.oldFolder.renameTo( new File(serverFolder, File.separator + "plugins" + File.separator + "OLD_BetterSleeping") ))
            {
                logger.log(Level.INFO, "Renamed /plugins/BetterSleeping3 to /plugins/OLD_BetterSleeping...");
                logger.log(Level.INFO, "You can safely delete the old configuration if you will not be downgrading to v3 in the future");
            }

        }
    }

    public void handleConfig()
    {
        logger.log(Level.INFO, "Migrating: config.yml");

        File file = new File(oldFolder, "config.yml");
        if (!file.exists())
        {
            logger.log(Level.INFO, "Config file not found. Not handling config.yml migration");
            return;
        }

        // Load the old settings
        YamlConfiguration oldConfig = YamlConfiguration.loadConfiguration(file);
        String language = oldConfig.getString("language");
        boolean disablePhantoms = oldConfig.getBoolean("disable_phantoms");

        // Force old values
        ValidationHandler handler = new ValidationHandler()
            .addValidator("language", new OverrideValueValidator(language))
            .addValidator("disable_phantoms", new OverrideValueValidator(disablePhantoms));

        logger.log(Level.INFO, "Language: " + language);
        logger.log(Level.INFO, "Disable phantoms: " + disablePhantoms);
        logger.log(Level.INFO, "---");

        // Copy the new config with forced values
        new OptionalBetterYaml("config.yml", handler, plugin, false);
    }

    public void handleBypassing()
    {
        logger.log(Level.INFO, "Migrating: bypassing.yml");

        File file = new File(oldFolder, "bypassing.yml");
        if (!file.exists())
        {
            logger.log(Level.INFO, "Config file not found. Not handling bypassing.yml migration");
            return;
        }

        // Load the old settings
        YamlConfiguration oldConfig = YamlConfiguration.loadConfiguration(file);
        boolean ignoreSurvival = oldConfig.getBoolean("ignore_survival");
        boolean ignoreCreative = oldConfig.getBoolean("ignore_creative");
        boolean ignoreAdventure = oldConfig.getBoolean("ignore_adventure");
        boolean ignoreSpectator = oldConfig.getBoolean("ignore_spectator");

        // Force old values
        ValidationHandler handler = new ValidationHandler()
                .addValidator("ignore_survival", new OverrideValueValidator(ignoreSurvival))
                .addValidator("ignore_creative", new OverrideValueValidator(ignoreCreative))
                .addValidator("ignore_adventure", new OverrideValueValidator(ignoreAdventure))
                .addValidator("ignore_spectator", new OverrideValueValidator(ignoreSpectator));

        logger.log(Level.INFO, "Ignore survival: " + ignoreSurvival);
        logger.log(Level.INFO, "Ignore creative: " + ignoreCreative);
        logger.log(Level.INFO, "Ignore adventure: " + ignoreAdventure);
        logger.log(Level.INFO, "Ignore spectator: " + ignoreSpectator);
        logger.log(Level.INFO, "---");

        // Copy the new config with forced values
        new OptionalBetterYaml("bypassing.yml", handler, plugin, false);
    }

    public void handleHooks()
    {
        logger.log(Level.INFO, "Migrating: hooks.yml");

        File file = new File(oldFolder, "hooks.yml");
        if (!file.exists())
        {
            logger.log(Level.INFO, "Config file not found. Not handling hooks.yml migration");
            return;
        }

        // Load the old settings
        YamlConfiguration oldConfig = YamlConfiguration.loadConfiguration(file);
        boolean essentials_afk_ignored = oldConfig.getBoolean("essentials_afk_ignored");
        int minimum_afk_time = oldConfig.getInt("minimum_afk_time");
        boolean vanished_ignored = oldConfig.getBoolean("vanished_ignored");
        boolean enable_gsit_support = oldConfig.getBoolean("enable_gsit_support");

        // Force old values
        ValidationHandler handler = new ValidationHandler()
                .addValidator("essentials_afk_ignored", new OverrideValueValidator(essentials_afk_ignored))
                .addValidator("minimum_afk_time", new OverrideValueValidator(minimum_afk_time))
                .addValidator("vanished_ignored", new OverrideValueValidator(vanished_ignored))
                .addValidator("enable_gsit_support", new OverrideValueValidator(enable_gsit_support));

        logger.log(Level.INFO, "essentials_afk_ignored: " + essentials_afk_ignored);
        logger.log(Level.INFO, "minimum_afk_time: " + minimum_afk_time);
        logger.log(Level.INFO, "vanished_ignored: " + vanished_ignored);
        logger.log(Level.INFO, "enable_gsit_support: " + enable_gsit_support);
        logger.log(Level.INFO, "---");

        // Copy the new config with forced values
        new OptionalBetterYaml("hooks.yml", handler, plugin, false);
    }

    public void handleBuffs()
    {
        logger.log(Level.INFO, "Migrating: buffs.yml");
        logger.log(Level.WARNING, ChatColor.RED + "Buffs cannot be migrated automatically. However, you can copy that file manually to keep your previous settings!");
        logger.log(Level.INFO, "---");
    }

    public void handleSleepingSettings()
    {
        logger.log(Level.INFO, "Migrating: sleeping_settings.yml");

        File file = new File(oldFolder, "sleeping_settings.yml");
        if (!file.exists())
        {
            logger.log(Level.INFO, "Config file not found. Not handling sleeping_settings.yml migration");
            return;
        }

        // Load the old settings
        YamlConfiguration oldConfig = YamlConfiguration.loadConfiguration(file);

        String sleeperCounter = oldConfig.getString("sleeper_counter");
        sleeperCounter = sleeperCounter == null || sleeperCounter.equalsIgnoreCase("percentage") ? "percentage" : "absolute";
        int needed = oldConfig.getInt(sleeperCounter + ".needed");

        String mode = oldConfig.getString("mode");
        boolean isSmooth = mode == null || mode.equalsIgnoreCase("smooth");
        int oldSpeedup = Math.max(1, oldConfig.getInt("smooth.base_speedup"));
        int nightSkipLength = isSmooth ? 500 / oldSpeedup : oldConfig.getInt("setter.delay");
        int bedEnterCooldown = oldConfig.getInt("bed_enter_delay");

        // Force old values
        ValidationHandler handler = new ValidationHandler()
                .addValidator("sleeper_calculator", new OverrideValueValidator(sleeperCounter))
                .addValidator("needed", new OverrideValueValidator(needed))
                .addValidator("night_skip_length", new OverrideValueValidator(nightSkipLength))
                .addValidator("bed_enter_cooldown", new OverrideValueValidator(bedEnterCooldown))
                .addOptionalSection("world_settings")
                .setOptionalValue("world_settings.worldname.enabled", true)
                .setOptionalValue("world_settings.worldname.day_length", 700)
                .setOptionalValue("world_settings.worldname.night_length", 500)
                .setOptionalValue("world_settings.worldname.night_skip_length", 10);

        logger.log(Level.INFO, "Sleeper calculator: " + sleeperCounter);
        logger.log(Level.INFO, "needed players (% or abs): " + needed);
        logger.log(Level.INFO, "Night skip duration: " + nightSkipLength);
        logger.log(Level.INFO, "Bed enter cooldown: " + bedEnterCooldown);
        logger.log(Level.INFO, "---");

        // Copy the new config with forced values
        new OptionalBetterYaml("sleeping_settings.yml", handler, plugin, false);
    }

}
