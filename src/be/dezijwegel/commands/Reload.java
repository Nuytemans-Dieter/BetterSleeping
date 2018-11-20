package be.dezijwegel.commands;

import be.dezijwegel.bettersleeping.Reloadable;
import be.dezijwegel.files.FileManagement;
import java.util.LinkedList;
import java.util.logging.Level;
import static org.bukkit.Bukkit.getLogger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Dieter Nuytemans
 */
public class Reload implements CommandExecutor {
    
    LinkedList<FileManagement> files;
    LinkedList<Reloadable> reloadables;
    
    private FileManagement langFile;
    
    private String prefix;
    private String message_reloaded;
    private String no_permission;
    
    /**
     * Set files that can be easily reloaded
     * @param files
     * @param reloadables
     * @param langFile
     */
    public Reload (LinkedList<FileManagement> files, LinkedList<Reloadable> reloadables, FileManagement langFile)
    {
        this.files = files;
        this.reloadables = reloadables;
        this.langFile = langFile;
        
        if (langFile.contains("prefix"))
            prefix = langFile.getString("prefix");
        else prefix = "§6[BetterSleeping] §3";
        
        if (langFile.contains("message_reloaded"))
            message_reloaded = langFile.getString("message_reloaded");
        else message_reloaded = "Reload complete!";
        
        if (langFile.contains("no_permission"))
            no_permission = langFile.getString("no_permission");
        else no_permission = "§4You don't have permission to execute that command!";
    }
    
    /**
     * Reload all files that were added previously
     */
    public void reloadFiles()
    {
        if (files.size() > 0)
        {
            for (FileManagement file : files)
            {
                file.reloadFile();
            }
        }
        
        if (reloadables.size() > 0) {
            for (Reloadable rel : reloadables)
            {
                rel.reload();
            }
        }
        
        if (langFile.contains("prefix"))
            prefix = langFile.getString("prefix");
        else prefix = "§6[BetterSleeping] §3";
        
        if (langFile.contains("message_reloaded"))
            message_reloaded = langFile.getString("message_reloaded");
        else message_reloaded = "Reload complete!";
        
        if (langFile.contains("no_permission"))
            no_permission = langFile.getString("no_permission");
        else no_permission = "§4You don't have permission to execute that command!";
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (cs instanceof Player)
        {
            Player sender = (Player) cs;
            if (string.equalsIgnoreCase("bettersleeping"))
            {
                if (strings.length > 0)
                {
                    if (strings[0].equalsIgnoreCase("reload"))
                    {
                        if (sender.isOp() || sender.hasPermission("bettersleeping.reload")) {
                            reloadFiles();
                            if (!message_reloaded.equalsIgnoreCase("ignored"))
                                sender.sendMessage(prefix + message_reloaded);
                            getLogger().log(Level.INFO, "[BetterSleeping] {0}", message_reloaded);
                        } else {
                            if (!no_permission.equalsIgnoreCase("ignored"))
                                sender.sendMessage(prefix + no_permission);
                        }       
                        return true;
                    }
                }
            }
        } else {
            if (string.equalsIgnoreCase("bettersleeping"))
            {
                if (strings.length > 0)
                {
                    if (strings[0].equalsIgnoreCase("reload"))
                    {
                        reloadFiles();
                        getLogger().log(Level.INFO, "[BetterSleeping] {0}", message_reloaded);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
