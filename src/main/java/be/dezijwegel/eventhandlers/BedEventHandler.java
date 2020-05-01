package be.dezijwegel.eventhandlers;

import be.dezijwegel.permissions.SleepDelayChecker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class BedEventHandler implements Listener {


    private final SleepDelayChecker sleepDelayChecker;


    public BedEventHandler()
    {
        sleepDelayChecker = new SleepDelayChecker(2);
    }


    @EventHandler
    public void onBedEnter (PlayerBedEnterEvent event)
    {
        // Check event status
        if (event.isCancelled() || event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK)
            return;

        Player player = event.getPlayer();

        // TODO Check bypass permissions, afk status and bypassed gamemode

        // Check sleep delay
        if (sleepDelayChecker.whenCanPlayerSleep(player.getUniqueId()) > 0)
        {

        }
    }



}
