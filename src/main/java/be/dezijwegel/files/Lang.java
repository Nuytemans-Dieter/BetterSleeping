package be.dezijwegel.files;

import be.dezijwegel.bettersleeping.BetterSleeping;
import be.dezijwegel.bettersleeping.Reloadable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class Lang implements Reloadable {

    private BetterSleeping plugin;
    private ConfigAPI configAPI;

    public Lang (BetterSleeping plugin)
    {
        this.plugin = plugin;

        configAPI = new ConfigAPI(ConfigAPI.FileType.LANG, plugin);
    }

    /**
     * (Attempt to) send a message to the given receiver
     * The message must exist in the default lang.yml or lang.yml on disk
     * @param messagePath
     * @param receiver
     */
    public void sendMessage(String messagePath, CommandSender receiver)
    {
        String msg = composeMessage(messagePath);
        if (msg != "") receiver.sendMessage(msg);
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
        if (msg != "") receiver.sendMessage(prepareMessage(msg,replacings));
    }

    /**
     * (Attempt to) send a message to the given receiver
     * The message must exist in the default lang.yml or lang.yml on disk
     * The placeholders (keys of replacings) will be replaced by their respective values
     * If a [singular.plural] part exists, it will be corrected based on the given boolean
     * @param messagePath
     * @param receiver
     * @param replacings
     */
    public void sendMessage(String messagePath, CommandSender receiver, Map<String, String> replacings, boolean singular)
    {
        String msg = composeMessage(messagePath);
        msg = correctSingularPlural(prepareMessage(msg,replacings), singular);
        if (msg != "") receiver.sendMessage(msg);
    }

    /**
     * Send a given String to a group of receivers
     * @param messagePath
     * @param receivers
     */
    public void sendMessageToGroup(String messagePath, List<Player> receivers)
    {
        String msg = composeMessage(messagePath);
        if (msg != "")
        {
            for (Player player : receivers) {
                player.sendMessage(msg);
            }
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
        if (msg != "")
        {
            String replaced = prepareMessage(msg, replacings);
            for (Player player : receivers) {
                player.sendMessage(replaced);
            }
        }
    }

    /**
     * (Attempt to) send a message to the given receivers
     * The message must exist in the default lang.yml or lang.yml on disk
     * The placeholders (keys of replacings) will be replaced by their respective values
     * If a [singular.plural] part exists, it will be corrected based on the given boolean
     * @param messagePath
     * @param receivers
     * @param replacings
     */
    public void sendMessageToGroup(String messagePath, List<Player> receivers, Map<String,String> replacings, boolean singular)
    {
        String msg = composeMessage(messagePath);
        if (msg != "")
        {
            String replaced = prepareMessage(msg, replacings);
            replaced = correctSingularPlural(replaced, singular);
            for (Player player : receivers) {
                player.sendMessage(replaced);
            }
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

        if (configAPI.getString("prefix") != null)
        {
            String prefix = configAPI.getString("prefix");
            message += prefix;
        }

        if (configAPI.getString(messagePath) != null)
        {
            if (configAPI.getString(messagePath).equalsIgnoreCase("ignored"))
                message = "";
            else message = message + (String) configAPI.getString(messagePath);
        }

        return message;
    }

    /**
     * Replace certain Strings within the given message
     * @return
     */
    public String prepareMessage(String message, Map<String, String> replacings)
    {
//        if (BetterSleeping.debug)
//        {
//            System.out.println("-----");
//            System.out.println("Preparing message: " + message);
//
//            for (Map.Entry<String, String> entry : replacings.entrySet()) {
//                if (message.contains(entry.getKey())) {
//                    System.out.println("Replace " + entry.getKey() + " with " + entry.getValue());
//                    message = message.replaceAll(entry.getKey(), entry.getValue());
//                } else {
//                    System.out.println("Message did not contain " + entry.getKey());
//                }
//            }
//            System.out.println("Result: " + message);
//            System.out.println("-----");
//        } else {
            for (Map.Entry<String, String> entry : replacings.entrySet()) {
                if (message.contains(entry.getKey())) {
                    message = message.replaceAll(entry.getKey(), entry.getValue());
                }
            }
//        }
        return message;
    }

    /**
     * Replaces [singular.plural] to the correct one (singular or plural), based on a given amount. The corrected String is returned.
     * @param str The String that will be checked for singular/plural nouns in the form of [singular.plural]
     * @param singular set to true if the String should be corrected to be singular, or plural if false
     * @return the corrected String
     */
    public String correctSingularPlural(String str, boolean singular)
    {
        String string = str;
        boolean bracketsOpen = false;
        int startIndex = 0;
        for (int ind = 0; ind < string.length()-1; ind++)
        {
            if (bracketsOpen)
            {
                if (string.charAt(ind) == ']')
                {
                    String temp = string.substring(startIndex+1,ind);
                    bracketsOpen = false;

                    if (str.contains("."))
                    {
                        String[] strings = temp.split("\\.");

                        if (singular) str = str.replace("[" + temp + "]",strings[0]);
                        else str = str.replace("[" + temp + "]",strings[1]);

                    } else str = str.replace("[" + temp + "]", temp);
                    System.out.println(str);
                }
            }
            else
            {
                if (string.charAt(ind) == '[')
                {
                    startIndex = ind;
                    bracketsOpen = true;
                }
            }
        }

        return str;
    }

    @Override
    public void reload() {
        configAPI = new ConfigAPI(ConfigAPI.FileType.LANG, plugin);
//        lang = new HashMap<String, Object>();
//
//        configAPI.loadTypesFromFile(String.class, lang);
    }
}
