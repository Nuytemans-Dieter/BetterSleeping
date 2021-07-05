package be.betterplugins.bettersleeping;

import be.betterplugins.bettersleeping.api.BetterSleepingAPI;
import be.betterplugins.bettersleeping.guice.BetterSleepingModule;
import be.betterplugins.bettersleeping.guice.StaticModule;
import be.betterplugins.bettersleeping.guice.UtilModule;
import be.betterplugins.bettersleeping.listeners.BedEventListener;
import be.betterplugins.bettersleeping.listeners.BuffsHandler;
import be.betterplugins.bettersleeping.listeners.PhantomHandler;
import be.betterplugins.bettersleeping.listeners.TimeSetToDayCounter;
import be.betterplugins.bettersleeping.model.SleepWorldManager;
import be.betterplugins.bettersleeping.model.WorldState;
import be.betterplugins.bettersleeping.model.WorldStateHandler;
import be.betterplugins.bettersleeping.util.BStatsHandler;
import be.betterplugins.core.commands.BPCommandHandler;
import be.betterplugins.core.interfaces.IReloadable;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public class BetterSleeping extends JavaPlugin implements IReloadable
{

    private final static Level logLevel = Level.ALL;

    private SleepWorldManager sleepWorldManager;
    private WorldStateHandler worldStateHandler;

    private static BetterSleepingAPI API;

    /**
     * Get the publicly accessible API of BetterSleeping
     *
     * @return the BetterSleepingAPI instance. Null if something went wrong while enabling BetterSleeping
     */
    public static @Nullable BetterSleepingAPI getAPI()
    {
        return BetterSleeping.API;
    }

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

        registerEvents(
            injector.getInstance(BedEventListener.class),
            injector.getInstance(BuffsHandler.class),
            injector.getInstance(TimeSetToDayCounter.class),
            injector.getInstance(PhantomHandler.class)
        );

        // Handle sleeping through a runnable
        sleepWorldManager = injector.getInstance(SleepWorldManager.class);

        // Disable daylightcycle in all worlds
        this.worldStateHandler.setWorldStates( new WorldState( false ));

        // Load the BetterSleeping API
        BetterSleeping.API = injector.getInstance(BetterSleepingAPI.class);

        // Enable bStats
        injector.getInstance(BStatsHandler.class);
    }

    /**
     * Register all provided Listeners
     *
     * @param listeners all listeners to be registered
     */
    private void registerEvents(Listener... listeners)
    {
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        for (Listener listener : listeners)
            pluginManager.registerEvents( listener, this );
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

        // Reset the API
        BetterSleeping.API = null;
    }

    @Override
    public void reload()
    {
        this.onDisable();
        this.startPlugin();
    }
}
