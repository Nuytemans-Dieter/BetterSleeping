package be.dezijwegel.bettersleeping.messenger;

import be.dezijwegel.bettersleeping.util.ConsoleLogger;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Messenger {


    private final Map<String, String> messages;     // Contains the messages in lang.yml by mapping path to value
    private final ConsoleLogger consoleLogger;


    /**
     * Creates a messenger for player output
     * @param messages the messages from lang.yml, mapping path to message
     */
    public Messenger(Map<String, String> messages)
    {
        this.messages = messages;
        this.consoleLogger = new ConsoleLogger(true);
    }


    /**
     * Compose a ready-to-be-sent BetterSleeping message
     * @param messageID the ID of the message, or a custom message
     * @param replacements the tag replacements for this message
     * @return the message ready to be sent
     */
    private String composeMessage(String messageID, MsgEntry... replacements)
    {
        // Get the message from lang.yml OR if non existent, get the raw message
        String message = messages.getOrDefault(messageID, messageID);

        // Early return if the message is disabled
        if (message.equals(""))
            return "";

        // Perform variable replacements
        for (MsgEntry entry : replacements)
        {
            message = message.replace(entry.getTag(), entry.getReplacement());
        }

        // Singular/plural support
        String[] replaceThis = StringUtils.substringsBetween(message, "[", "]");
        if (replaceThis != null)
        {
            String[] replaceBy = new String[replaceThis.length];
            for (int i = 0; i < replaceThis.length; i++)
            {
                String[] options = replaceThis[i].split("\\.");
                if (options.length == 3)
                {
                    double amount = Double.parseDouble(options[0]);
                    replaceBy[i] = amount == 1 ? options[1] : options[2];
                }
                else
                {
                    replaceBy[i] = options[1];
                }
            }

            message = StringUtils.replaceEach(message, replaceThis, replaceBy);
            message = message.replaceAll("\\[", "").replaceAll("]", "");
        }

        // Get the prefix and put it before the message
        String prefix = "&6[BetterSleeping] &3";
        message = prefix + message;
        message = message.replace('&', '§');

        return message;
    }


    /**
     * Send a message from lang.yml to a CommandSender
     * If the message does not exist, it will be sent to the player in its raw form
     * As optional parameter, a list or several MsgEntries can be given as parameter
     * @param receiver the receiver
     * @param messageID the id of the message
     */
    public void sendMessage(CommandSender receiver, String messageID, MsgEntry... replacements)
    {
        sendMessage(Collections.singletonList(receiver), messageID, replacements);
    }



    /**
     * Send a message from lang.yml to a list of players
     * If the message does not exist, it will be sent to the player in its raw form
     * As optional parameter, a list or several MsgEntries can be given as parameter
     * @param receivers the list of players
     * @param messageID the id of the message
     */
    public void sendMessage(List<? extends CommandSender> receivers, String messageID, MsgEntry... replacements)
    {
        // Compose the message and return if message is disabled
        String message = composeMessage(messageID, replacements);
        if (message.equals(""))
            return;

        // Send everyone a message
        for (CommandSender receiver : receivers)
        {
            // Get the senders name
            String name;
            if (receiver instanceof Player)
            {
                Player player = (Player)receiver;
                name = player.getDisplayName();
            }
            else
            {
                name = receiver.getName();
            }

            message = message.replace("<user>", ChatColor.stripColor( name ));
            receiver.sendMessage(message);
        }
    }
}
