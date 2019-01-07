package be.dezijwegel.bettersleeping;

import be.dezijwegel.commands.Reload;
import be.dezijwegel.events.OnSleepEvent;
import be.dezijwegel.events.OnSleepEventGlobal;
import be.dezijwegel.events.OnSleepEventLocal;
import be.dezijwegel.files.FileManagement;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Dieter Nuytemans
 */
public class BetterSleeping extends JavaPlugin{

    private FileManagement configFile;
    private FileManagement langFile;
    
    private OnSleepEvent onSleepEvent;
    protected Reload reload;

    LinkedList<Reloadable> reloadables;
    
   @Override
   public void onEnable()
   {
       Bukkit.getScheduler().runTaskLater(this, () -> {
           getLogger().info("Good morning!");

           configFile = new FileManagement(FileManagement.FileType.CONFIG, this);
           langFile = new FileManagement(FileManagement.FileType.LANG, this);


           configFile.saveDefaultConfig();
           langFile.saveDefaultConfig();

           LinkedList<FileManagement> files = new LinkedList<>();
           files.add(configFile);
           files.add(langFile);

           if (configFile.contains("world_specific_behavior") && configFile.getBoolean("world_specific_behavior"))
           {
               onSleepEvent = new OnSleepEventLocal(configFile, langFile, this);
           } else {
               onSleepEvent = new OnSleepEventGlobal(configFile, langFile, this);
           }

           getServer().getPluginManager().registerEvents(onSleepEvent, this);

           reloadables = new LinkedList<>();
           reloadables.add(onSleepEvent);
           //Makes sure that all values are loaded on startup
           onSleepEvent.reload();

           reload = new Reload(files, reloadables, langFile, this);

           this.getCommand("bettersleeping").setExecutor(reload);
       },1L);
   }
   
   @Override
   public void onDisable()
   {
       getLogger().info("Good night!");
   }

    /**
     * Allows the world_specific_behavior to be changed by just reloading the server
     */
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
}
