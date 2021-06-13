package be.dezijwegel.bettersleeping;

import be.betterplugins.core.commands.BPCommandHandler;
import be.betterplugins.core.interfaces.IReloadable;
import be.dezijwegel.bettersleeping.guice.BetterSleepingModule;
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

        Injector injector = Guice.createInjector(
                new BetterSleepingModule(this, Level.ALL)
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
