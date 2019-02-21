package be.dezijwegel.bettersleeping;

import be.dezijwegel.commands.Reload;
import java.util.LinkedList;

import be.dezijwegel.commands.TabCompletion;
import be.dezijwegel.events.OnSleepEvent;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Dieter Nuytemans
 */
public class BetterSleeping extends JavaPlugin implements Reloadable{

    public static boolean debug = false;

    private OnSleepEvent onSleepEvent;
    private Reload reload;

    private LinkedList<Reloadable> reloadables;
    
   @Override
   public void onEnable()
   {
       startPlugin();
   }

    @Override
    public void reload() {
        HandlerList.unregisterAll();
        startPlugin();
    }

    public void startPlugin()
    {
        Management management = new Management(this);

        onSleepEvent = new OnSleepEvent(management, this);
        getServer().getPluginManager().registerEvents(onSleepEvent, this);

        reloadables = new LinkedList<Reloadable>();
        reloadables.add(this);
        reload = new Reload(reloadables, management, this);

        this.getCommand("bettersleeping").setExecutor(reload);
        this.getCommand("bettersleeping").setTabCompleter(new TabCompletion());
    }

}
