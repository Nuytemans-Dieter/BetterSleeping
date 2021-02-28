package be.dezijwegel.bettersleeping.commands.bscommands;

import be.dezijwegel.bettersleeping.messaging.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

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
        messenger.sendMessage(commandSender, "You are using BetterSleeping " + version, true);
        return true;
    }


    @Override
    public String getPermission()
    {
        return "bettersleeping.version";
    }


    @Override
    public List<String> getDescription()
    {
        return new ArrayList<String>() {{
            add("Shows the BetterSleeping version");
        }};
    }

    @Override
    public String getDescriptionAsString() {
        return "Shows the BetterSleeping version";
    }


}
