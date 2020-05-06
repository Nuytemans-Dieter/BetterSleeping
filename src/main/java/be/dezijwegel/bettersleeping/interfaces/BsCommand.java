package be.dezijwegel.bettersleeping.interfaces;

import org.bukkit.command.CommandSender;

public interface BsCommand {

    /**
     * Should contain the correct actions to execute the command
     * @param cs sender of the command
     * @param cmnd the command itself
     * @param string used alias
     * @param strings parameters of the command
     * @return whether or not execution was successful
     */
    boolean execute(CommandSender cs, org.bukkit.command.Command cmnd, String string, String[] strings);

    /**
     * Get the permission required for this command
     * @return permission node
     */
    String getPermission();
}
