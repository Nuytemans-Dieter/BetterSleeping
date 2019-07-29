package be.dezijwegel;

import be.dezijwegel.commands.Reload;
import be.dezijwegel.commands.TabCompletion;
import be.dezijwegel.events.OnPhantomSpawnEvent;
import be.dezijwegel.events.OnSleepEvent;
import be.dezijwegel.interfaces.Reloadable;
import be.dezijwegel.management.Management;
import be.dezijwegel.placeholderAPI.BetterSleepingExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;

/**
 *
 * @author Dieter Nuytemans
 */
public class BetterSleeping extends JavaPlugin implements Reloadable {

    public static boolean debug = false;

    private OnSleepEvent onSleepEvent;
    private OnPhantomSpawnEvent onPhantomSpawnEvent;
    private Reload reload;

    private LinkedList<Reloadable> reloadables;
    
    @Override
    public void onEnable()
   {
       startPlugin();
   }

    @Override
    public void reload() {
        HandlerList.unregisterAll(this);
        startPlugin();
    }

    public void startPlugin()
    {
        if (BetterSleeping.debug)
        {
            Bukkit.getLogger().info("-----");
            Bukkit.getLogger().info("Starting BetterSleeping in debugging mode...");
            Bukkit.getLogger().info("-----");
        }
        Management management = new Management(this);

        onSleepEvent = new OnSleepEvent(management, this);
        onPhantomSpawnEvent = new OnPhantomSpawnEvent(management);
        getServer().getPluginManager().registerEvents(onSleepEvent, this);
        getServer().getPluginManager().registerEvents(onPhantomSpawnEvent, this);

        reloadables = new LinkedList<Reloadable>();
        reloadables.add(this);
        reload = new Reload(reloadables, management, this);

        // If PlaceholderAPI is registered
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new BetterSleepingExpansion(this, management, onSleepEvent.getSleepTracker()).register();
            Bukkit.getConsoleSender().sendMessage("[BetterSleeping] " + ChatColor.GREEN + "Succesfully hooked into PlaceholderAPI!");
        }

        this.getCommand("bettersleeping").setExecutor(reload);
        this.getCommand("bettersleeping").setTabCompleter(new TabCompletion());
    }

}
