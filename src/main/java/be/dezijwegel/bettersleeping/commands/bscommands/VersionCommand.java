package be.dezijwegel.bettersleeping.commands.bscommands;

import be.dezijwegel.bettersleeping.messaging.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class VersionCommand extends BsCommand {


    private final String version;


    public VersionCommand(JavaPlugin plugin, Messenger messenger)
    {
        super( messenger );

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
