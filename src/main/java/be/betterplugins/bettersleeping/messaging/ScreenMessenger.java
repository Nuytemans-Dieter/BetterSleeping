package be.betterplugins.bettersleeping.messaging;

import be.betterplugins.core.messaging.logging.BPLogger;
import be.betterplugins.core.messaging.messenger.Messenger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ScreenMessenger extends Messenger
{

    private final JavaPlugin plugin;
    private final Map<Player, ScreenMessageSender> playerMessengerMap = new ConcurrentHashMap<>();

    public ScreenMessenger(JavaPlugin plugin, Map<String, String> messages, String prefix, BPLogger logger)
    {
        super(messages, logger, prefix);
        this.plugin = plugin;

        // Register creation and deletion of messengers for every player
        plugin.getServer().getPluginManager().registerEvents(new PlayerQueueEventListener(), plugin);
        // Create messenger for every player online (e.g. in case of reload)
        plugin.getServer().getOnlinePlayers().forEach(player -> playerMessengerMap.put(player, new ScreenMessageSender(plugin, player)));
    }

    @Override
    protected void sendMessage(CommandSender receiver, String message)
    {
        if (receiver instanceof Player)
        {
            ScreenMessageSender sender = playerMessengerMap.get( receiver );
            sender.sendMessage(message);
        }
        else
        {
            // Fallback
            receiver.sendMessage(message);
        }
    }

    /**
     * Event listener that manages queue of all players and their corresponding
     * messengers
     */
    private class PlayerQueueEventListener implements Listener
    {

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event)
        {
            // Create messenger for every player joining
            playerMessengerMap.put(event.getPlayer(), new ScreenMessageSender(plugin, event.getPlayer()));
        }

        @EventHandler
        public void onPlayerJoin(PlayerQuitEvent event)
        {
            // Remove messenger for every player leaving
            playerMessengerMap.remove(event.getPlayer());
        }
    }

}
