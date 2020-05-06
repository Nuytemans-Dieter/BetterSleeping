package be.dezijwegel.bettersleeping.commands;

import be.dezijwegel.bettersleeping.interfaces.BsCommand;
import be.dezijwegel.bettersleeping.messaging.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class VersionCommand implements BsCommand {

    private final Messenger messenger;
    private final String version;


    VersionCommand(JavaPlugin plugin, Messenger messenger)
    {
        this.messenger = messenger;
        version = plugin.getDescription().getVersion();
    }


    @Override
    public boolean execute(CommandSender commandSender, Command command, String alias, String[] arguments)
    {
        messenger.sendMessage(commandSender, "You are using BetterSleeping " + version);
        return true;
    }


    @Override
    public String getPermission()
    {
        return "bettersleeping.version";
    }


    @Override
    public String getDescription()
    {
        return "Shows the BetterSleeping version";
    }


}
