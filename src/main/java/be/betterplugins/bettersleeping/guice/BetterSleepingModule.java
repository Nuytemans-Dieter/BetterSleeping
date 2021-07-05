package be.betterplugins.bettersleeping.guice;

import be.betterplugins.bettersleeping.commands.*;
import be.betterplugins.bettersleeping.commands.StatusCommand;
import be.betterplugins.bettersleeping.configuration.ConfigContainer;
import be.betterplugins.bettersleeping.listeners.BuffsHandler;
import be.betterplugins.bettersleeping.messaging.ScreenMessenger;
import be.betterplugins.bettersleeping.model.sleeping.SleepWorldManager;
import be.betterplugins.bettersleeping.model.BypassChecker;
import be.betterplugins.bettersleeping.util.Theme;
import be.betterplugins.core.CoreFactory;
import be.betterplugins.core.commands.BPCommandHandler;
import be.betterplugins.core.commands.messages.CommandMessages;
import be.betterplugins.core.messaging.logging.BPLogger;
import be.betterplugins.core.messaging.messenger.Messenger;
import be.betterplugins.bettersleeping.BetterSleeping;
import be.dezijwegel.betteryaml.BetterLang;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;
import java.util.logging.Level;

public class BetterSleepingModule extends AbstractModule
{

    private final BetterSleeping plugin;
    private final Level logLevel;

    public BetterSleepingModule(BetterSleeping plugin, Level logLevel)
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
    public BetterSleeping provideBetterSleeping()
    {
        return plugin;
    }

    @Provides
    @Singleton
    public Messenger provideMessenger(@Named("has_spigot") boolean hasSpigot, JavaPlugin plugin, BetterLang lang, ConfigContainer configContainer, BPLogger logger)
    {
        logger.log(Level.CONFIG, hasSpigot ? "This server is running on Spigot" : "This server is NOT running on Spigot");
        boolean sendOnScreen = configContainer.getConfig().getBoolean("action_bar_messages");

        Messenger messenger;
        if (hasSpigot && sendOnScreen)
        {
            logger.log(Level.CONFIG, "Using on screen messaging");
            messenger = new ScreenMessenger(plugin, lang.getMessages(), logger);
        }
        else
        {
            logger.log(Level.CONFIG, "Using chat messaging");
            messenger = new Messenger(lang.getMessages(), logger, Theme.prefix);
        }

        return messenger;
    }

    @Provides
    @Singleton
    public BPCommandHandler provideCommandHandler(SleepWorldManager sleepWorldManager, BuffsHandler buffsHandler, BypassChecker bypassChecker, Messenger messenger, BetterLang lang, BPLogger logger)
    {
        // Use a chatMessenger to override the instances where we never want to send messages on screen
        Messenger chatMessenger = new Messenger(lang.getMessages(), logger, Theme.prefix);

        HelpCommand     help    = new HelpCommand( chatMessenger );
        ReloadCommand   reload  = new ReloadCommand( plugin, chatMessenger );
        ShoutCommand    shout   = new ShoutCommand( messenger );
        SleepCommand    sleep   = new SleepCommand( messenger, sleepWorldManager );
        BuffsCommand    buffs   = new BuffsCommand( chatMessenger, buffsHandler, bypassChecker );
        StatusCommand   status = new StatusCommand( chatMessenger, sleepWorldManager );
        VersionCommand  version = new VersionCommand( plugin, chatMessenger );

        Map<String, String> messageMap = lang.getMessages();
        CommandMessages messages = new CoreFactory().createCommandMessages(
                "&4This command can only be performed by the foreseen executor",
                messageMap.getOrDefault("no_permission", "no_permission"),
                messageMap.getOrDefault("unknown_command", "unknown_command")
        );

        return new BPCommandHandler
        ( messages, messenger,
                help,
                reload,
                shout,
                sleep,
                buffs,
                status,
                version
        );
    }

    @Provides
    @Singleton
    public BPLogger provideLogger()
    {
        return new BPLogger(logLevel, "BetterSleeping4");
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

}
