package be.dezijwegel.management;

import be.dezijwegel.BetterSleeping;
import be.dezijwegel.files.Config;
import be.dezijwegel.files.Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
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

        boolean sendMessagesInChat = config.getBoolean("messages_in_chat");
        Lang.SendType sendType;
        if (sendMessagesInChat) sendType = Lang.SendType.CHAT;
        else                    sendType = Lang.SendType.SCREEN;

        lang = new Lang(plugin, sendType, config.getBoolean("message_sound"));

        buffs = new BuffManagement(plugin);

        checkConfiguration();
    }

    /**
     * Get the Config class with al settings
     * @return
     */
    public Config getConfig()
    {
        return config;
    }

    /**
     * This method will report any faulty configurations to the console
     */
    private void checkConfiguration()
    {
        // No errors can be made if messages are sent in chat
        if (config.getBoolean("messages_in_chat"))
        {
            return;
        }

        if ( (!lang.isPathDisabled("buff_received") || (!lang.isPathDisabled("no_buff_received")) ) && !(lang.isPathDisabled("good_morning")))
        {
            ConsoleCommandSender console = Bukkit.getConsoleSender();

            console.sendMessage("[BetterSleeping] " + ChatColor.RED + "Players may not receive all messages due to the messages_in_chat setting.");
            if (!lang.isPathDisabled("buff_received"))
                console.sendMessage("[BetterSleeping] " + ChatColor.RED + "good_morning may not be visible to all users. You can either disable buff_received or good_morning");
            if (!lang.isPathDisabled("no_buff_received"))
                console.sendMessage("[BetterSleeping] " + ChatColor.RED + "good_morning may not be visible to all users. You can either disable no_buff_received or good_morning");
            console.sendMessage("[BetterSleeping] " + ChatColor.RED + "Alternatively, you can set messages_in_chat to true.");
        }

        /**
         * import net.md_5.bungee.api.ChatMessageType;
         * import net.md_5.bungee.api.chat.BaseComponent;
         * import net.md_5.bungee.api.chat.TextComponent;
         */
        // If messages are sent on screen AND the server is not running Spigot -> Warn the console!
        if ( ! isUsingSpigot() )
        {
            ConsoleCommandSender console = Bukkit.getConsoleSender();

            console.sendMessage("[BetterSleeping] " + ChatColor.DARK_RED + "You are not using Spigot so messages cannot be displayed on screen!");
            console.sendMessage("[BetterSleeping] " + ChatColor.RED + "Please set 'messages_in_chat' in config.yml to false start using Spigot to prevent console errors.");
            console.sendMessage("[BetterSleeping] " + ChatColor.RED + "The option 'messages_in_chat' in config.yml is now being ignored and messages will be sent through chat!");
            lang = new Lang(plugin, Lang.SendType.CHAT, config.getBoolean("message_sound"));
        }
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

    /**
     * Check if the server is running Spigot
     * @return true if Spigot is being used. False in every other case
     */
    public boolean isUsingSpigot () {
        try  {
            Class.forName( "org.spigotmc.SpigotConfig" );
            return true;
        }  catch (ClassNotFoundException e) {
            return false;
        }
    }

}
