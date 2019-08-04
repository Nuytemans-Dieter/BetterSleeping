package be.dezijwegel.commands;

import be.dezijwegel.management.Management;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.ArrayList;
import java.util.List;

public class Help implements be.dezijwegel.interfaces.Command {

    private List<CommandInfo> commandList;
    private Management management;

    public Help(Management management)
    {
        this.management = management;

        commandList = new ArrayList<>();
        commandList.add( new CommandInfo("/bs reload", "Reloads all BetterSleeping configuration files.", "bettersleeping.reload"));
        commandList.add( new CommandInfo("Not a command", "Players with this permission will not be able to sleep and won't be counted towards required sleeping players.", "bettersleeping.bypass or essentials.sleepingignored"));
        commandList.add( new CommandInfo("/bs skip", "If all players in your world have bypass permissions, the night will be skipped instantly.", "bettersleeping.bypass or essentials.sleepingignored"));
        commandList.add( new CommandInfo("/bs status or /bs s", "This will display info about the current sleeping players", "bettersleeping.status") );
    }

    @Override
    public boolean execute(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (cs instanceof ConsoleCommandSender || cs.hasPermission("bettersleeping.help.user") || cs.hasPermission("bettersleeping.help.admin") || cs.hasPermission("bettersleeping.help"))
        {
            cs.sendMessage(ChatColor.GOLD + "---= BetterSleeping help =---");
            for (CommandInfo info : commandList)
            {
                cs.sendMessage(ChatColor.DARK_AQUA + "Command: " + ChatColor.WHITE + info.command);
                cs.sendMessage(ChatColor.DARK_AQUA + "Description: " + ChatColor.WHITE + info.description);
                if (cs.hasPermission("bettersleeping.help.admin"))
                {
                    cs.sendMessage(ChatColor.DARK_AQUA + "Permission: " + ChatColor.WHITE + info.permission);
                }
                cs.sendMessage(ChatColor.GOLD + "---==---");
            }
        } else {
            management.sendMessage("no_permission", cs);
        }

        return true;
    }

    private class CommandInfo {

        public String command;
        public String description;
        public String permission;

        public CommandInfo(String command, String description, String permission){
            this.command = command;
            this.description = description;
            this.permission = permission;
        }

    }

}

