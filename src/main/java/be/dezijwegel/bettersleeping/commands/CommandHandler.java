package be.dezijwegel.bettersleeping.commands;

import be.dezijwegel.bettersleeping.BetterSleeping;
import be.dezijwegel.bettersleeping.events.handlers.BuffsHandler;
import be.dezijwegel.bettersleeping.interfaces.BsCommand;
import be.dezijwegel.bettersleeping.messaging.Messenger;
import be.dezijwegel.bettersleeping.permissions.BypassChecker;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler implements CommandExecutor {


    private final Messenger messenger;

    private final Map<String, BsCommand> playerCommands;
    private final Map<String, BsCommand> consoleCommands;


    public CommandHandler(BetterSleeping plugin, Messenger messenger, BuffsHandler buffsHandler, BypassChecker bypassChecker)
    {
        this.messenger = messenger;

        playerCommands  = new HashMap<>();
        consoleCommands = new HashMap<>();

        BsCommand version   = new VersionCommand(plugin, messenger);
        BsCommand help      = new HelpCommand(messenger, playerCommands);
        BsCommand reload    = new ReloadCommand(plugin, messenger);
        BsCommand status    = new StatusCommand(messenger, plugin.getBedEventHandler());
        BsCommand buffs     = new BuffsCommand(messenger, buffsHandler, bypassChecker);

        playerCommands.put("version",   version );
        playerCommands.put("help"   ,   help    );
        playerCommands.put("reload" ,   reload  );
        playerCommands.put("status" ,   status  );
        playerCommands.put("buffs"  ,   buffs   );

        consoleCommands.put("version",  version );
        consoleCommands.put("help",     help    );
        consoleCommands.put("reload",   reload  );

        plugin.getCommand("bettersleeping").setTabCompleter(new TabCompleter(playerCommands, consoleCommands));
    }


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] arguments) {

        // Get user commands or console commands
        Map<String, BsCommand> commandMap;
        if (commandSender instanceof Player)
            commandMap = playerCommands;
        else if (commandSender instanceof ConsoleCommandSender)
            commandMap = consoleCommands;
        else
        {
            // No support for command blocks
            messenger.sendMessage(commandSender, "&cOnly players and the console can execute BetterSleeping commands!");
            return true;
        }


        String cmd = (arguments.length == 0) ? "help" : arguments[0];


        if (commandMap.containsKey( cmd ))
        {
            BsCommand bsCommand = commandMap.get(cmd);
            if (commandSender.hasPermission( bsCommand.getPermission() ))
                return bsCommand.execute(commandSender, command, alias, arguments);
            else
                messenger.sendMessage(commandSender, "no_permission");
        }
        else
        {
            messenger.sendMessage(commandSender, "&cThe command '/bs " + cmd + "' is not recognised! Execute /bs help to see a list of commands");
        }

        return true;
    }
}
