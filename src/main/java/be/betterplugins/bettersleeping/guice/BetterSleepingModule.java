package be.betterplugins.bettersleeping.guice;

import be.betterplugins.bettersleeping.configuration.ConfigContainer;
import be.betterplugins.core.CoreFactory;
import be.betterplugins.core.commands.BPCommandHandler;
import be.betterplugins.core.commands.messages.CommandMessages;
import be.betterplugins.core.messaging.logging.BPLogger;
import be.betterplugins.core.messaging.messenger.Messenger;
import be.betterplugins.bettersleeping.BetterSleeping;
import be.betterplugins.bettersleeping.commands.HelpCommand;
import be.betterplugins.bettersleeping.commands.ReloadCommand;
import be.betterplugins.bettersleeping.commands.ShoutCommand;
import be.betterplugins.bettersleeping.commands.VersionCommand;
import be.dezijwegel.betteryaml.BetterLang;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

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
    public Messenger provideMessenger(BetterLang lang, BPLogger logger)
    {
        // The shorten_prefix option has been removed, 98.6% had this option enabled
        return new Messenger(lang.getMessages(), logger, ChatColor.GOLD + "[BS4] " + ChatColor.DARK_AQUA);
    }

    @Provides
    @Singleton
    public BPCommandHandler provideCommandHandler(Messenger messenger, BetterLang lang)
    {
        CoreFactory fact = new CoreFactory();

        HelpCommand     help    = new HelpCommand(messenger);
        ReloadCommand   reload  = new ReloadCommand(plugin, messenger);
        ShoutCommand    shout   = new ShoutCommand(messenger);
        VersionCommand  version = new VersionCommand(plugin, messenger);

        Map<String, String> messageMap = lang.getMessages();
        CommandMessages messages = fact.createCommandMessages(
                "&4This command can only be performed by the foreseen executor",
                messageMap.getOrDefault("no_permission", "no_permission"),
                messageMap.getOrDefault("unknown_command", "unknown_command")
        );

        return new BPCommandHandler( messages, messenger,
                help,
                reload,
                shout,
                version
        );
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

}
