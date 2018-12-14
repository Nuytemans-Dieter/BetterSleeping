package be.dezijwegel.bettersleeping;

import be.dezijwegel.commands.Reload;
import be.dezijwegel.events.OnSleepEvent;
import be.dezijwegel.events.OnSleepEventGlobal;
import be.dezijwegel.events.OnSleepEventLocal;
import be.dezijwegel.files.FileManagement;
import java.util.LinkedList;
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
    
   @Override
   public void onEnable()
   {
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
       
       LinkedList<Reloadable> reloadables = new LinkedList<>();
       reloadables.add(onSleepEvent);
       
       reload = new Reload(files, reloadables, langFile, this);
       this.getCommand("bettersleeping").setExecutor(reload);
       //reload.reloadFiles();
   }
   
   @Override
   public void onDisable()
   {
       getLogger().info("Good night!");
   }
   
   public void reloadBehavior()
   {
       boolean worldSpecificBehavior;
       if (configFile.containsIgnoreDefault("world_specific_behavior"))
           worldSpecificBehavior = configFile.getBoolean("world_specific_behavior");
       else worldSpecificBehavior = false;
       
       HandlerList.unregisterAll(onSleepEvent);
       
       if (worldSpecificBehavior)   
           onSleepEvent = new OnSleepEventLocal(configFile, langFile, this);
       else                         
           onSleepEvent = 
                   new OnSleepEventGlobal(configFile, langFile, this);
       
       getServer().getPluginManager().registerEvents(onSleepEvent, this);
   }
}
