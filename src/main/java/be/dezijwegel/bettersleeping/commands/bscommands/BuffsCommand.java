//package be.dezijwegel.bettersleeping.commands.bscommands;
//
//import be.betterplugins.core.commands.shortcuts.PlayerBPCommand;
//import be.betterplugins.core.messaging.messenger.Messenger;
//import be.dezijwegel.bettersleeping.listeners.BuffsHandler;
//import be.dezijwegel.bettersleeping.permissions.BypassChecker;
//import org.bukkit.ChatColor;
//import org.bukkit.command.Command;
//import org.bukkit.entity.Player;
//import org.bukkit.potion.PotionEffect;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.Collections;
//import java.util.List;
//
//public class BuffsCommand extends PlayerBPCommand
//{
//
//
//    final BuffsHandler buffsHandler;
//    final BypassChecker bypassChecker;
//
//
//    public BuffsCommand(Messenger messenger, BuffsHandler buffsHandler, BypassChecker bypassChecker)
//    {
//        super( messenger );
//
//        this.buffsHandler = buffsHandler;
//        this.bypassChecker = bypassChecker;
//    }
//
//    String toString (PotionEffect buff)
//    {
//        String string = "";
//        string += buff.toString().split(":")[0].toLowerCase().replace("_", " ");
//        string += " x" + (buff.getAmplifier()+1);
//        int duration = buff.getDuration() / 20;
//        string += " (" + duration + " [" + duration + ".second.seconds])";
//        return string;
//    }
//
//
//    @Override
//    public @NotNull String getCommandName() {
//        return "buffs";
//    }
//
//    @Override
//    public @NotNull List<String> getAliases() {
//        return Collections.singletonList("b");
//    }
//
//    @Override
//    public @NotNull String getPermission()
//    {
//        return "bettersleeping.buffs";
//    }
//
//    @Override
//    public boolean execute(@NotNull Player player, @NotNull Command command, @NotNull String[] strings)
//    {
//
//        messenger.sendMessage(player, ChatColor.GOLD + "--=={BetterSleeping buffs}==--");
//
//        if (buffsHandler.getBuffs().size() == 0)
//        {
//            messenger.sendMessage(player, ChatColor.AQUA + "There are no enabled sleeping buffs!");
//        }
//        else
//        {
//            messenger.sendMessage(player, "When you sleep, you will get:");
//            for (PotionEffect buff : buffsHandler.getBuffs())
//            {
//                messenger.sendMessage(player, ChatColor.AQUA + "  - " + toString(buff));
//            }
//        }
//
//        messenger.sendMessage(player, ChatColor.GOLD + "---===---");
//
//        if (buffsHandler.getDebuffs().size() == 0)
//        {
//            messenger.sendMessage(player, ChatColor.AQUA + "There are no enabled sleeping debuffs!");
//        }
//        else if (bypassChecker.isPlayerBypassed( player ))
//        {
//            messenger.sendMessage(player, "You will not get debuffs when not sleeping.");
//        }
//        else
//        {
//            messenger.sendMessage(player, "When you don't sleep, you will get:");
//            for (PotionEffect buff : buffsHandler.getDebuffs())
//            {
//                messenger.sendMessage(player, ChatColor.AQUA + "  - " + toString(buff));
//            }
//        }
//
//
//        messenger.sendMessage(player, ChatColor.GOLD + "---===---");
//        return true;
//    }
//}
