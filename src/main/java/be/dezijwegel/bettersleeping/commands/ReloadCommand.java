package be.dezijwegel.bettersleeping.commands;

import be.dezijwegel.bettersleeping.BetterSleeping;
import be.dezijwegel.bettersleeping.interfaces.BsCommand;
import be.dezijwegel.bettersleeping.interfaces.Reloadable;
import be.dezijwegel.bettersleeping.messenger.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements BsCommand {


    private final Reloadable plugin;
    private final Messenger messenger;


    /**
     * Instance that can reload the plugin
     * @param plugin the object required to reload the plugin
     * @param messenger a Messenger object
     */
    public ReloadCommand(Reloadable plugin, Messenger messenger)
    {
        this.plugin = plugin;
        this.messenger = messenger;
    }

    @Override
    public boolean execute(CommandSender commandSender, Command command, String alias, String[] arguments)
    {
        if (commandSender.hasPermission( getPermission() ))
        {
            plugin.reload();
            messenger.sendMessage(commandSender, "message_reloaded");
        }
        else
        {
            messenger.sendMessage(commandSender, "no_permission");
        }
        return true;
    }

    @Override
    public String getPermission()
    {
        return "bettersleeping.reload";
    }

    @Override
    public String getDescription()
    {
        return "Reloads BetterSleeping and its configuration. This WILL reset internal sleeping player counts, keep that in mind.";
    }
}
