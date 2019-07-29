package be.dezijwegel.interfaces;

import org.bukkit.command.CommandSender;

public interface Command {

    /**
     * Should contain the correct actions to execute the command
     * @param cs
     * @param cmnd
     * @param string
     * @param strings
     * @return
     */
    boolean execute(CommandSender cs, org.bukkit.command.Command cmnd, String string, String[] strings);

}
