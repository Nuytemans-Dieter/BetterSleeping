package be.betterplugins.bettersleeping;

import be.betterplugins.bettersleeping.api.BetterSleepingAPI;
import be.betterplugins.bettersleeping.guice.BetterSleepingModule;
import be.betterplugins.bettersleeping.guice.StaticModule;
import be.betterplugins.bettersleeping.guice.UtilModule;
import be.betterplugins.bettersleeping.hooks.GSitListener;
import be.betterplugins.bettersleeping.hooks.PapiExpansion;
import be.betterplugins.bettersleeping.listeners.*;
import be.betterplugins.bettersleeping.model.ConfigContainer;
import be.betterplugins.bettersleeping.model.sleeping.SleepWorldManager;
import be.betterplugins.bettersleeping.model.world.WorldState;
import be.betterplugins.bettersleeping.model.world.WorldStateHandler;
import be.betterplugins.bettersleeping.runnables.BossBarRunnable;
import be.betterplugins.bettersleeping.util.BStatsHandler;
import be.betterplugins.bettersleeping.util.FileLogger;
import be.betterplugins.bettersleeping.util.migration.SettingsMigrator;
import be.betterplugins.core.commands.BPCommandHandler;
import be.betterplugins.core.interfaces.IReloadable;
import be.betterplugins.core.messaging.logging.BPLogger;
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

    private final static Level logLevel = Level.INFO;

    private BPLogger logger;
    private SleepWorldManager sleepWorldManager;
    private BossBarRunnable bossBarRunnable;
    private WorldStateHandler worldStateHandler;
    private AnimationHandler animationHandler;

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

        // Automatically migrate BS3 options to the BS4 config files if needed
        new SettingsMigrator(this, new BPLogger(Level.CONFIG, "BetterSleeping4"));

        this.startPlugin();
    }


    private void startPlugin()
    {
        Injector injector = Guice.createInjector(
                new BetterSleepingModule(this, logLevel),
                new UtilModule(),
                new StaticModule()
        );

        this.logger = injector.getInstance(BPLogger.class);

        // Get all configuration
        ConfigContainer config = injector.getInstance(ConfigContainer.class);

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
            injector.getInstance(PhantomHandler.class),
            injector.getInstance(AnimationHandler.class)
        );

        // Handle sleeping through a runnable
        sleepWorldManager = injector.getInstance(SleepWorldManager.class);

        // Disable daylightcycle in all worlds
        this.worldStateHandler.setWorldStates( new WorldState( false ));

        // Handle the boss bar
        boolean enableBossBar = config.getConfig().getBoolean("enable_bossbar");
        if (enableBossBar)
        {
            this.bossBarRunnable = injector.getInstance(BossBarRunnable.class);
            this.bossBarRunnable.runTaskTimer(this, 20L, 5L);
        }

        // Handle GSit events
        boolean doSupportGSit = config.getHooks().getBoolean("enable_gsit_support");
        boolean hasGSit = getServer().getPluginManager().getPlugin("GSit") != null;
        if (doSupportGSit && hasGSit)
        {
            this.getServer().getPluginManager().registerEvents( injector.getInstance(GSitListener.class), this );
        }
        else if (hasGSit)
        {
            logger.log(Level.INFO, "You are using GSit but did not opt-in to enable GSit support with BetterSleeping!");
        }

        // Load the BetterSleeping API
        BetterSleeping.API = injector.getInstance(BetterSleepingAPI.class);

        // Register the PAPI expansion
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
        {
            injector.getInstance(PapiExpansion.class).register();
        }

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

        // Stop handling animations
        if (animationHandler != null)
        {
            animationHandler.reload();
            animationHandler = null;
        }

        // Stop handling bossbars
        if (bossBarRunnable != null)
        {
            bossBarRunnable.stopBossBars();
            bossBarRunnable = null;
        }

        if (logger != null && logger instanceof FileLogger)
        {
            ((FileLogger) logger).close();
            logger = null;
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
