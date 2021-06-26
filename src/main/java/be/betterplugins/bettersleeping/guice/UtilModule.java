package be.betterplugins.bettersleeping.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import javax.inject.Named;

public class UtilModule extends AbstractModule
{

    @Provides
    @Singleton
    @Named("has_spigot")
    public boolean provideHasSpigot()
    {
        boolean hasSpigot;
        try
        {
            Class.forName("org.spigotmc.SpigotConfig");
            hasSpigot = true;
        }
        catch (ClassNotFoundException ignored)
        {
            hasSpigot = false;
        }

        return hasSpigot;
    }

}
