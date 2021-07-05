package be.betterplugins.bettersleeping.listeners;

import be.betterplugins.bettersleeping.animation.Animation;
import be.betterplugins.bettersleeping.animation.SleepingAnimation;
import be.betterplugins.bettersleeping.animation.ZAnimation;
import be.betterplugins.bettersleeping.animation.location.PlayerSleepLocation;
import be.betterplugins.bettersleeping.api.BecomeDayEvent;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class AnimationHandler implements Listener {

    private final Map<UUID, SleepingAnimation> sleepingAnimations;
    private final Animation sleepingAnimation;
    private final JavaPlugin plugin;

    @Inject
    public AnimationHandler(JavaPlugin plugin)
    {
        this.plugin = plugin;

        this.sleepingAnimations = new HashMap<>();
        this.sleepingAnimation = new ZAnimation(Particle.COMPOSTER, 0.5, 0.1, plugin);
    }

    public void startSleepingAnimation(Player player)
    {
        // Start animations
        SleepingAnimation animation = new SleepingAnimation(this.sleepingAnimation, 200, plugin);
        animation.startAnimation( new PlayerSleepLocation( player ));
        this.sleepingAnimations.put(player.getUniqueId(), animation);
    }

    @EventHandler
    public void timeSetToDayEvent(BecomeDayEvent event)
    {
        // Stop all animations
        this.sleepingAnimations.forEach( (uuid, animation) -> animation.stopAnimation());
        this.sleepingAnimations.clear();
    }

    @EventHandler
    public void onBedLeave(PlayerBedLeaveEvent event)
    {
        UUID uuid =  event.getPlayer().getUniqueId();
        stopAnimation( uuid );
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event)
    {
        UUID uuid = event.getPlayer().getUniqueId();
        stopAnimation(uuid);
    }

    private void stopAnimation(UUID uuid)
    {
        if (this.sleepingAnimations.containsKey( uuid ))
        {
            this.sleepingAnimations.remove(uuid).stopAnimation();
        }
    }

}
