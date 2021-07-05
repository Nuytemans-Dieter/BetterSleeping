package be.betterplugins.bettersleeping.commands;

import be.betterplugins.core.commands.BPCommand;
import be.betterplugins.core.messaging.messenger.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class HelpCommand extends BPCommand
{

    public HelpCommand(Messenger messenger)
    {
        super(messenger);
    }

    @Override
    public @NotNull String getCommandName()
    {
        return "help";
    }

    @Override
    public @NotNull List<String> getAliases()
    {
        return Collections.singletonList("h");
    }

    @Override
    public @NotNull String getPermission()
    {
        return "bettersleeping.help";
    }

    @Override
    public boolean mayExecute(CommandSender commandSender)
    {
        return true;
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String[] strings)
    {
        messenger.sendMessage(commandSender, "command_help_header");
        messenger.sendMessage(commandSender, "command_help_buffs");
        messenger.sendMessage(commandSender, "command_help_reload");
        messenger.sendMessage(commandSender, "command_help_shout");
        messenger.sendMessage(commandSender, "command_help_sleep");
        messenger.sendMessage(commandSender, "command_help_status");
        messenger.sendMessage(commandSender, "command_help_version");
        messenger.sendMessage(commandSender, "command_help_footer");
        return true;
    }
}
