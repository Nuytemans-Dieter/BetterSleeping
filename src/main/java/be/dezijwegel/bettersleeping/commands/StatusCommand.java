package be.dezijwegel.bettersleeping.commands;

import be.dezijwegel.bettersleeping.events.handlers.BedEventHandler;
import be.dezijwegel.bettersleeping.interfaces.BsCommand;
import be.dezijwegel.bettersleeping.messaging.Messenger;
import be.dezijwegel.bettersleeping.messaging.MsgEntry;
import be.dezijwegel.bettersleeping.timechange.TimeChanger;
import be.dezijwegel.bettersleeping.util.SleepStatus;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatusCommand implements BsCommand {


    private final Messenger messenger;
    private final BedEventHandler bedEventHandler;


    public StatusCommand(Messenger messenger, BedEventHandler bedEventHandler)
    {
        this.messenger = messenger;
        this.bedEventHandler = bedEventHandler;
    }


    @Override
    public boolean execute(CommandSender commandSender, Command command, String alias, String[] arguments)
    {
        if ( ! (commandSender instanceof Player))
        {
            messenger.sendMessage(commandSender, "&cOnly players can execute /bs status!");
            return true;
        }

        Player player = (Player) commandSender;
        SleepStatus status = bedEventHandler.getSleepStatus( player.getWorld() );

        if (status == null)
        {
            messenger.sendMessage(player, "&cWe're very sorry but checking the sleeping status is not available in your world.");
            return true;
        }

        messenger.sendMessage(player, ChatColor.GOLD + "---==BetterSleeping Status==---");
        messenger.sendMessage(player, ChatColor.GOLD + "Showing satus of world: " + ChatColor.DARK_AQUA + status.getWorld().getName());

        int left = status.getNumLeft();
        messenger.sendMessage(player, ChatColor.GOLD + "Sleeping: " + ChatColor.DARK_AQUA +  "<left> more [<left>.player.players] needed",
                              new MsgEntry("<left>", String.valueOf(left)));

        messenger.sendMessage(player, ChatColor.GOLD + "Sleeping: " + ChatColor.DARK_AQUA + status.getNumSleeping() + "/" + status.getTotalNeeded());
        messenger.sendMessage(player, ChatColor.GOLD + "Player counter info: " + ChatColor.DARK_AQUA + status.getSettingMessage());

        String setType = (status.getType() == TimeChanger.TimeChangeType.SMOOTH) ? "Time will pass faster when enough people sleep" : "Time will be set to day after a delay";
        messenger.sendMessage(player, ChatColor.GOLD + "Time set type: " + ChatColor.DARK_AQUA + setType);

        messenger.sendMessage(player, ChatColor.GOLD + "---==<>==---");

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
