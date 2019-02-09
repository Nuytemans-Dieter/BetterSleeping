package be.dezijwegel.bettersleeping;

import be.dezijwegel.commands.Reload;
import java.util.LinkedList;

import be.dezijwegel.events.OnSleepEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Dieter Nuytemans
 */
public class BetterSleeping extends JavaPlugin{

    public static boolean debug = true;

    private OnSleepEvent onSleepEvent;
    protected Reload reload;

    LinkedList<Reloadable> reloadables;
    
   @Override
   public void onEnable()
   {
       Management management = new Management(this);
       onSleepEvent = new OnSleepEvent(management, this);

       getServer().getPluginManager().registerEvents(onSleepEvent, this);

       reloadables = new LinkedList<>();
       //Makes sure that all values are loaded on startup

       //reload = new Reload(files, reloadables, langFile, this);

       //this.getCommand("bettersleeping").setExecutor(reload);
   }

    /**
     * Allows the world_specific_behavior to be changed by just reloading the server
     */
    /*
   public void reloadBehavior()
   {
       boolean worldSpecificBehavior;
       if (configFile.containsIgnoreDefault("world_specific_behavior"))
           worldSpecificBehavior = configFile.getBoolean("world_specific_behavior");
       else worldSpecificBehavior = false;
       
       HandlerList.unregisterAll(onSleepEvent);
       reloadables.remove(onSleepEvent);
       
       if (worldSpecificBehavior)   
           onSleepEvent = new OnSleepEventLocal(configFile, langFile, this);
       else                         
           onSleepEvent = new OnSleepEventGlobal(configFile, langFile, this);
       
       getServer().getPluginManager().registerEvents(onSleepEvent, this);
       reloadables.add(onSleepEvent);
   }
   */
}
