package be.dezijwegel.Runnables;

import be.dezijwegel.customEvents.PlayersDidNotSleepEvent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlaySoundRunnable extends BukkitRunnable {

    private Player player;
    private Sound sound;

    public PlaySoundRunnable(Player player, Sound sound)
    {
        this.player = player;
        this.sound = sound;
    }

    @Override
    public void run() {
        player.playSound(player.getLocation(), sound, 10, 1);
    }
}