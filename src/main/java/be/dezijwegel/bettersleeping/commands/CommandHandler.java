package be.dezijwegel.bettersleeping.commands;

import be.dezijwegel.bettersleeping.BetterSleeping;
import be.dezijwegel.bettersleeping.interfaces.BsCommand;
import be.dezijwegel.bettersleeping.messenger.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler implements CommandExecutor {


    private final Messenger messenger;

    private final Map<String, BsCommand> playerCommands;
    private final Map<String, BsCommand> consoleCommands;


    public CommandHandler(BetterSleeping plugin, Messenger messenger)
    {
        this.messenger = messenger;

        playerCommands = new HashMap<>();
        consoleCommands = new HashMap<>();

        BsCommand version = new VersionCommand(plugin, messenger);
        BsCommand help = new HelpCommand(messenger, playerCommands);
        BsCommand reload = new ReloadCommand(plugin, messenger);

        playerCommands.put("version",   version);
        playerCommands.put("help",      help);
        playerCommands.put("reload",    reload);

        consoleCommands.put("version",  version);
        consoleCommands.put("help",     help);
        consoleCommands.put("reload",   reload);

        plugin.getCommand("bettersleeping").setTabCompleter(new TabCompleter(playerCommands));
    }


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] arguments) {

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


        String cmd;
        if (arguments.length == 0)
            cmd = "help";
        else
            cmd = arguments[0];

        if (commandMap.containsKey( cmd ))
        {
            return commandMap.get(cmd).execute(commandSender, command, alias, arguments);
        }
        else
        {
            messenger.sendMessage(commandSender, "&cThe command '/bs " + cmd + "' is not recognised! Execute /bs help to see a list of commands");
            return true;
        }
    }
}
