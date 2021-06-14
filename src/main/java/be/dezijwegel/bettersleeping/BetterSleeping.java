package be.dezijwegel.bettersleeping;

import be.betterplugins.core.commands.BPCommandHandler;
import be.betterplugins.core.interfaces.IReloadable;
import be.dezijwegel.bettersleeping.guice.BetterSleepingModule;
import be.dezijwegel.bettersleeping.guice.UtilModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class BetterSleeping extends JavaPlugin implements IReloadable
{

    @Override
    public void onEnable()
    {
        super.onEnable();

        // Before all else: check if bettersleeping4 folder does NOT exist yet and bettersleeping3 folder DOES exist
        // If this is met: automatically migrate BS3 options to the BS4 config files

        Injector injector = Guice.createInjector(
                new BetterSleepingModule(this, Level.ALL),
                new UtilModule()
        );

        BPCommandHandler commandHandler = injector.getInstance(BPCommandHandler.class);
        getCommand("bettersleeping").setExecutor( commandHandler );
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
    }

    @Override
    public void reload()
    {
        // TODO: Handle reloading
    }
}
