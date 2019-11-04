package be.dezijwegel.commands;

import be.dezijwegel.events.DisableSkipTracker;
import be.dezijwegel.events.SleepTracker;
import be.dezijwegel.files.Lang;
import be.dezijwegel.management.Management;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Night implements be.dezijwegel.interfaces.Command {

    private Management management;
    private SleepTracker sleepTracker;

    public Night(Management management, SleepTracker sleepTracker)
    {
        this.management = management;
        this.sleepTracker = sleepTracker;
    }

    @Override
    public boolean execute(CommandSender cs, Command cmnd, String string, String[] strings) {
        if ( ! (cs instanceof Player)) {
            cs.sendMessage("[BetterSleeping] " + ChatColor.DARK_RED + "Only players can execute this command!");
            return true;
        }

        Player player = (Player) cs;

        String command = strings[0];
        if (command.equalsIgnoreCase("night") || command.equalsIgnoreCase("n") )
        {
            DisableSkipTracker disableSkipTracker = sleepTracker.getDisableSkipTracker();
            World world = player.getWorld();
            if ( ! disableSkipTracker.isDisabled(world) )
            {
                // If the player has no permission
                if (!player.isOp() && !player.hasPermission("bettersleeping.reload"))
                {
                    management.sendMessage("no_permission", player);
                    return true;
                }


                // If this command is disabled
                if ( ! management.getBooleanSetting("enable_no_skip_night_command"))
                {
                    management.sendMessage("command_disabled", player);
                    return true;
                }

                disableSkipTracker.disableSkip(world, player);
                Map<String, String> replacings = new HashMap<>();
                replacings.put("<user>", Lang.stripColor( ChatColor.stripColor( player.getName() ) ));
                replacings.put("<time>", Integer.toString(disableSkipTracker.getDuration()));
                management.sendMessageToGroup("disable_skip", sleepTracker.getRelevantPlayers(world), replacings, disableSkipTracker.getDuration() == 1);
            } else {
                management.sendMessage("already_disabled", player);
            }
        }

        return true;
    }

}
