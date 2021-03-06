package be.betterplugins.bettersleeping.listeners;

import be.betterplugins.bettersleeping.model.ConfigContainer;
import com.google.inject.Inject;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class PhantomHandler implements Listener
{

    private final boolean disablePhantoms;

    @Inject
    public PhantomHandler(ConfigContainer container)
    {
        this.disablePhantoms = container.getConfig().getBoolean("disable_phantoms");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPhantomSpawn(EntitySpawnEvent spawnEvent)
    {
        if (disablePhantoms && spawnEvent.getEntityType() == EntityType.PHANTOM)
            spawnEvent.setCancelled(true);
    }

}
