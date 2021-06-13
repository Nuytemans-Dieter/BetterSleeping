package be.dezijwegel.bettersleeping.commands.bscommands;

import be.betterplugins.core.commands.BPCommand;
import be.betterplugins.core.interfaces.IReloadable;
import be.betterplugins.core.messaging.messenger.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ReloadCommand extends BPCommand
{


    private final IReloadable plugin;


    /**
     * Instance that can reload the plugin
     * @param plugin the object required to reload the plugin
     * @param messenger a Messenger object
     */
    public ReloadCommand(IReloadable plugin, Messenger messenger)
    {
        super( messenger );

        this.plugin = plugin;
    }

    @Override
    public @NotNull String getCommandName()
    {
        return "reload";
    }

    @Override
    public @NotNull List<String> getAliases()
    {
        return Collections.singletonList("r");
    }

    @Override
    public @NotNull String getPermission()
    {
        return "bettersleeping.reload";
    }

    @Override
    public boolean mayExecute(CommandSender commandSender)
    {
        return commandSender instanceof ConsoleCommandSender;
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String[] strings)
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
}
