package be.dezijwegel.commands;

import be.dezijwegel.events.SleepTracker;
import be.dezijwegel.interfaces.Command;
import be.dezijwegel.management.Management;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Status implements Command {

    private SleepTracker sleepTracker;
    private Management management;

    private final int numPlayersListed = 5;

    public Status(SleepTracker sleepTracker, Management management)
    {
        this.sleepTracker = sleepTracker;
        this.management = management;
    }


    @Override
    public boolean execute(CommandSender cs, org.bukkit.command.Command cmnd, String string, String[] strings) {
        if (cs instanceof Player)
        {
            Player player = (Player) cs;
            World world = player.getWorld();
            int numNeeded = sleepTracker.getTotalSleepersNeeded( world );
            int numSleeping = sleepTracker.getNumSleepingPlayers( world );


            player.sendMessage(ChatColor.GOLD + "---= BetterSleeping status =---");

            if ( management.getBooleanSetting("multiworld_support") )
            {
                player.sendMessage(ChatColor.DARK_AQUA + "World: " + ChatColor.WHITE + world.getName() );
            }

            player.sendMessage(ChatColor.DARK_AQUA + "Sleeping players: " + ChatColor.WHITE + numSleeping + "/" +numNeeded);

            if (sleepTracker.getNumSleepingPlayers(world) != 0) {

                String sleepingPlayerNames = "";
                List<Player> sleepers = getSleepingPlayers(player);
                for (int i = 0; i < numPlayersListed; i++) {

                    Player p = sleepers.get(i);

                    if (i == numPlayersListed-1 || i == sleepers.size()-1) {
                        sleepingPlayerNames += p.getName();
                        i = numPlayersListed;
                    } else {
                        sleepingPlayerNames += p.getName() + ", ";
                    }
                }

                if (sleepers.size() > 1) {
                    player.sendMessage(ChatColor.DARK_AQUA + "Some sleeping players: " + ChatColor.WHITE + sleepingPlayerNames);
                } else {
                    player.sendMessage(ChatColor.DARK_AQUA + "Only one sleeping player: " + ChatColor.WHITE + sleepingPlayerNames);
                }
            }

            player.sendMessage(ChatColor.GOLD + "---==---");

        } else {
            String message = "Only players can perform /bs status!";

            if (management.getConsoleConfig().isNegativeRed())
                cs.sendMessage("[BetterSleeping] " + ChatColor.RED + message);
            else
                cs.sendMessage("[BetterSleeping] " + message);
            
            return true;
        }

        return true;
    }

    /**
     * Get the first five relevant sleeping players for a given player
     * @param player
     * @return
     */
    private List<Player> getSleepingPlayers( Player player )
    {
        List<Player> players = new ArrayList<Player>();

        World world = player.getWorld();
        for (Player p : Bukkit.getOnlinePlayers())
        {
            if (p.getWorld().equals( world ))
            {
                if ( p.isSleeping() )
                    players.add( p );

                if (players.size() >= numPlayersListed)
                    return players;
            }
        }

        return players;
    }
}
