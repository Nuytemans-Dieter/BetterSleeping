package be.dezijwegel.bettersleeping.commands.bscommands;

import be.dezijwegel.bettersleeping.messaging.Messenger;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class BsCommand {


    final Messenger messenger;


    public BsCommand( Messenger messenger )
    {
        this.messenger = messenger;
    }

    /**
     * Should contain the correct actions to execute the command
     * @param commandSender sender of the command
     * @param command the command itself
     * @param alias used alias
     * @param arguments parameters of the command
     * @return whether or not execution was successful
     */
    abstract public boolean execute(CommandSender commandSender, org.bukkit.command.Command command, String alias, String[] arguments);


    /**
     * Get the permission required for this command
     * @return permission node
     */
    abstract public String getPermission();

    abstract public List<String> getDescription();

    /**
     * Get a description for this command
     * @return decription for this command
     */
    abstract public String getDescriptionAsString();
}
