package be.dezijwegel.bettersleeping;

import be.dezijwegel.commands.Reload;
import be.dezijwegel.events.OnSleepEvent;
import be.dezijwegel.files.FileManagement;
import java.util.LinkedList;
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

       onSleepEvent = new OnSleepEvent(configFile, langFile, this);
       getServer().getPluginManager().registerEvents(onSleepEvent, this);
       
       LinkedList<Reloadable> reloadables = new LinkedList<>();
       reloadables.add(onSleepEvent);
       
       reload = new Reload(files, reloadables, langFile);
       this.getCommand("bettersleeping").setExecutor(reload);
       reload.reloadFiles();
   }
   
   @Override
   public void onDisable()
   {
       getLogger().info("Good night!");
   }
    
}
