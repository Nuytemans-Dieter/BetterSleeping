package be.dezijwegel.bettersleeping;

import be.dezijwegel.files.Config;
import be.dezijwegel.files.Lang;
import org.bukkit.command.CommandSender;

import java.util.Map;


public class Management {

    private Config config;
    private Lang lang;

    public Management (BetterSleeping plugin)
    {
        config = new Config(plugin);
        lang = new Lang(plugin);
    }

    /**
     * Send the given message from lang.yml to the player (prefix will be automatically included, if present)
     * @param messagePath
     * @param receiver
     */
    public void sendMessage (String messagePath, CommandSender receiver)
    {
        lang.sendMessage(messagePath, receiver);
    }

    public void sendMessage(String messagePath, CommandSender receiver, Map<String, String> replacings) {
        lang.sendMessage(messagePath, receiver, replacings);
    }

    /**
     * Get a Boolean setting from the config file
     * Returns null if no such option is present
     * @param path
     * @return
     */
    public Boolean getBooleanSetting(String path)
    {
        return config.getBoolean(path);
    }

    /**
     * Get an int setting from the config file
     * Returns null if no such option is present
     * @param path
     * @return
     */
    public Integer getIntegerSetting(String path)
    {
        return config.getInt(path);
    }
}
