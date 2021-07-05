package be.betterplugins.bettersleeping.commands;

import be.betterplugins.bettersleeping.model.SleepStatus;
import be.betterplugins.bettersleeping.model.sleeping.SleepWorldManager;
import be.betterplugins.bettersleeping.util.Theme;
import be.betterplugins.core.commands.shortcuts.PlayerBPCommand;
import be.betterplugins.core.messaging.messenger.Messenger;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class StatusCommand extends PlayerBPCommand {


    final SleepWorldManager sleepWorldManager;


    public StatusCommand(Messenger messenger, SleepWorldManager sleepWorldManager)
    {
        super( messenger );

        this.sleepWorldManager = sleepWorldManager;
    }


    @Override
    public @NotNull String getCommandName()
    {
        return "status";
    }

    @Override
    public @NotNull List<String> getAliases()
    {
        return Collections.singletonList("s");
    }

    @Override
    public @NotNull String getPermission()
    {
        return "bettersleeping.status";
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull Command command, @NotNull String[] strings)
    {
        SleepStatus status = this.sleepWorldManager.getSleepStatus( player.getWorld() );

        if ( status == null )
        {
            messenger.sendMessage(player, "&cWe're very sorry but BetterSleeping is not available in your world.");
            return true;
        }

        messenger.sendMessage(player, Theme.primaryColor + "--=={BetterSleeping4 status}==--");
        messenger.sendMessage(player, Theme.secondaryColor + "Showing status of world: " + Theme.tertiaryColor + player.getWorld().getName());
        messenger.sendMessage(player, Theme.secondaryColor + "Sleeping: " + Theme.tertiaryColor + status.getNumSleepers() + "/" + status.getNumNeeded());
        messenger.sendMessage(player, Theme.secondaryColor + "Day speed: " + Theme.tertiaryColor + "x" + status.getDaySpeedup());
        messenger.sendMessage(player, Theme.secondaryColor + "Night speed: " + Theme.tertiaryColor + "x" + status.getNightSpeedup());
        messenger.sendMessage(player, Theme.secondaryColor + "Sleeping speed: " + Theme.tertiaryColor + "x" + status.getSleepSpeedup());
        messenger.sendMessage(player, Theme.primaryColor + "---==<>==---");

        return true;
    }
}
