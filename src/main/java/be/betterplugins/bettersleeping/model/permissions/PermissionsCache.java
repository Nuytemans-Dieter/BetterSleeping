package be.betterplugins.bettersleeping.model.permissions;

import be.betterplugins.bettersleeping.messaging.ScreenMessenger;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class PermissionsCache implements Listener
{

    Map<UUID, Map<String, Boolean>> permissionsCacheMap;

    @Inject
    public PermissionsCache(JavaPlugin plugin)
    {
        this.permissionsCacheMap = new HashMap<>();

        // Argument may be null in unit tests
        if (plugin != null)
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public boolean hasPermission(Player player, String permission)
    {
        Map<String, Boolean> permissionMap = permissionsCacheMap.get(player.getUniqueId());

        if (permissionMap == null)
        {
            permissionMap = new HashMap<>();
        }

        if (!permissionMap.containsKey(permission))
        {
            permissionMap.put(permission, player.hasPermission(permission));
        }

        permissionsCacheMap.put(player.getUniqueId(), permissionMap);

        return permissionMap.get(permission);
    }

    @EventHandler
    public void onPlayerLeave (PlayerQuitEvent playerQuitEvent)
    {
        this.permissionsCacheMap.remove(playerQuitEvent.getPlayer().getUniqueId());
    }
}
