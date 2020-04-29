package be.dezijwegel.commands;

import be.dezijwegel.BetterSleeping;
import be.dezijwegel.interfaces.BsCommand;
import be.dezijwegel.interfaces.Reloadable;
import be.dezijwegel.management.Management;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;

import static org.bukkit.Bukkit.getConsoleSender;

/**
 *
 * @author Dieter Nuytemans
 */
public class Reload implements BsCommand {
    
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

    @Override
    public boolean execute(CommandSender cs, Command cmnd, String string, String[] strings) {
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

        return false;
    }
}
