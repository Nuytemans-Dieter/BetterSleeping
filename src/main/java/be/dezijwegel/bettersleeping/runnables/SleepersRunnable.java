package be.dezijwegel.bettersleeping.runnables;

import be.dezijwegel.bettersleeping.interfaces.SleepersNeededCalculator;
import be.dezijwegel.bettersleeping.messenger.MsgEntry;
import be.dezijwegel.bettersleeping.messenger.PlayerMessenger;
import be.dezijwegel.bettersleeping.timechange.TimeChanger;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SleepersRunnable extends BukkitRunnable {

    // Final data
    private final World world;
    private final Set<Player> sleepers;

    // Utility
    private final SleepersNeededCalculator sleepersCalculator;
    private final TimeChanger timeChanger;
    private final PlayerMessenger messenger;

    // Variables for internal working
    private int numNeeded;
    private long oldTime;
    private boolean notifyEnoughSleeping = true;


    /**
     * A runnable that will detect time changes and its cause
     * @param world
     */
    public SleepersRunnable(World world, PlayerMessenger messenger, TimeChanger timeChanger, SleepersNeededCalculator sleepersCalculator)
    {
        this.world = world;
        this.messenger = messenger;
        oldTime = world.getTime();

        this.timeChanger = timeChanger;

        this.sleepersCalculator = sleepersCalculator;
        numNeeded = sleepersCalculator.getNumNeeded(world);

        this.sleepers = new HashSet<>();
    }


    /**
     * Mark a player as sleeping
     * A reference to the player will be stored
     * @param player the now sleeping player
     */
    public void playerEnterBed(Player player)
    {
        sleepers.add(player);
        numNeeded = sleepersCalculator.getNumNeeded(world);
        int remaining = numNeeded > sleepers.size() ? numNeeded - sleepers.size() : 0;

        messenger.sendMessage(player, "bed_enter_message",
                new MsgEntry("<num_sleeping>",      "" + sleepers.size()),
                new MsgEntry("<needed_sleeping>",   "" + numNeeded),
                new MsgEntry("<remaining_sleeping>","" + remaining));

        List<Player> players = world.getPlayers();
        players.remove(player);
        messenger.sendMessage(players, "bed_enter_broadcast",
                new MsgEntry("<player>", player.getDisplayName()),
                new MsgEntry("<num_sleeping>",      "" + sleepers.size()),
                new MsgEntry("<needed_sleeping>",   "" + numNeeded),
                new MsgEntry("<remaining_sleeping>","" + remaining));
    }


    /**
     * Mark a player as awake
     * The player's reference will be deleted
     * @param player the now awake player
     */
    public void playerLeaveBed(Player player)
    {
        sleepers.remove(player);
        numNeeded = sleepersCalculator.getNumNeeded(world);
    }



    @Override
    public void run() {


        /*/
         *  TIME DETECTOR
        /*/


        // Time check subsystem: detect time set to day
        long newTime = world.getTime();

        // True if time is set to day
        if (newTime < oldTime + 1) {
            messenger.sendMessage(world.getPlayers(), "morning_message");
        }

        oldTime = newTime;

        // Early return if players can't sleep anyway
        if (newTime < TimeChanger.TIME_RAIN_NIGHT && !world.isThundering())
            return;


        /*/
         *  SLEEP HANDLER
        /*/


        // Make sure the set does not contain any false sleepers
        sleepers.removeIf(player -> !player.isSleeping());

        if (sleepers.size() >= numNeeded)
        {
            timeChanger.tick(sleepers.size(), numNeeded);

            if (notifyEnoughSleeping) {
                int remaining = numNeeded > sleepers.size() ? numNeeded - sleepers.size() : 0;
                messenger.sendMessage(world.getPlayers(), "enough_sleeping",
                        new MsgEntry("<num_sleeping>",      "" + sleepers.size()),
                        new MsgEntry("<needed_sleeping>",   "" + numNeeded),
                        new MsgEntry("<remaining_sleeping>","" + remaining));
                notifyEnoughSleeping = false;
            }
        } else if (!notifyEnoughSleeping && sleepers.size() > 0)
        {
                int remaining = numNeeded - sleepers.size();
                messenger.sendMessage(world.getPlayers(), "skipping_canceled",
                        new MsgEntry("<num_sleeping>", "" + sleepers.size()),
                        new MsgEntry("<needed_sleeping>", "" + numNeeded),
                        new MsgEntry("<remaining_sleeping>", "" + remaining));
                notifyEnoughSleeping = true;
        }
        

    }
}
