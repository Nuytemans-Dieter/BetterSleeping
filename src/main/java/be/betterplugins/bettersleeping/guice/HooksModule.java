package be.betterplugins.bettersleeping.guice;

import be.betterplugins.bettersleeping.hooks.EssentialsHook;
import be.betterplugins.bettersleeping.hooks.NoEssentialsHook;
import be.betterplugins.bettersleeping.model.ConfigContainer;
import be.betterplugins.core.messaging.logging.BPLogger;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.bukkit.Bukkit;

public class HooksModule extends AbstractModule
{

    @Provides
    @Singleton
    public EssentialsHook provideEssentialsHook(ConfigContainer config, BPLogger logger)
    {
        if (Bukkit.getServer().getPluginManager().getPlugin("Essentials") != null)
        {
            return new EssentialsHook(config, logger);
        }
        else
        {
            return new NoEssentialsHook(config, logger);
        }
    }

}
