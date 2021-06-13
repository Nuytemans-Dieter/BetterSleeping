package be.dezijwegel.bettersleeping.commands.bscommands;

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
        messenger.sendMessage(commandSender, "Help info placeholder");
        return true;
    }
}
