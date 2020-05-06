package be.dezijwegel.bettersleeping.commands;

import be.dezijwegel.bettersleeping.interfaces.BsCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TabCompleter implements org.bukkit.command.TabCompleter {


    private final Map<String, BsCommand> subCommands;  // Every subcommand mapped to its BsCommand object


    public TabCompleter(Map<String, BsCommand> commands)
    {
        subCommands = commands;
    }


    /**
     * This will return a sorted list of type list, containing all allowed commands for a CommandSender
     *
     * @param cs which CommandSender needs to be checked
     * @param partialMatch enforces each command to start with the given String
     * @return a list of possible commands
     */
    private List<String> getAllowedCommands( @NotNull CommandSender cs, @Nullable String partialMatch )
    {
        List<String> options = new ArrayList<>();

        // Get the allowed commands for this CommandSender
        for(Map.Entry<String, BsCommand> entry : subCommands.entrySet())
        {
            String cmdName = entry.getKey();
            BsCommand command = entry.getValue();

            if (cs.hasPermission( command.getPermission() ))
                options.add( cmdName );
        }


        // Only keep the matches
        if (partialMatch != null)
        {
            StringUtil.copyPartialMatches( partialMatch, options, options );
        }


        // Sort the result
        Collections.sort(options);


        return options;
    }


    /**
     * This will return a sorted list of type list, containing all allowed commands for a CommandSender
     *
     * @param cs which CommandSender needs to be checked
     * @return a list of possible commands
     */
    private List<String> getAllowedCommands( @NotNull CommandSender cs)
    {
        return getAllowedCommands(cs, null);
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] arguments)
    {

        // Only support console and player
        if ( !(commandSender instanceof Player) || !(commandSender instanceof ConsoleCommandSender))
            return null;

        // Return the correct list of possible commands
        if (arguments.length == 0)
            return getAllowedCommands(commandSender);
        else if (arguments.length == 1)
            return getAllowedCommands(commandSender, arguments[0]);
        else
            return null;
    }

}
