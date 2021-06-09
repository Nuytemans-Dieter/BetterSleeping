package be.dezijwegel.bettersleeping;

import be.betterplugins.core.messaging.logging.BPLogger;
import be.betterplugins.core.messaging.messenger.Messenger;
import be.dezijwegel.bettersleeping.guice.BetterSleepingModule;
import be.dezijwegel.betteryaml.BetterLang;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class BetterSleeping extends JavaPlugin
{

    @Override
    public void onEnable()
    {
        super.onEnable();

        Injector injector = Guice.createInjector(
                new BetterSleepingModule(this, Level.ALL)
        );

        BPLogger logger = injector.getInstance(BPLogger.class);
        BetterLang lang = injector.getInstance(BetterLang.class);
//        Messenger messenger = injector.getInstance(Messenger.class);
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
    }
}
