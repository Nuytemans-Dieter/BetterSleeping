package be.dezijwegel.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ScreenMessageSender {

    /**
     * Send a message on-screen to a player
     * The server MUST be running Spigot! No checks will be performed.
     * @param player the player who will receive this message
     * @param message the raw message
     */
    public static void sendMessage(Player player, String message)
    {
        Player.Spigot p = player.spigot();

        BaseComponent bc = new TextComponent();
        bc.addExtra( message );
        ChatMessageType type = ChatMessageType.ACTION_BAR;
        p.sendMessage(type, bc);
    }

}
