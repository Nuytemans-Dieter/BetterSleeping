package be.betterplugins.bettersleeping.commands;

import be.betterplugins.bettersleeping.model.SleepStatus;
import be.betterplugins.bettersleeping.model.sleeping.SleepWorldManager;
import be.betterplugins.bettersleeping.util.Theme;
import be.betterplugins.core.commands.shortcuts.PlayerBPCommand;
import be.betterplugins.core.messaging.messenger.Messenger;
import be.betterplugins.core.messaging.messenger.MsgEntry;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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

    protected DecimalFormat createDecimalFormat()
    {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');

        return new DecimalFormat("##.##", symbols);
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull Command command, @NotNull String[] strings)
    {
        SleepStatus status = this.sleepWorldManager.getSleepStatus( player.getWorld() );

        if ( status == null )
        {
            messenger.sendMessage(player, "world_disabled");
            return true;
        }

        DecimalFormat df = createDecimalFormat();

        String daySpeedup = df.format( status.getDaySpeedup() );
        String nightSpeedup = df.format( status.getNightSpeedup() );
        String sleepSpeedup = df.format( status.getSleepSpeedup() );

        messenger.sendMessage(player, "command_status_header");
        messenger.sendMessage(player, "command_status_world",
                new MsgEntry("<worldname>", player.getWorld().getName()));
        messenger.sendMessage(player, "command_status_sleeping",
                new MsgEntry("<num_sleeping>", status.getNumSleepers()),
                new MsgEntry("<needed_sleeping>", status.getNumNeeded()),
                new MsgEntry("<remaining_sleeping>", status.getNumMissing()));
        messenger.sendMessage(player, "command_status_dayspeed",
                new MsgEntry("<var>", daySpeedup));
        messenger.sendMessage(player, "command_status_nightspeed",
                new MsgEntry("<var>", nightSpeedup));
        messenger.sendMessage(player, "command_status_sleepingspeed",
                new MsgEntry("<var>", sleepSpeedup));
        messenger.sendMessage(player, "command_status_footer");

        return true;
    }
}
