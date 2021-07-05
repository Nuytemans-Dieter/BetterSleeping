package be.betterplugins.bettersleeping.commands;

import be.betterplugins.bettersleeping.model.sleeping.SleepWorldManager;
import be.betterplugins.bettersleeping.util.TimeUtil;
import be.betterplugins.core.commands.shortcuts.PlayerBPCommand;
import be.betterplugins.core.messaging.messenger.Messenger;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class SleepCommand extends PlayerBPCommand
{

    private final SleepWorldManager sleepWorldManager;

    public SleepCommand(Messenger messenger, SleepWorldManager sleepWorldManager)
    {
        super(messenger);
        this.sleepWorldManager = sleepWorldManager;
    }

    @Override
    public @NotNull String getCommandName()
    {
        return "sleep";
    }

    @Override
    public @NotNull List<String> getAliases()
    {
        return Collections.singletonList("s");
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
        if (!sleepWorldManager.isWorldEnabled( world ))
        {
            messenger.sendMessage(player, "&cSleeping is not enabled in this world!");
            return true;
        }

        // Make sure the time is right in the player's world
        if (!TimeUtil.isSleepPossible( world ))
        {
            messenger.sendMessage(player, "&cYou feel not tired yet, please wait until night");
            return true;
        }

        sleepWorldManager.addSleeper( player );
        return true;
    }
}
