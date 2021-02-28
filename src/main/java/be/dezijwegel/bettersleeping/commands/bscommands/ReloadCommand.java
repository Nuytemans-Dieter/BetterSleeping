package be.dezijwegel.bettersleeping.commands.bscommands;

import be.dezijwegel.bettersleeping.interfaces.Reloadable;
import be.dezijwegel.bettersleeping.messaging.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommand extends BsCommand {


    private final Reloadable plugin;


    /**
     * Instance that can reload the plugin
     * @param plugin the object required to reload the plugin
     * @param messenger a Messenger object
     */
    public ReloadCommand(Reloadable plugin, Messenger messenger)
    {
        super( messenger );

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender commandSender, Command command, String alias, String[] arguments)
    {
        if (commandSender.hasPermission( getPermission() ))
        {
            plugin.reload();
            messenger.sendMessage(commandSender, "message_reloaded", true);
        }
        else
        {
            messenger.sendMessage(commandSender, "no_permission", true);
        }
        return true;
    }

    @Override
    public String getPermission()
    {
        return "bettersleeping.reload";
    }

    @Override
    public List<String> getDescription()
    {
        return new ArrayList<String>() {{
            add("Reloads config files");
            add("Also resets sleeper counts!");
        }};
    }

    @Override
    public String getDescriptionAsString() {
        return "Reloads BetterSleeping and its configuration. This WILL reset internal sleeping player counts, keep that in mind.";
    }
}
