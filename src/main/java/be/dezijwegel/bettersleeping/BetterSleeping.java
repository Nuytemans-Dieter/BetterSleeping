package be.dezijwegel.bettersleeping;

import be.betterplugins.core.commands.BPCommandHandler;
import be.betterplugins.core.interfaces.IReloadable;
import be.dezijwegel.bettersleeping.guice.StaticModule;
import be.dezijwegel.bettersleeping.model.SleepWorldManager;
import be.dezijwegel.bettersleeping.guice.BetterSleepingModule;
import be.dezijwegel.bettersleeping.guice.UtilModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class BetterSleeping extends JavaPlugin implements IReloadable
{

    private final static Level logLevel = Level.ALL;

    private SleepWorldManager sleepWorldManager;


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

        BPCommandHandler commandHandler = injector.getInstance(BPCommandHandler.class);
        getCommand("bettersleeping").setExecutor( commandHandler );

        sleepWorldManager = injector.getInstance(SleepWorldManager.class);
    }


    @Override
    public void reload()
    {
        if (sleepWorldManager != null)
        {
            sleepWorldManager.stopRunnables();
            sleepWorldManager = null;
        }

        this.startPlugin();
    }
}
