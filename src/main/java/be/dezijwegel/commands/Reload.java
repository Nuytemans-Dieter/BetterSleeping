package be.dezijwegel.commands;

import be.dezijwegel.BetterSleeping;
import be.dezijwegel.management.Management;
import be.dezijwegel.interfaces.Reloadable;
import java.util.LinkedList;

import static org.bukkit.Bukkit.getConsoleSender;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Dieter Nuytemans
 */
public class Reload implements CommandExecutor {
    
    private LinkedList<Reloadable> reloadables;

    private Management management;
    private BetterSleeping plugin;

    
    /**
     * Creates an object that reloads given objects IN ORDER!!
     * @param reloadables
     * @param management
     * @param plugin
     */
    public Reload (LinkedList<Reloadable> reloadables, Management management, BetterSleeping plugin)
    {
        this.management = management;
        this.reloadables = reloadables;
        this.plugin = plugin;
    }
    
    /**
     * Reload all files that were added previously
     */
    public void reloadFiles()
    {
        for (Reloadable rel : reloadables)
        {
            rel.reload();
        }
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {

        if (cmnd.getName().equalsIgnoreCase("bettersleeping"))
        {
            if (strings.length > 0)
            {
                if (strings[0].equalsIgnoreCase("reload"))
                {
                    if (!(cs instanceof Player) || cs.isOp() || cs.hasPermission("bettersleeping.reload")) {
                        reloadFiles();
                        if (cs instanceof Player)
                            {management.sendMessage("message_reloaded", cs);}
                        management.sendMessage("message_reloaded", getConsoleSender());
                    } else {
                        management.sendMessage("no_permission", cs);
                    }
                    return true;
                }
            }
        }

        return false;
    }
}
