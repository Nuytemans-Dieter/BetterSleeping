//package be.dezijwegel.bettersleeping.commands;
//
//import be.betterplugins.core.commands.BPCommand;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandSender;
//import org.bukkit.command.ConsoleCommandSender;
//import org.bukkit.entity.Player;
//import org.bukkit.util.StringUtil;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//
//public class TabCompleter implements org.bukkit.command.TabCompleter {
//
//
//    private final Map<String, BPCommand> commands;
//
//
//    public TabCompleter(Map<String, BPCommand> commands)
//    {
//        this.commands = commands;
//    }
//
//
//    /**
//     * This will return a sorted list of type list, containing all allowed commands for a CommandSender
//     *
//     * @param cs which CommandSender needs to be checked
//     * @param partialMatch enforces each command to start with the given String
//     * @return a list of possible commands
//     */
//    private List<String> getAllowedCommands( @NotNull  Map<String, BPCommand> commands, @NotNull CommandSender cs, @Nullable String partialMatch )
//    {
//        List<String> options = new ArrayList<>();
//
//        // Get the allowed commands for this CommandSender
//        for(Map.Entry<String, BPCommand> entry : commands.entrySet())
//        {
//            String cmdName = entry.getKey();
//            BPCommand command = entry.getValue();
//
//            if (cs.hasPermission( command.getPermission() ) && command.mayExecute( cs ))
//                options.add( cmdName );
//        }
//
//
//        List<String> matches = new ArrayList<>();
//        // Only keep the matches
//        if (partialMatch != null)
//        {
//            StringUtil.copyPartialMatches( partialMatch, options, matches );
//        }
//
//
//        // Sort the result
//        Collections.sort(matches);
//
//
//        return matches;
//    }
//
//
//    /**
//     * This will return a sorted list of type list, containing all allowed commands for a CommandSender
//     *
//     * @param cs which CommandSender needs to be checked
//     * @return a list of possible commands
//     */
//    private List<String> getAllowedCommands( @NotNull Map<String, BPCommand> commands, @NotNull CommandSender cs)
//    {
//        return getAllowedCommands(commands, cs, null);
//    }
//
//
//    @Override
//    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] arguments)
//    {
//
//        // Only support console and player
//        if ( !(commandSender instanceof Player) && !(commandSender instanceof ConsoleCommandSender))
//            return null;
//
//        // Return the correct list of possible commands
//        if (arguments.length == 0)
//            return getAllowedCommands(commands, commandSender);
//        else if (arguments.length == 1)
//            return getAllowedCommands(commands, commandSender, arguments[0]);
//        else  return null;
//    }
//
//}
