package be.dezijwegel.messenger;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.regex.Pattern;

public class PlayerMessenger {


    private final Map<String, String> messages;     // Contains the messages in lang.yml by mapping path to value


    /**
     * Creates a messenger for player output
     * @param messages the messages from lang.yml, mapping path to message
     */
    public PlayerMessenger(Map<String, String> messages)
    {
        this.messages = messages;
    }


    /**
     * Send a message from lang.yml to a player
     * If the message does not exist, it will be sent to the player in its raw form
     * As optional parameter, a list or several MsgEntries can be given as parameter
     * @param player the player
     * @param messageID the id of the message
     */
    public void sendMessage(Player player, String messageID, MsgEntry... replacements)
    {
        // Get the message from lang.yml OR if non existent, get the raw message
        String message = messages.getOrDefault(messageID, messageID);

        // Perform all replacements
        for (MsgEntry entry : replacements)
        {
            message = message.replace(entry.getTag(), entry.getReplacement());
        }

        // Get the prefix and put it before the message
        String prefix = "&6[BetterSleeping] &3";
        message = prefix + message;

        message = message.replace("<user>", player.getDisplayName());

        player.sendMessage(prefix + message);
    }

}
