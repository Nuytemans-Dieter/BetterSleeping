package be.dezijwegel.bettersleeping.events.listeners;

import be.dezijwegel.bettersleeping.animation.Animation;
import be.dezijwegel.bettersleeping.animation.SleepingAnimation;
import be.dezijwegel.bettersleeping.animation.ZAnimation;
import be.dezijwegel.bettersleeping.animation.location.PlayerLocation;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AnimationHandler implements Listener {

    private final Map<UUID, SleepingAnimation> sleepingAnimations;
    private final Animation sleepingAnimation;

    public AnimationHandler()
    {
        this.sleepingAnimations = new HashMap<>();
        this.sleepingAnimation = new ZAnimation(Particle.COMPOSTER, 0.5, 0.1);
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event)
    {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK)
            return;

        // Start animations
        Player player = event.getPlayer();
        SleepingAnimation animation = new SleepingAnimation(this.sleepingAnimation, 200);
        animation.startAnimation( new PlayerLocation( player ));
        this.sleepingAnimations.put(player.getUniqueId(), animation);
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
