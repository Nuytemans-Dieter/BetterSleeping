package be.dezijwegel.runnables;

import be.dezijwegel.files.Lang;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class SendMessageRunnable extends BukkitRunnable {

    private Lang lang;

    private List<Player> players;       // A list of player that will receive the message
    private Player player;              // A single player that will receive the message

    private String messagePath;         // The path to the message that will be sent


    /**
     * Set the receivers that will get the message from lang.yml
     * @param receivers a list of receivers for this message
     * @param messagePath the path to the message in lang.yml
     */
    public SendMessageRunnable(Lang lang, List<Player> receivers, String messagePath)
    {
        this.lang = lang;

        this.players = receivers;
        this.messagePath = messagePath;
    }

    /**
     * Set the receiver that will get the message from lang.yml
     * @param receiver a list of receivers for this message
     * @param messagePath the path to the message in lang.yml
     */
    public SendMessageRunnable(Lang lang, Player receiver, String messagePath)
    {
        this.lang = lang;

        this.player = receiver;
        this.messagePath = messagePath;
    }

    @Override
    public void run() {
        if (player != null)
        {
            lang.sendMessage(messagePath, player);
        }

        if (players != null)
        {
            lang.sendMessageToGroup(messagePath, players);
        }
    }
}
