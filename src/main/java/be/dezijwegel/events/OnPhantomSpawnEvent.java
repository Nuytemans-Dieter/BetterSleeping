package be.dezijwegel.events;

import be.dezijwegel.management.Management;
import org.bukkit.Bukkit;
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
        if ( management.getBooleanSetting("disable_phantoms") && !Bukkit.getVersion().contains("1.12") )
        {
            if ( event.getEntityType() == EntityType.PHANTOM )
            {
                event.setCancelled(true);
            }
        }
    }
}
