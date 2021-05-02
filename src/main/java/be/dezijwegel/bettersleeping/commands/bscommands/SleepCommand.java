package be.dezijwegel.bettersleeping.commands.bscommands;

import be.dezijwegel.bettersleeping.messaging.Messenger;
import be.dezijwegel.bettersleeping.runnables.SleepersRunnable;
import be.dezijwegel.bettersleeping.util.SleepTimeChecker;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SleepCommand extends BsCommand {

    private final Map<World, SleepersRunnable> sleepHandlers;

    public SleepCommand(Messenger messenger, Map<World, SleepersRunnable> sleepHandlers) {
        super(messenger);
        this.sleepHandlers = sleepHandlers;
    }

    @Override
    public boolean execute(CommandSender commandSender, Command command, String alias, String[] arguments) {

        if ( ! (commandSender instanceof Player))
        {
            messenger.sendMessage(commandSender, "&cOnly players can execute /bs sleep!", true);
            return true;
        }

        Player player = (Player) commandSender;
        World world = player.getWorld();

        // Make sure this world is enabled
        if (!sleepHandlers.containsKey( world ))
        {
            messenger.sendMessage(player, "&cSleeping is not enabled in this world!", true);
            return true;
        }

        // Make sure the time is right in the player's world
        if (SleepTimeChecker.isSleepPossible( world ))
        {
            messenger.sendMessage(player, "&cYou feel not tired yet, please wait until night", true);
            return true;
        }

        sleepHandlers.get( world ).playerCustomEnterBed( player );

        return true;
    }

    @Override
    public String getPermission() {
        return "bettersleeping.sleepcommand";
    }

    @Override
    public List<String> getDescription() {
        return new ArrayList<String>() {{
            add("Makes the plugin think");
            add("you are sleeping");
        }};
    }

    @Override
    public String getDescriptionAsString() {
        return "Makes the plugin believe that you are sleeping";
    }
}
