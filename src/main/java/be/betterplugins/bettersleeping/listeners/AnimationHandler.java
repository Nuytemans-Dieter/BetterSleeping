package be.betterplugins.bettersleeping.listeners;

import be.betterplugins.bettersleeping.animation.ZZZAnimation;
import be.betterplugins.bettersleeping.animation.location.PlayerSleepLocation;
import be.betterplugins.bettersleeping.api.BecomeDayEvent;
import be.betterplugins.core.interfaces.IReloadable;
import be.betterplugins.core.messaging.logging.BPLogger;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

@Singleton
public class AnimationHandler implements Listener, IReloadable
{

    private final Map<UUID, ZZZAnimation> sleepingAnimations;
    private final JavaPlugin plugin;

    private final BPLogger logger;

    @Inject
    public AnimationHandler(JavaPlugin plugin, BPLogger logger)
    {
        this.plugin = plugin;
        this.logger = logger;

        this.sleepingAnimations = new HashMap<>();
    }

    public void startSleepingAnimation(Player player)
    {
        this.logger.log(Level.FINEST, "Starting animation for player " + player.getName());

        // Start animations
        ZZZAnimation animation = new ZZZAnimation(Particle.COMPOSTER, 0.5, 0.1, 200, plugin);
        animation.startAnimation( new PlayerSleepLocation( player ));
        ZZZAnimation previous = this.sleepingAnimations.put(player.getUniqueId(), animation);

        // Stop previous animation, if one was active
        if (previous != null)
        {
            previous.stopAnimation();
        }
    }

    @EventHandler
    public void timeSetToDayEvent(BecomeDayEvent event)
    {
        this.logger.log(Level.FINEST, "Stopping animations for all players");

        // Stop all animations
        this.sleepingAnimations.forEach( (uuid, animation) -> animation.stopAnimation());
        this.sleepingAnimations.clear();
    }

    @EventHandler
    public void onBedLeave(PlayerBedLeaveEvent event)
    {
        this.logger.log(Level.FINEST, "Stopping animation for player " + event.getPlayer().getName() + " due to waking up");
        UUID uuid =  event.getPlayer().getUniqueId();
        stopAnimation( uuid );
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event)
    {
        this.logger.log(Level.FINEST, "Stopping animation for player " + event.getPlayer().getName() + " due to leaving");
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

    @Override
    public void reload()
    {
        // Stop all animations
        this.sleepingAnimations.forEach( (uuid, animation) -> animation.stopAnimation());
        this.sleepingAnimations.clear();
    }
}
