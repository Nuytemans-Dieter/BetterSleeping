package be.betterplugins.bettersleeping;

import be.betterplugins.bettersleeping.guice.BetterSleepingModule;
import be.betterplugins.bettersleeping.guice.StaticModule;
import be.betterplugins.bettersleeping.guice.UtilModule;
import be.betterplugins.bettersleeping.listeners.BedEventListener;
import be.betterplugins.bettersleeping.listeners.BuffsHandler;
import be.betterplugins.bettersleeping.model.SleepWorldManager;
import be.betterplugins.bettersleeping.model.WorldState;
import be.betterplugins.bettersleeping.model.WorldStateHandler;
import be.betterplugins.core.commands.BPCommandHandler;
import be.betterplugins.core.interfaces.IReloadable;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class BetterSleeping extends JavaPlugin implements IReloadable
{

    private final static Level logLevel = Level.ALL;

    private SleepWorldManager sleepWorldManager;
    private WorldStateHandler worldStateHandler;

    @Override
    public void onEnable()
    {
        super.onEnable();

        // Before all else: check if bettersleeping4 folder does NOT exist yet and bettersleeping3 folder DOES exist
        // If this is met: automatically migrate BS3 options to the BS4 config files

        this.startPlugin();
    }


    private void startPlugin()
    {
        Injector injector = Guice.createInjector(
                new BetterSleepingModule(this, logLevel),
                new UtilModule(),
                new StaticModule()
        );

        // Capture the state of all worlds
        this.worldStateHandler = injector.getInstance( WorldStateHandler.class );

        // Handle commands
        BPCommandHandler commandHandler = injector.getInstance(BPCommandHandler.class);
        getCommand("bettersleeping").setExecutor( commandHandler );

        // Register events
        BedEventListener bedEventListener = injector.getInstance(BedEventListener.class);
        BuffsHandler buffsHandler = injector.getInstance(BuffsHandler.class);

        Bukkit.getServer().getPluginManager().registerEvents( bedEventListener, this );
        Bukkit.getServer().getPluginManager().registerEvents( buffsHandler, this );

        // Handle sleeping through a runnable
        sleepWorldManager = injector.getInstance(SleepWorldManager.class);

        // Disable daylightcycle in all worlds
        this.worldStateHandler.setWorldStates( new WorldState( false ));
    }


    @Override
    public void onDisable()
    {
        // Unregister all listeners from this plugin
        HandlerList.unregisterAll(this);

        // Stop all time handling runnables
        if (sleepWorldManager != null)
        {
            sleepWorldManager.stopRunnables();
            sleepWorldManager = null;
        }

        // Reset all world states to their normal values
        if (worldStateHandler != null)
        {
            worldStateHandler.revertWorldStates();
            worldStateHandler = null;
        }
    }

    @Override
    public void reload()
    {
        this.onDisable();
        this.startPlugin();
    }
}
