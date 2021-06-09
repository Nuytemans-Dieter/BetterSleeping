//package be.dezijwegel.bettersleeping.commands.bscommands;
//
//import be.betterplugins.core.commands.shortcuts.PlayerBPCommand;
//import be.betterplugins.core.messaging.messenger.Messenger;
//import be.betterplugins.core.messaging.messenger.MsgEntry;
//import be.dezijwegel.bettersleeping.listeners.BedEventHandler;
//import be.dezijwegel.bettersleeping.timechange.TimeChanger;
//import be.dezijwegel.bettersleeping.util.SleepStatus;
//import org.bukkit.ChatColor;
//import org.bukkit.command.Command;
//import org.bukkit.entity.Player;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.Collections;
//import java.util.List;
//
//public class StatusCommand extends PlayerBPCommand {
//
//
//    final BedEventHandler bedEventHandler;
//
//
//    public StatusCommand(Messenger messenger, BedEventHandler bedEventHandler)
//    {
//        super( messenger );
//
//        this.bedEventHandler = bedEventHandler;
//    }
//
//
//    @Override
//    public @NotNull String getCommandName()
//    {
//        return "status";
//    }
//
//    @Override
//    public @NotNull List<String> getAliases()
//    {
//        return Collections.singletonList("s");
//    }
//
//    @Override
//    public @NotNull String getPermission()
//    {
//        return "bettersleeping.status";
//    }
//
//    @Override
//    public boolean execute(@NotNull Player player, @NotNull Command command, @NotNull String[] strings)
//    {
//        SleepStatus status = bedEventHandler.getSleepStatus( player.getWorld() );
//
//        if (status == null)
//        {
//            messenger.sendMessage(player, "&cWe're very sorry but checking the sleeping status is not available in your world.");
//            return true;
//        }
//
//        messenger.sendMessage(player, ChatColor.GOLD + "--=={BetterSleeping status}==--");
//        messenger.sendMessage(player, ChatColor.DARK_AQUA + "Showing satus of world: " + ChatColor.AQUA + status.getWorld().getName());
//
//        int left = status.getNumLeft();
//        messenger.sendMessage(player, ChatColor.DARK_AQUA + "Sleeping: " + ChatColor.AQUA +  "<left> more [<left>.player.players] needed",
//                new MsgEntry("<left>", String.valueOf(left)));
//
//        messenger.sendMessage(player, ChatColor.DARK_AQUA + "Sleeping: " + ChatColor.AQUA + status.getNumSleeping() + "/" + status.getTotalNeeded());
//        messenger.sendMessage(player, ChatColor.DARK_AQUA + "Player counter info: " + ChatColor.AQUA + status.getSettingMessage());
//
//        String setType = (status.getType() == TimeChanger.TimeChangeType.SMOOTH) ? "Time will pass faster when enough people sleep" : "Time will be set to day after a delay";
//        messenger.sendMessage(player, ChatColor.DARK_AQUA + "Time set type: " + ChatColor.AQUA + setType);
//
//        messenger.sendMessage(player, ChatColor.GOLD + "---==<>==---");
//
//        return true;
//    }
//}
