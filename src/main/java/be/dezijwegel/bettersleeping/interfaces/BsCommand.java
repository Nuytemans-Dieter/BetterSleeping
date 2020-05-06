package be.dezijwegel.bettersleeping.interfaces;

import org.bukkit.command.CommandSender;

public interface BsCommand {


    /**
     * Should contain the correct actions to execute the command
     * @param commandSender sender of the command
     * @param command the command itself
     * @param alias used alias
     * @param arguments parameters of the command
     * @return whether or not execution was successful
     */
    boolean execute(CommandSender commandSender, org.bukkit.command.Command command, String alias, String[] arguments);


    /**
     * Get the permission required for this command
     * @return permission node
     */
    String getPermission();


    /**
     * Get a description for this command
     * @return decription for this command
     */
    String getDescription();
}
