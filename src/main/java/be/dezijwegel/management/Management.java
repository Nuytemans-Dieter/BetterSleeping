package be.dezijwegel.management;

import be.dezijwegel.BetterSleeping;
import be.dezijwegel.files.Config;
import be.dezijwegel.files.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;


public class Management {

    private BetterSleeping plugin;
    private Config config;
    private Lang lang;
    private BuffManagement buffs;

    public Management (BetterSleeping plugin) {
        this.plugin = plugin;
        config = new Config(plugin);
        lang = new Lang(plugin);
        buffs = new BuffManagement(plugin);
    }

    /**
     * Add all buffs to a player
     * @param player
     */
    public void addEffects(Player player)
    {
        buffs.addEffects(player);
    }

    /**
     * Add all buffs to a List of players
     * @param players
     */
    public void addEffects(List<Player> players)
    {
        for (Player player : players)
        {
            buffs.addEffects(player);
        }
    }

    /**
     * Get the amount of enabled buffs
     * @return
     */
    public int getNumBuffs()
    {
        return buffs.getNumBuffs();
    }

    /**
     * Check if one or more buffs will be given after sleeping
     * @return
     */
    public boolean areBuffsEnabled()
    {
        return buffs.areBuffsEnabled();
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

    /**
     * Send a message to a receiver in the provided list and perform replacements
     * @param messagePath
     * @param receiver
     * @param replacings
     */
    public void sendMessage(String messagePath, CommandSender receiver, Map<String, String> replacings) {
        lang.sendMessage(messagePath, receiver, replacings);
    }

    /**
     * Send a message to a receiver in the provided list, perform replacements and fix singular/plural nouns
     * @param messagePath
     * @param receiver
     * @param replacings
     */
    public void sendMessage(String messagePath, CommandSender receiver, Map<String, String> replacings, boolean singular) {
        lang.sendMessage(messagePath, receiver, replacings, singular);
    }

    /**
     * Send a message to all receivers in the provided list
     * @param messagePath
     * @param receivers
     */
    public void sendMessageToGroup(String messagePath, List<Player> receivers)
    {
        lang.sendMessageToGroup(messagePath, receivers);
    }

    /**
     * Send a message to all receivers in the provided list and perform replacements
     * @param messagePath
     * @param receivers
     * @param replacings
     */
    public void sendMessageToGroup(String messagePath, List<Player> receivers, Map<String, String> replacings)
    {
        lang.sendMessageToGroup(messagePath,receivers,replacings);
    }

    /**
     * Send a message to all receivers in the provided list, perform replacements and fix singular/plural nouns
     * @param messagePath
     * @param receivers
     * @param replacings
     */
    public void sendMessageToGroup(String messagePath, List<Player> receivers, Map<String, String> replacings, boolean singular)
    {
        lang.sendMessageToGroup(messagePath,receivers,replacings, singular);
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
