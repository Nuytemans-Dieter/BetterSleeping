package be.dezijwegel.bettersleeping.commands.bscommands;

import be.dezijwegel.bettersleeping.events.handlers.BedEventHandler;
import be.dezijwegel.bettersleeping.messaging.Messenger;
import be.dezijwegel.bettersleeping.messaging.MsgEntry;
import be.dezijwegel.bettersleeping.timechange.TimeChanger;
import be.dezijwegel.bettersleeping.util.SleepStatus;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatusCommand extends BsCommand {


    private final BedEventHandler bedEventHandler;


    public StatusCommand(Messenger messenger, BedEventHandler bedEventHandler)
    {
        super( messenger );

        this.bedEventHandler = bedEventHandler;
    }


    @Override
    public boolean execute(CommandSender commandSender, Command command, String alias, String[] arguments)
    {
        if ( ! (commandSender instanceof Player))
        {
            messenger.sendMessage(commandSender, "&cOnly players can execute /bs status!", true);
            return true;
        }

        Player player = (Player) commandSender;
        SleepStatus status = bedEventHandler.getSleepStatus( player.getWorld() );

        if (status == null)
        {
            messenger.sendMessage(player, "&cWe're very sorry but checking the sleeping status is not available in your world.", true);
            return true;
        }

        messenger.sendMessage(player, ChatColor.GOLD + "--=={BetterSleeping status}==--", true);
        messenger.sendMessage(player, ChatColor.DARK_AQUA + "Showing satus of world: " + ChatColor.AQUA + status.getWorld().getName(), true);

        int left = status.getNumLeft();
        messenger.sendMessage(player, ChatColor.DARK_AQUA + "Sleeping: " + ChatColor.AQUA +  "<left> more [<left>.player.players] needed", true,
                              new MsgEntry("<left>", String.valueOf(left)));

        messenger.sendMessage(player, ChatColor.DARK_AQUA + "Sleeping: " + ChatColor.AQUA + status.getNumSleeping() + "/" + status.getTotalNeeded(), true);
        messenger.sendMessage(player, ChatColor.DARK_AQUA + "Player counter info: " + ChatColor.AQUA + status.getSettingMessage(), true);

        String setType = (status.getType() == TimeChanger.TimeChangeType.SMOOTH) ? "Time will pass faster when enough people sleep" : "Time will be set to day after a delay";
        messenger.sendMessage(player, ChatColor.DARK_AQUA + "Time set type: " + ChatColor.AQUA + setType, true);

        messenger.sendMessage(player, ChatColor.GOLD + "---==<>==---", true);

        return true;
    }

    @Override
    public String getPermission()
    {
        return "bettersleeping.status";
    }

    @Override
    public String getDescription()
    {
        return "View the amount of current sleeping players and the total required amount. Will also show some other basic information.";
    }
}
