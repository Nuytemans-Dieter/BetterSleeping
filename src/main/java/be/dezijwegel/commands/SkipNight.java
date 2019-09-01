package be.dezijwegel.commands;

import be.dezijwegel.Runnables.SetTimeToDay;
import be.dezijwegel.events.SleepTracker;
import be.dezijwegel.management.Management;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SkipNight implements be.dezijwegel.interfaces.Command{

    private Management management;
    private SleepTracker sleepTracker;

    public SkipNight(Management management, SleepTracker sleepTracker)
    {
        this.management = management;
        this.sleepTracker = sleepTracker;
    }

    @Override
    public boolean execute(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (strings[0].equalsIgnoreCase("skip"))
        {
            if (cs instanceof ConsoleCommandSender)
            {
                cs.sendMessage("[BetterSleeping] " + ChatColor.RED + "The console cannot perform /bs skip!");
                return true;
            }

            Player player = (Player) cs;

            if ( ! sleepTracker.isPlayerBypassed( player ))
            {
                management.sendMessage("no_permission", player);
                return true;
            }

            World world = player.getWorld();

            if (world.getTime() < 12600) {
                management.sendMessage("not_night_yet", player);
                return true;
            }


            if ( allPlayersInWorldHaveBypassPermissions(world) ) {
                SetTimeToDay toDay = new SetTimeToDay(world, management, sleepTracker);
                toDay.run();
            } else {
                management.sendMessage("not_all_players_bypassed", player);
            }

            return true;
        }

        return false;
    }

    private boolean allPlayersInWorldHaveBypassPermissions(World world)
    {
        for (Player p : Bukkit.getOnlinePlayers())
        {
            if (p.getWorld().equals(world))
            {
                if ( ! sleepTracker.isPlayerBypassed(p))
                    return false;
            }
        }
        return true;
    }
}
