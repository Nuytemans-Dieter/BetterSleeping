package be.betterplugins.bettersleeping.guice;

import be.betterplugins.bettersleeping.commands.*;
import be.betterplugins.bettersleeping.commands.StatusCommand;
import be.betterplugins.bettersleeping.model.ConfigContainer;
import be.betterplugins.bettersleeping.listeners.BuffsHandler;
import be.betterplugins.bettersleeping.messaging.ScreenMessenger;
import be.betterplugins.bettersleeping.model.sleeping.SleepWorldManager;
import be.betterplugins.bettersleeping.model.permissions.BypassChecker;
import be.betterplugins.bettersleeping.util.FileLogger;
import be.betterplugins.bettersleeping.util.Theme;
import be.betterplugins.core.CoreFactory;
import be.betterplugins.core.commands.BPCommandHandler;
import be.betterplugins.core.commands.messages.CommandMessages;
import be.betterplugins.core.messaging.logging.BPLogger;
import be.betterplugins.core.messaging.messenger.Messenger;
import be.betterplugins.bettersleeping.BetterSleeping;
import be.dezijwegel.betteryaml.BetterLang;
import be.dezijwegel.betteryaml.BetterYaml;
import be.dezijwegel.betteryaml.OptionalBetterYaml;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
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
    @Named("prefix")
    public String providePrefix()
    {
        return Theme.primaryColor + "[BS4] " + Theme.secondaryColor;
    }

    @Provides
    @Singleton
    public Messenger provideMessenger(@Named("has_spigot") boolean hasSpigot, @Named("prefix") String prefix, JavaPlugin plugin, BetterLang lang, ConfigContainer configContainer, BPLogger logger)
    {
        logger.log(Level.CONFIG, hasSpigot ? "This server is running on Spigot" : "This server is NOT running on Spigot");

        BPLogger messengerLogger = new BPLogger(Level.OFF, "BetterSleeping4");
        boolean sendOnScreen = configContainer.getConfig().getBoolean("action_bar_messages");

        Messenger messenger;
        if (hasSpigot && sendOnScreen)
        {
            logger.log(Level.CONFIG, "Using on screen messaging");
            messenger = new ScreenMessenger(plugin, lang.getMessages(), prefix, messengerLogger);
        }
        else
        {
            logger.log(Level.CONFIG, "Using chat messaging");
            messenger = new Messenger(lang.getMessages(), messengerLogger, prefix);
        }

        return messenger;
    }

    @Provides
    @Singleton
    public BPCommandHandler provideCommandHandler(SleepWorldManager sleepWorldManager, @Named("prefix") String prefix, BuffsHandler buffsHandler, BypassChecker bypassChecker, Messenger messenger, Map<String, String> langMessages, BPLogger logger)
    {
        // Use a chatMessenger to override the instances where we never want to send messages on screen
        Messenger chatMessenger = new Messenger(langMessages, logger, prefix);

        HelpCommand     help    = new HelpCommand( chatMessenger );
        ReloadCommand   reload  = new ReloadCommand( plugin, chatMessenger );
        ShoutCommand    shout   = new ShoutCommand( messenger, sleepWorldManager );
        SleepCommand    sleep   = new SleepCommand( messenger, sleepWorldManager );
        BuffsCommand    buffs   = new BuffsCommand( chatMessenger, buffsHandler, bypassChecker );
        StatusCommand   status = new StatusCommand( chatMessenger, sleepWorldManager );
        VersionCommand  version = new VersionCommand( plugin, chatMessenger );

        CommandMessages messages = new CoreFactory().createCommandMessages
        (
                langMessages.getOrDefault("wrong_executor", "wrong_executor"),
                langMessages.getOrDefault("no_permission", "no_permission"),
                langMessages.getOrDefault("unknown_command", "unknown_command")
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
    public BPLogger provideLogger(JavaPlugin plugin)
    {
        YamlConfiguration config = YamlConfiguration.loadConfiguration( new File(plugin.getDataFolder() + File.separator + "config.yml") );
        boolean doFileLogging = config.contains("save_logs") && config.getBoolean("save_logs");

        if (doFileLogging)
        {
            try
            {
                return new FileLogger(logLevel, plugin, "BetterSleeping4");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return new BPLogger(logLevel, "BetterSleeping4");
    }

    @Provides
    @Singleton
    public BetterLang provideBetterLang(ConfigContainer config, BPLogger logger)
    {
        logger.log(Level.FINER, "Providing BetterLang...");

        // Get the desired language code
        String configLanguage = config.getConfig().getString("language", "en-us").toLowerCase();
        logger.log(Level.FINER, "The selected language is: " + configLanguage);

//        // Delete the current file if the selected language is not active
//        try {
//            File langFile = new File(plugin.getDataFolder(), "lang.yml");
//
//            if (langFile.isFile())
//            {
//                logger.log(Level.FINER, "Loading current language configuration...");
//                YamlConfiguration langConfig = new YamlConfiguration();
//                langConfig.load(langFile);
//                final String currentLanguage = langConfig.getString("selected_language");
//                logger.log(Level.FINER, "The current language is: " + currentLanguage);
//
//                if (currentLanguage != null && !configLanguage.equalsIgnoreCase(currentLanguage))
//                {
//                    logger.log(Level.INFO, "Deleting the current language configuration");
////                File newLangLocation = new File(plugin.getDataFolder(), "old-lang-" + System.currentTimeMillis() + ".yml");
////                langFile.renameTo(newLangLocation);
////                langFile.delete();
//                }
//                else
//                {
//                    logger.log(Level.FINER, "No lang.yml deletion is required as no new language has been selected");
//                }
//            }
//            else
//            {
//                logger.log(Level.FINER, "No current language configuration found");
//            }
//        }
//        catch (IOException | InvalidConfigurationException e)
//        {
//            logger.log(Level.WARNING, "Something went wrong while deleting your old language file");
//            logger.log(Level.WARNING, "Please delete lang.yml manually and restart BetterSleeping");
//        }

        // Load language configuration
        logger.log(Level.CONFIG, "Loading language: " + configLanguage);
        BetterLang betterLang = new BetterLang("lang.yml", configLanguage + ".yml", plugin);

        if (!betterLang.getYamlConfiguration().isPresent())
        {
            logger.log(Level.SEVERE, "This is a BetterLang issue.");
            logger.log(Level.SEVERE, "BetterSleeping cannot enable due to an error in your jar file, please contact the developer!");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }

        logger.log(Level.FINER, "Providing BetterLang complete!");
        return betterLang;
    }

    @Provides
    @Singleton
    public Map<String, String> provideMessages(BetterLang betterLang)
    {
        return betterLang.getMessages();
    }
}
