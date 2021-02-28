package be.dezijwegel.bettersleeping.commands.bscommands;

import be.dezijwegel.bettersleeping.messaging.Messenger;
import be.dezijwegel.bettersleeping.messaging.MsgEntry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class HelpCommand extends BsCommand {


    final Map<String, BsCommand> commands;


    public HelpCommand(Messenger messenger, Map<String, BsCommand> commands)
    {
        super( messenger );

        this.commands = commands;
    }


    @Override
    public boolean execute(CommandSender commandSender, Command command, String alias, String[] arguments)
    {
        if ( ! commandSender.hasPermission( getPermission() ))
        {
            messenger.sendMessage(commandSender, "no_permission", true, new MsgEntry("<var>", "/bs " + arguments[0]));
            return true;
        }

        // Not using the Messenger class because of optimizations. No checks are needed for these messages
        commandSender.sendMessage(ChatColor.GOLD + "--=={BetterSleeping help}==--");

        if (arguments.length < 2)
        {
            for (Map.Entry<String, BsCommand> entry : commands.entrySet())
            {
                BsCommand cmd = entry.getValue();
                if (commandSender.hasPermission( cmd.getPermission() ) || commandSender.hasPermission("bettersleeping.help.admin"))
                    sendCommandInfo(commandSender, entry.getKey(), entry.getValue());
            }
        }
        else if (commands.containsKey(arguments[1].toLowerCase()))
        {
            String cmdName = arguments[1].toLowerCase();
            BsCommand cmd = commands.get( cmdName );
            if (commandSender.hasPermission( cmd.getPermission() ))
            {
                commandSender.sendMessage(ChatColor.DARK_AQUA + "Showing help for: /bs " + cmdName);
                sendCommandInfo(commandSender, cmdName, cmd);
            }
            else
            {
                commandSender.sendMessage(ChatColor.RED + "You do not have access to execute this command: /bs " + cmdName);
            }
        }
        else
        {
            commandSender.sendMessage(ChatColor.RED + "The command '/bs " + arguments[1] + "' was not found. Execute /bs help to see a list of commands");
        }

        return true;
    }


    /**
     * Send command info on a particular command
     * @param commandSender the receiver for the help message
     * @param commandName the name of this command, as follows: /bs <commandName>
     * @param command a BsCommand instance
     */
    private void sendCommandInfo(CommandSender commandSender, String commandName, BsCommand command)
    {
        commandSender.sendMessage(ChatColor.GOLD + "Command: " + ChatColor.DARK_AQUA + "/bs " + commandName);
        commandSender.sendMessage(ChatColor.GOLD + "Description: " + ChatColor.DARK_AQUA + command.getDescription());
        if (commandSender.hasPermission("bettersleeping.help.admin"))
            commandSender.sendMessage(ChatColor.GOLD + "Permission: " + ChatColor.DARK_AQUA + command.getPermission());
        commandSender.sendMessage(ChatColor.GOLD + "---===---");
    }


    @Override
    public String getPermission()
    {
        return "bettersleeping.help";
    }

    @Override
    public String getDescription()
    {
        return "Shows all commands a person has access to. Admins will also see the corresponding permissions.";
    }
}
