package be.dezijwegel.bettersleeping.messaging;

import be.dezijwegel.bettersleeping.util.Version;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.protocol.packet.Chat;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Messenger {


    private final Map<String, String> messages;     // Contains the messages in lang.yml by mapping path to value
    private final boolean doShortenPrefix;


    /**
     * Creates a messenger for player output
     * @param messages the messages from lang.yml, mapping path to message
     */
    public Messenger(Map<String, String> messages, boolean doShortenPrefix)
    {
        this.messages = messages;
        this.doShortenPrefix = doShortenPrefix;
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
                if (options.length >= 3)
                {
                    try
                    {
                        double amount = Double.parseDouble(options[0]);
                        replaceBy[i] = amount == 1 ? options[1] : options[2];
                    }
                    catch(NumberFormatException exception)
                    {
                        new ConsoleLogger(true).log("A tag was not recognised: '" + options[0] + "'. Please check your language configuration.", ChatColor.RED);
                        replaceBy[i] = options[1];
                    }
                }
                else if (options.length >= 1)
                {
                    replaceBy[i] = options[options.length - 1];
                }
            }

            message = StringUtils.replaceEach(message, replaceThis, replaceBy);
            message = message.replaceAll("\\[", "").replaceAll("]", "");
        }

        // Get the prefix and put it before the message
        String prefix = doShortenPrefix ? "&6[BS] &3" : "&6[BetterSleeping] &3";
        message = prefix + message;
        message = message.replace('&', '§');
        message = replaceRGBFormatByColor( message );

        return message;
    }


    private String replaceRGBFormatByColor(String string)
    {
        String[] rgbList = StringUtils.substringsBetween(string, "$(", ")$");

        if (rgbList == null)
            return string;

        // Get Spigot and required version
        String spigotVersionStr = Bukkit.getServer().getBukkitVersion().split("-")[0];
        Version spigotVersion = new Version( spigotVersionStr );
        Version rgbVersion = new Version(1, 16, 0);

        // If 1.16+, use rgb
        if ( spigotVersion.compareTo(rgbVersion) >= 0 )
        {
            for (String s : rgbList)
            {
                net.md_5.bungee.api.ChatColor chatColor = null;

                String[] colors = s.split(",");
                if (colors != null && colors.length == 3)
                {
                    try
                    {
                        int r = Integer.parseInt(colors[0].replaceAll(" ", ""));
                        int g = Integer.parseInt(colors[1].replaceAll(" ", ""));
                        int b = Integer.parseInt(colors[2].replaceAll(" ", ""));
                        Color color = new Color(r, g, b);
                        chatColor = net.md_5.bungee.api.ChatColor.of(color);
                    } catch (NumberFormatException ignored) {}
                }

                if (chatColor != null)
                    string = string.replaceFirst("\\$\\(.*?\\)\\$", "" + chatColor);
            }
        }

        // Remove remaining tags
        string = string.replaceAll("\\$\\(.*?\\)\\$", "");

        return string;
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
            String name = receiver.getName();
            String finalMessage = message.replace("<user>", ChatColor.stripColor( name ));

            boolean isSuccess = false;

            if (receiver instanceof Player)
                try
                {
                    Class.forName("org.spigotmc.SpigotConfig");
                    ((Player)receiver).spigot().sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacyText(finalMessage));
                    isSuccess = true;
                } catch (ClassNotFoundException ignored) {}

            if (!isSuccess)
                receiver.sendMessage( finalMessage );
        }
    }
}
