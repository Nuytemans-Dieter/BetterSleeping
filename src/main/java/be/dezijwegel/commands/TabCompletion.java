package be.dezijwegel.commands;

import be.dezijwegel.events.SleepTracker;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class TabCompletion implements TabCompleter {

    private SleepTracker sleepTracker;

    public TabCompletion ( SleepTracker sleepTracker )
    {
        this.sleepTracker = sleepTracker;
    }

    @Override
    public List<String> onTabComplete (CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("bettersleeping"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                List options = new LinkedList<String>();

                if (args.length > 0)
                    return options;

                if ( player.hasPermission("bettersleeping.help.user") || player.hasPermission("bettersleeping.help.admin") || player.hasPermission("bettersleeping.help"))
                {
                    options.add("help");
                }

                if ( sender.hasPermission("bettersleeping.reload") )
                {
                    options.add("reload");
                }

                if ( sleepTracker.isPlayerBypassed( player ) )
                {
                    options.add("skip");
                }

                if ( sender.hasPermission("bettersleeping.status") )
                {
                    options.add("status");
                }

                if (options.isEmpty())
                    return null;

                return options;

            } else return null;
        } else return null;
    }

}
