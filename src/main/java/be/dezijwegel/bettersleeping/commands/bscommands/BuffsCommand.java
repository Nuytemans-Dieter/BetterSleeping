package be.dezijwegel.bettersleeping.commands.bscommands;

import be.dezijwegel.bettersleeping.events.handlers.BuffsHandler;
import be.dezijwegel.bettersleeping.messaging.Messenger;
import be.dezijwegel.bettersleeping.permissions.BypassChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class BuffsCommand extends BsCommand {


    private final BuffsHandler buffsHandler;
    private final BypassChecker bypassChecker;


    public BuffsCommand(Messenger messenger, BuffsHandler buffsHandler, BypassChecker bypassChecker)
    {
        super( messenger );

        this.buffsHandler = buffsHandler;
        this.bypassChecker = bypassChecker;
    }


    @Override
    public boolean execute(CommandSender commandSender, Command command, String alias, String[] arguments)
    {
        if ( ! (commandSender instanceof Player ))
            return true;

        Player player = (Player) commandSender;

        messenger.sendMessage(player, ChatColor.GOLD + "--=={BetterSleeping buffs}==--", true);

        if (buffsHandler.getBuffs().size() == 0)
        {
            messenger.sendMessage(player, ChatColor.AQUA + "There are no enabled sleeping buffs!", true );
        }
        else
        {
            messenger.sendMessage(player, "When you sleep, you will get:", true);
            for (PotionEffect buff : buffsHandler.getBuffs())
            {
                messenger.sendMessage(player, ChatColor.AQUA + "  - " + toString(buff), true );
            }
        }

        messenger.sendMessage(player, ChatColor.GOLD + "---===---", true);

        if (buffsHandler.getDebuffs().size() == 0)
        {
            messenger.sendMessage(player, ChatColor.AQUA + "There are no enabled sleeping debuffs!", true );
        }
        else if (bypassChecker.isPlayerBypassed( player ))
        {
            messenger.sendMessage(player, "You will not get debuffs when not sleeping.", true );
        }
        else
        {
            messenger.sendMessage(player, "When you don't sleep, you will get:", true);
            for (PotionEffect buff : buffsHandler.getDebuffs())
            {
                messenger.sendMessage(player, ChatColor.AQUA + "  - " + toString(buff), true );
            }
        }


        messenger.sendMessage(player, ChatColor.GOLD + "---===---", true);
        return true;
    }


    private String toString (PotionEffect buff)
    {
        String string = "";
        string += buff.toString().split(":")[0].toLowerCase().replace("_", " ");
        string += " x" + (buff.getAmplifier()+1);
        int duration = buff.getDuration() / 20;
        string += " (" + duration + " [" + duration + ".second.seconds])";
        return string;
    }


    @Override
    public String getPermission()
    {
        return "bettersleeping.buffs";
    }

    @Override
    public String getDescription()
    {
        return "Shows the buffs and debuffs the executor will get when (not) sleeping.";
    }
}
