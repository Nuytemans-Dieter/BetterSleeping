package be.dezijwegel.bettersleeping.guice;

import be.betterplugins.core.messaging.logging.BPLogger;
import be.betterplugins.core.messaging.messenger.Messenger;
import be.dezijwegel.bettersleeping.util.ConfigContainer;
import be.dezijwegel.betteryaml.BetterLang;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.bukkit.plugin.java.JavaPlugin;

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
    public JavaPlugin provideJavaPlugin()
    {
        return plugin;
    }

    @Provides
    @Singleton
    public BPLogger provideLogger()
    {
        return new BPLogger(logLevel);
    }

    @Provides
    @Singleton
    public BetterLang provideBetterLang(ConfigContainer config, BPLogger logger)
    {
        String configValue = config.getConfig().getString("language");
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
