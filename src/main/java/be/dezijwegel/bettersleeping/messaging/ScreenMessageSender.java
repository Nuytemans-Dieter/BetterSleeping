package be.dezijwegel.bettersleeping.messaging;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ScreenMessageSender {

    private static final long MESSAGE_DELAY = 3L * 20L;

    private final Plugin plugin;
    private final Player player;
    private final Queue<String> messageQueue = new ConcurrentLinkedQueue();

    public ScreenMessageSender(Plugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    /**
     * Send a message on-screen to a player The server MUST be running Spigot!
     * No checks will be performed.
     *
     * @param message the raw message
     */
    public void sendMessage(String message) {
        synchronized (messageQueue) {
            messageQueue.add(message);
            if (messageQueue.size() == 1) {
                sendMessage();
            }
        }
    }

    private void sendMessage() {
        String message = messageQueue.peek();

        Player.Spigot p = player.spigot();
        BaseComponent bc = new TextComponent();
        bc.addExtra(message);
        ChatMessageType type = ChatMessageType.ACTION_BAR;
        p.sendMessage(type, bc);

        new BukkitRunnable() {
            @Override
            public void run() {
                synchronized (messageQueue) {
                    messageQueue.remove();
                    if (!messageQueue.isEmpty()) {
                        sendMessage();
                    }
                }
            }
        }.runTaskLater(plugin, MESSAGE_DELAY);
    }

}
