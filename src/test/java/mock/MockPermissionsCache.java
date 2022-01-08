package mock;

import be.betterplugins.bettersleeping.model.permissions.PermissionsCache;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;

public class MockPermissionsCache extends PermissionsCache
{

    HashSet<Player> playersWithBypassPermission;

    public MockPermissionsCache(JavaPlugin plugin, HashSet<Player> playersWithBypassPermission)
    {
        super(plugin);
        this.playersWithBypassPermission = playersWithBypassPermission;
    }

    @Override
    public boolean hasPermission(Player player, String permission)
    {
        return playersWithBypassPermission.contains(player);
    }
}
