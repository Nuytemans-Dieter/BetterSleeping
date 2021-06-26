package be.betterplugins.bettersleeping.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.List;

public class StaticModule extends AbstractModule
{
    @Provides
    public List<World> provideAllWorlds()
    {
        return Bukkit.getWorlds();
    }

}
