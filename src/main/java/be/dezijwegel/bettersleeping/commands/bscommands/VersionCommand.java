package be.dezijwegel.bettersleeping.commands.bscommands;

import be.betterplugins.core.commands.BPCommand;
import be.betterplugins.core.messaging.messenger.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class VersionCommand extends BPCommand
{


    private final String version;


    public VersionCommand(JavaPlugin plugin, Messenger messenger)
    {
        super( messenger );

        version = plugin.getDescription().getVersion();
    }

    @Override
    public @NotNull String getCommandName()
    {
        return "version";
    }

    @Override
    public @NotNull List<String> getAliases()
    {
        return Collections.singletonList("v");
    }

    @Override
    public @NotNull String getPermission()
    {
        return "bettersleeping.version";
    }

    @Override
    public boolean mayExecute(CommandSender commandSender)
    {
        return true;
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String[] strings)
    {
        messenger.sendMessage(commandSender, "You are using BetterSleeping " + version);
        return true;
    }

}
