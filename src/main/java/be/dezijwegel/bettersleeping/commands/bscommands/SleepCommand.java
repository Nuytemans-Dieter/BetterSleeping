package be.dezijwegel.bettersleeping.commands.bscommands;

import be.betterplugins.core.commands.shortcuts.PlayerBPCommand;
import be.betterplugins.core.messaging.messenger.Messenger;
import be.dezijwegel.bettersleeping.runnables.SleepersRunnable;
import be.dezijwegel.bettersleeping.util.SleepTimeChecker;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SleepCommand extends PlayerBPCommand
{

    private final Map<World, SleepersRunnable> sleepHandlers;

    public SleepCommand(Messenger messenger, Map<World, SleepersRunnable> sleepHandlers)
    {
        super(messenger);
        this.sleepHandlers = sleepHandlers;
    }

    @Override
    public @NotNull String getCommandName()
    {
        return "sleep";
    }

    @Override
    public @NotNull List<String> getAliases()
    {
        return new ArrayList<>();
    }

    @Override
    public @NotNull String getPermission()
    {
        return "bettersleeping.sleepcommand";
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull Command command, @NotNull String[] strings)
    {
        World world = player.getWorld();

        // Make sure this world is enabled
        if (!sleepHandlers.containsKey( world ))
        {
            messenger.sendMessage(player, "&cSleeping is not enabled in this world!");
            return true;
        }

        // Make sure the time is right in the player's world
        if (SleepTimeChecker.isSleepPossible( world ))
        {
            messenger.sendMessage(player, "&cYou feel not tired yet, please wait until night");
            return true;
        }

        sleepHandlers.get( world ).playerCustomEnterBed( player );

        return true;
    }
}
