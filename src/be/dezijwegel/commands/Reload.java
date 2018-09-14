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
        
        prefix = langFile.getString("prefix");
        message_reloaded = langFile.getString("message_reloaded");
        no_permission = langFile.getString("no_permission");
    }
    
    /**
     * Reload all files that were added previously
     */
    public void reloadFiles()
    {
        if (files.size() > 0)
        {
            files.stream().forEach((file) -> {
                file.reloadFile();
            });
        }
        
        if (reloadables.size() > 0) {
            reloadables.stream().forEach((reloadable) -> {
                reloadable.reload();
            });
        }
        
        prefix = langFile.getString("prefix");
        message_reloaded = langFile.getString("message_reloaded");
        no_permission = langFile.getString("no_permission");
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
                            sender.sendMessage(prefix + message_reloaded);
                            getLogger().log(Level.INFO, "[BetterSleeping] {0}", message_reloaded);
                        } else sender.sendMessage(prefix + no_permission);       
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
