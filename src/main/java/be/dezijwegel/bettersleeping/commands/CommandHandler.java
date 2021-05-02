package be.dezijwegel.bettersleeping.commands;

import be.dezijwegel.bettersleeping.BetterSleeping;
import be.dezijwegel.bettersleeping.commands.bscommands.*;
import be.dezijwegel.bettersleeping.listeners.BuffsHandler;
import be.dezijwegel.bettersleeping.messaging.Messenger;
import be.dezijwegel.bettersleeping.messaging.MsgEntry;
import be.dezijwegel.bettersleeping.permissions.BypassChecker;
import be.dezijwegel.bettersleeping.runnables.SleepersRunnable;
import org.bukkit.World;
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

    private final Map<String, String> shortcuts;

    public CommandHandler(BetterSleeping plugin, Messenger messenger, BuffsHandler buffsHandler, BypassChecker bypassChecker, Map<World, SleepersRunnable> sleepHandlers)
    {
        this.messenger = messenger;

        playerCommands  = new HashMap<>();
        consoleCommands = new HashMap<>();

        BsCommand version   = new VersionCommand(plugin, messenger);
        BsCommand help      = new HelpCommand(messenger, playerCommands);
        BsCommand reload    = new ReloadCommand(plugin, messenger);
        BsCommand status    = new StatusCommand(messenger, plugin.getBedEventHandler());
        BsCommand buffs     = new BuffsCommand(messenger, buffsHandler, bypassChecker);
        BsCommand sleep     = new SleepCommand(messenger, sleepHandlers);
        BsCommand shout     = new ShoutCommand(messenger);

        playerCommands.put("version",   version );
        playerCommands.put("help"   ,   help    );
        playerCommands.put("reload" ,   reload  );
        playerCommands.put("status" ,   status  );
        playerCommands.put("buffs"  ,   buffs   );
        playerCommands.put("sleep"  ,   sleep   );
        playerCommands.put("shout"  ,   shout   );

        consoleCommands.put("version",  version );
        consoleCommands.put("help",     help    );
        consoleCommands.put("reload",   reload  );

        shortcuts = new HashMap<>();
        shortcuts.put("v", "version");
        shortcuts.put("h", "help");
        shortcuts.put("r", "reload");
        shortcuts.put("s", "status");
        shortcuts.put("b", "buffs");

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
            messenger.sendMessage(commandSender, "&cOnly players and the console can execute BetterSleeping commands!", true);
            return true;
        }

        // Default to /bs help if no argument given
        String cmd = (arguments.length == 0) ? "help" : arguments[0];

        // Check for shortcut
        if (shortcuts.containsKey( cmd ))
            cmd = shortcuts.get( cmd );

        if (commandMap.containsKey( cmd ))
        {
            BsCommand bsCommand = commandMap.get(cmd);
            if (commandSender.hasPermission( bsCommand.getPermission() ) || commandSender instanceof ConsoleCommandSender)
                return bsCommand.execute(commandSender, command, alias, arguments);
            else
                messenger.sendMessage(commandSender, "no_permission", true, new MsgEntry("<var>", "/bs " + cmd));
        }
        else
        {
            messenger.sendMessage(commandSender, "&cThe command '/bs " + cmd + "' is not recognised! Execute /bs help to see a list of commands", true);
        }

        return true;
    }
}
