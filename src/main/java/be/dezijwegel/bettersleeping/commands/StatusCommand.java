package be.dezijwegel.bettersleeping.commands;

import be.dezijwegel.bettersleeping.interfaces.BsCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class StatusCommand implements BsCommand {
    @Override
    public boolean execute(CommandSender commandSender, Command command, String alias, String[] arguments)
    {
        return false;
    }

    @Override
    public String getPermission()
    {
        return "bettersleeping.status";
    }

    @Override
    public String getDescription()
    {
        return "View the amount of current sleeping players and the total required amount. Will also show some other basic information.";
    }
}
