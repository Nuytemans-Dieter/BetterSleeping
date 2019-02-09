package be.dezijwegel.files;

import be.dezijwegel.bettersleeping.BetterSleeping;
import be.dezijwegel.bettersleeping.Reloadable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Lang implements Reloadable {

    private BetterSleeping plugin;
    private ConfigAPI configAPI;
    private Map<String, Object> lang;

    public Lang (BetterSleeping plugin)
    {
        this.plugin = plugin;

        configAPI = new ConfigAPI(ConfigAPI.FileType.LANG, plugin);
        lang = new HashMap<String, Object>();

        configAPI.loadTypesFromFile(String.class, lang);
    }

    /**
     * (Attempt to) send a message to the given receiver
     * The message must exist in the default lang.yml or lang.yml on disk
     * @param messagePath
     * @param receiver
     */
    public void sendMessage(String messagePath, CommandSender receiver)
    {
        receiver.sendMessage(composeMessage(messagePath));
    }

    /**
     * (Attempt to) send a message to the given receiver
     * The message must exist in the default lang.yml or lang.yml on disk
     * The placeholders (keys of replacings) will be replaced by their respective values
     * @param messagePath
     * @param receiver
     * @param replacings
     */
    public void sendMessage(String messagePath, CommandSender receiver, Map<String, String> replacings)
    {
        String msg = composeMessage(messagePath);
        receiver.sendMessage(prepareMessage(msg,replacings));
    }

    /**
     * Send a given String to a group of receivers
     * @param messagePath
     * @param receivers
     */
    public void sendMessageToGroup(String messagePath, List<Player> receivers)
    {
        String msg = composeMessage(messagePath);
        for (Player player : receivers)
        {
            player.sendMessage(msg);
        }
    }

    /**
     * (Attempt to) send a message to the given receivers
     * The message must exist in the default lang.yml or lang.yml on disk
     * The placeholders (keys of replacings) will be replaced by their respective values
     * @param messagePath
     * @param receivers
     * @param replacings
     */
    public void sendMessageToGroup(String messagePath, List<Player> receivers, Map<String,String> replacings)
    {
        String msg = composeMessage(messagePath);
        String replaced = prepareMessage(msg, replacings);
        for (Player player : receivers)
        {
            player.sendMessage(replaced);
        }
    }

    /**
     * Creates a String that combines prefix and message
     * @param messagePath
     * @return
     */
    public String composeMessage(String messagePath)
    {
        String message = "";

        if (lang.containsKey("prefix"))
        {
            Object prefix = lang.get("prefix");
            if (prefix != null && prefix instanceof String)
            message = message + (String) prefix;
        }

        if (lang.containsKey(messagePath))
        {
            if (lang.get(messagePath) instanceof String)
            {
                message = message + (String) lang.get(messagePath);
            }
        }

        return message;
    }

    /**
     * Replace certain Strings within the given message
     * @return
     */
    public String prepareMessage(String message, Map<String, String> replacings)
    {
        for (Map.Entry<String, String> entry : replacings.entrySet())
        {
            if (message.contains(entry.getKey()))
            {
                message.replaceAll(entry.getKey(), entry.getValue());
            }
        }
        return message;
    }

    @Override
    public void reload() {
        configAPI = new ConfigAPI(ConfigAPI.FileType.LANG, plugin);
        lang = new HashMap<String, Object>();

        configAPI.loadTypesFromFile(String.class, lang);
    }
}
