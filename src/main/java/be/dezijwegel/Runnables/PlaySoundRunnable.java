package be.dezijwegel.Runnables;

import be.dezijwegel.customEvents.PlayersDidNotSleepEvent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlaySoundRunnable extends BukkitRunnable {

    private Player player;
    private Sound sound;
    float volume;
    float pitch;

    public PlaySoundRunnable(Player player, Sound sound)
    {
        this.player = player;
        this.sound = sound;
        volume = 1;
        pitch = 1;
    }

    public PlaySoundRunnable(Player player, Sound sound, float volume, float pitch)
    {
        this.player = player;
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void run() {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }
}