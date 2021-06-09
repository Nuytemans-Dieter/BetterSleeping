package be.dezijwegel.bettersleeping.guice;

import be.betterplugins.core.messaging.logging.BPLogger;
import be.betterplugins.core.messaging.messenger.Messenger;
import be.dezijwegel.betteryaml.BetterLang;
import be.dezijwegel.betteryaml.OptionalBetterYaml;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.logging.Level;

public class BetterSleepingModule extends AbstractModule
{

    private final JavaPlugin plugin;
    private final Level logLevel;

    public BetterSleepingModule(JavaPlugin plugin, Level logLevel)
    {
        this.plugin = plugin;
        this.logLevel = logLevel;
    }

    @Provides
    @Singleton
    public BPLogger provideLogger()
    {
        return new BPLogger(logLevel);
    }

    @Provides
    @Singleton
    @Named("config")
    public YamlConfiguration provideConfig()
    {
        return new OptionalBetterYaml("config.yml", plugin, true).getYamlConfiguration().orElse( new YamlConfiguration() );
    }

    @Provides
    @Singleton
    @Named("buffs")
    public YamlConfiguration provideBuffs()
    {
        return new OptionalBetterYaml("buffs.yml", plugin, true).getYamlConfiguration().orElse(new YamlConfiguration());
    }

    @Provides
    @Singleton
    @Named("bypassing")
    public YamlConfiguration provideBypassing()
    {
        return new OptionalBetterYaml("bypassing.yml", plugin, true).getYamlConfiguration().orElse(new YamlConfiguration());
    }

    @Provides
    @Singleton
    @Named("hooks")
    public YamlConfiguration provideHooks()
    {
        return new OptionalBetterYaml("hooks.yml", plugin, true).getYamlConfiguration().orElse(new YamlConfiguration());
    }

    @Provides
    @Singleton
    @Named("sleeping_settings")
    public YamlConfiguration provideSleepingSettings()
    {
        return new OptionalBetterYaml("sleeping_settings.yml", plugin, true).getYamlConfiguration().orElse(new YamlConfiguration());
    }

    @Provides
    @Singleton
    public BetterLang provideBetterLang(@Named("config") YamlConfiguration section, BPLogger logger)
    {
        String configValue = section.getString("language");
        String language = configValue != null ? configValue.toLowerCase() : "en-us";

        logger.log(Level.CONFIG, "Loading language: " + language);

        return new BetterLang("lang.yml", language + ".yml", plugin);
    }

    @Provides
    @Singleton
    Messenger provideMessenger(BetterLang lang, BPLogger logger)
    {
        return new Messenger(lang.getMessages(), logger, "[BS4]");
    }

}
