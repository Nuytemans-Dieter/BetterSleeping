package be.dezijwegel.bettersleeping.runnables;

import be.dezijwegel.bettersleeping.events.custom.TimeSetToDayEvent;
import be.dezijwegel.bettersleeping.interfaces.SleepersNeededCalculator;
import be.dezijwegel.bettersleeping.messaging.MsgEntry;
import be.dezijwegel.bettersleeping.messaging.Messenger;
import be.dezijwegel.bettersleeping.sleepersneeded.AbsoluteNeeded;
import be.dezijwegel.bettersleeping.timechange.TimeChanger;
import be.dezijwegel.bettersleeping.util.SleepStatus;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SleepersRunnable extends BukkitRunnable {

    // Final data
    private final World world;
    private final Set<Player> sleepers;
    private final HashMap<Player, Long> bedLeaveTracker;

    // Utility
    private final SleepersNeededCalculator sleepersCalculator;
    private final TimeChanger timeChanger;
    private final Messenger messenger;

    // Variables for internal working
    private int numNeeded;
    private long oldTime;
    private boolean areAllPlayersSleeping = false;


    /**
     * A runnable that will detect time changes and its cause
     */
    public SleepersRunnable(World world, Messenger messenger, TimeChanger timeChanger, SleepersNeededCalculator sleepersCalculator)
    {
        this.world = world;
        this.messenger = messenger;
        oldTime = world.getTime();

        this.timeChanger = timeChanger;

        this.sleepersCalculator = sleepersCalculator;
        numNeeded = sleepersCalculator.getNumNeeded(world);

        this.sleepers = new HashSet<>();
        this.bedLeaveTracker = new HashMap<>();
    }


    /**
     * Mark a player as sleeping
     * A reference to the player will be stored
     * @param player the now sleeping player
     */
    public void playerEnterBed(Player player)
    {
        sleepers.add(player);
        bedLeaveTracker.remove(player);

        // Check whether all players are sleeping
        if (sleepers.size() == world.getPlayers().size())
            areAllPlayersSleeping = true;

        numNeeded = sleepersCalculator.getNumNeeded(world);

        int remaining = Math.max( numNeeded - sleepers.size() , 0);

        messenger.sendMessage(player, "bed_enter_message",
                new MsgEntry("<num_sleeping>",      "" + sleepers.size()),
                new MsgEntry("<needed_sleeping>",   "" + numNeeded),
                new MsgEntry("<remaining_sleeping>","" + remaining));


        if (sleepers.size() == numNeeded)
        {
            messenger.sendMessage(world.getPlayers(), "enough_sleeping",
                    new MsgEntry("<num_sleeping>",      "" + sleepers.size()),
                    new MsgEntry("<needed_sleeping>",   "" + numNeeded),
                    new MsgEntry("<remaining_sleeping>","" + remaining));
        } else if (sleepers.size() < numNeeded) {
            List<Player> players = world.getPlayers();
            players.remove(player);
            messenger.sendMessage(players, "bed_enter_broadcast",
                    new MsgEntry("<player>", ChatColor.stripColor( player.getDisplayName() )),
                    new MsgEntry("<num_sleeping>",      "" + sleepers.size()),
                    new MsgEntry("<needed_sleeping>",   "" + numNeeded),
                    new MsgEntry("<remaining_sleeping>","" + remaining));
        }
    }


    public SleepStatus getSleepStatus()
    {
        int set = sleepersCalculator.getSetting();
        String setting = (sleepersCalculator instanceof AbsoluteNeeded) ?   "An absolute amount of players has to sleep: " + set :
                                                                            set + "% of players needs to sleep";

        return new SleepStatus(sleepers.size(), numNeeded, world, timeChanger.getType(), setting);
    }


    /**
     * Mark a player as awake
     * The player's reference will be deleted
     * @param player the now awake player
     */
    public void playerLeaveBed(Player player)
    {
        if(areAllPlayersSleeping)
            return;

        int previousSize = sleepers.size();
        sleepers.remove(player);
        bedLeaveTracker.put(player, world.getTime());

        numNeeded = sleepersCalculator.getNumNeeded(world);

        // Don't send cancelled messages when the time is not right
        if (world.getTime() < 20 || world.getTime() > 23450)
            return;

        // Check if enough players WERE sleeping but now not anymore
        if (   sleepers.size() < previousSize
            && previousSize >= numNeeded
            && sleepers.size() < numNeeded
            && !timeChanger.removedStorm(false))
        {
            int remaining = numNeeded - sleepers.size();
            messenger.sendMessage(world.getPlayers(), "skipping_canceled",
                    new MsgEntry("<num_sleeping>", "" + sleepers.size()),
                    new MsgEntry("<needed_sleeping>", "" + numNeeded),
                    new MsgEntry("<remaining_sleeping>", "" + remaining));
        }
    }


    /**
     * Delete the player from all internal lists
     * @param player the player to be deleted
     */
    public void playerLogout(Player player)
    {
        playerLeaveBed(player);
        bedLeaveTracker.remove(player);
    }


    @Override
    public void run() {


        /*/
         *  TIME DETECTOR
        /*/


        // Time check subsystem: detect time set to day
        long newTime = world.getTime();

        // True if time is set to day OR the storm was skipped during the day
        if ((newTime < 10 && newTime < oldTime) || (timeChanger.removedStorm(true) && newTime < 12000)) {
            // Find players who slept
            if (areAllPlayersSleeping)
            {
                sleepers.addAll( world.getPlayers() );
            }
            else
            {
                for (Map.Entry<Player, Long> entry : bedLeaveTracker.entrySet())
                {
                    if ((entry.getValue() < 10) || (entry.getValue() >= 23450))
                        sleepers.add(entry.getKey());
                }
            }


            // Find the skip cause
            TimeSetToDayEvent.Cause cause;

            if ( timeChanger.wasTimeSetToDay() )                // Caused by BetterSleeping?
                cause = TimeSetToDayEvent.Cause.SLEEPING;
            else if ( areAllPlayersSleeping )                   // Caused by all players in a world sleeping -> Time is set to day instantly
                cause = TimeSetToDayEvent.Cause.SLEEPING;
            else if ( newTime == 0 && oldTime == 23999 )        // Natural passing of time?
                cause = TimeSetToDayEvent.Cause.NATURAL;
            else                                                // Caused by some time setter?
                cause = TimeSetToDayEvent.Cause.OTHER;


            if (cause != TimeSetToDayEvent.Cause.NATURAL)
            {
                // Send good morning, only when the players slept
                messenger.sendMessage(world.getPlayers(), "morning_message");
            }


            // Throw event for other devs to handle (and to handle buffs internally)
            List<Player> nonSleepers = world.getPlayers();
            nonSleepers.removeAll( sleepers );
            Event timeSetToDayEvent = new TimeSetToDayEvent(world, cause, new ArrayList<>(sleepers), nonSleepers);
            Bukkit.getPluginManager().callEvent( timeSetToDayEvent );

            // Reset state
            areAllPlayersSleeping = false;
            bedLeaveTracker.clear();
            sleepers.clear();
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
            timeChanger.tick(sleepers.size(), numNeeded);

    }
}
