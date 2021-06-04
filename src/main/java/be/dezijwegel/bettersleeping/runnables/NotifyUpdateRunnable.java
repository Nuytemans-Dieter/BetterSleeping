package be.dezijwegel.bettersleeping.runnables;

import be.dezijwegel.bettersleeping.util.Version;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class NotifyUpdateRunnable extends BukkitRunnable {

    private final Messenger messenger;
    private final Version currentVersion;
    private final Version latestVersion;


    public NotifyUpdateRunnable(Messenger messenger, String currentVersion, String latestVersion)
    {
        this.messenger = messenger;
        this.currentVersion = new Version(currentVersion);
        this.latestVersion = new Version(latestVersion);
    }

    @Override
    public void run()
    {
        // No version check if something went wrong
        if (!currentVersion.isCorrectFormat() || !latestVersion.isCorrectFormat())
            return;

        // Find all admins
        List<Player> admins = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (player.isOp() ||
                player.hasPermission("bettersleeping.admin") ||
                player.hasPermission("bettersleeping.*") )
            {
                admins.add( player );
            }
        }


        // Compose and send message
        String message = "You are using BetterSleeping v" + currentVersion.toString() + " but the latest version is v" + latestVersion.toString() +".";
        messenger.sendMessage(admins, message, true);
    }
}
