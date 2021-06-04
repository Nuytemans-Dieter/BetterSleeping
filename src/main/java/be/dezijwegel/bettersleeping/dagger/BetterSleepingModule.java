package be.dezijwegel.bettersleeping.dagger;

import be.betterplugins.core.messaging.logging.BPLogger;
import be.betterplugins.core.messaging.messenger.Messenger;
import be.dezijwegel.betteryaml.BetterLang;
import be.dezijwegel.betteryaml.OptionalBetterYaml;
import dagger.Module;
import dagger.Provides;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Named;
import java.util.logging.Level;

@Module
public class BetterSleepingModule
{

    private final JavaPlugin plugin;
    private Messenger messenger;

    private BetterLang betterLang;
    private OptionalBetterYaml config;
    private OptionalBetterYaml buffs;
    private OptionalBetterYaml bypassing;
    private OptionalBetterYaml hooks;
    private OptionalBetterYaml sleepingSettings;


    public BetterSleepingModule(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Provides @Named("config") OptionalBetterYaml provideConfig()
    {
        if (config == null)
            config = new OptionalBetterYaml("config.yml", plugin, true);
        return config;
    }

    @Provides @Named("buffs") OptionalBetterYaml provideBuffs()
    {
        if (buffs == null)
            buffs = new OptionalBetterYaml("config.yml", plugin, true);
        return buffs;
    }

    @Provides @Named("bypassing") OptionalBetterYaml provideBypassing()
    {
        if (bypassing == null)
            bypassing = new OptionalBetterYaml("bypassing.yml", plugin, true);
        return bypassing;
    }

    @Provides @Named("hooks") OptionalBetterYaml provideHooks()
    {
        if (hooks == null)
            hooks = new OptionalBetterYaml("hooks.yml", plugin, true);
        return hooks;
    }

    @Provides @Named("sleeping_settings") OptionalBetterYaml provideSleepingSettings()
    {
        if (sleepingSettings == null)
            sleepingSettings = new OptionalBetterYaml("sleeping_settings.yml", plugin, true);
        return sleepingSettings;
    }

    @Provides BetterLang provideBetterLang()
    {
        if (betterLang == null)
        {
            OptionalBetterYaml config = provideConfig();
            YamlConfiguration section = config.getYamlConfiguration().orElse(new YamlConfiguration());
            String configValue = section.getString("language");
            String language = configValue != null ? configValue.toLowerCase() : "en-us";
            betterLang = new BetterLang("lang.yml", language + ".yml", plugin);
        }
        return betterLang;
    }

//    @Provides Messenger provideMessenger()
//    {
//        if (messenger == null)
//        {
//            BetterLang lang = provideBetterLang();
//            messenger = new Messenger(lang.getMessages(), new BPLogger(Level.FINEST), "temp");
//        }
//        return messenger;
//    }

}
