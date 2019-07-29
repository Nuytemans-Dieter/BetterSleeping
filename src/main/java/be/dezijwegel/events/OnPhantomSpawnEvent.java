package be.dezijwegel.events;

import be.dezijwegel.management.Management;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class OnPhantomSpawnEvent implements Listener {

    Management management;

    public OnPhantomSpawnEvent(Management management)
    {
        this.management = management;
    }

    /**
     * If phantom spawns are disabled, this handler will prevent them from spawning
     * @param event an EntitySpawnEvent
     */
    @EventHandler
    public void onPhantomSpawn(EntitySpawnEvent event)
    {
        if (event.getEntityType() == EntityType.PHANTOM)
        {
            if ( management.getBooleanSetting("disable_phantoms") == true )
            {
                event.setCancelled(true);
            }
        }
    }
}
