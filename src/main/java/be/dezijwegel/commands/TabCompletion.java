package be.dezijwegel.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class TabCompletion implements TabCompleter {

    @Override
    public List<String> onTabComplete (CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("bettersleeping"))
        {
            if (sender instanceof Player)
            {
                if (sender.isOp() || sender.hasPermission("bettersleeping.reload"))
                {
                    List options = new LinkedList<String>();
                    options.add("reload");
                    return options;
                } else return null;
            } else return null;
        } else return null;
    }

}
