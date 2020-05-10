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
    private boolean notifyEnoughSleeping = true;
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
        int remaining = numNeeded > sleepers.size() ? numNeeded - sleepers.size() : 0;

        messenger.sendMessage(player, "bed_enter_message",
                new MsgEntry("<num_sleeping>",      "" + sleepers.size()),
                new MsgEntry("<needed_sleeping>",   "" + numNeeded),
                new MsgEntry("<remaining_sleeping>","" + remaining));

        List<Player> players = world.getPlayers();
        players.remove(player);
        messenger.sendMessage(players, "bed_enter_broadcast",
                new MsgEntry("<player>", ChatColor.stripColor( player.getDisplayName() )),
                new MsgEntry("<num_sleeping>",      "" + sleepers.size()),
                new MsgEntry("<needed_sleeping>",   "" + numNeeded),
                new MsgEntry("<remaining_sleeping>","" + remaining));
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

        sleepers.remove(player);
        bedLeaveTracker.put(player, world.getTime());

        numNeeded = sleepersCalculator.getNumNeeded(world);
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

        // True if time is set to day
        if (newTime < 10 && newTime < oldTime + 1) {

            // Find players who slept
            for (Map.Entry<Player, Long> entry : bedLeaveTracker.entrySet())
            {
                if ( (entry.getValue() < 10)  || (entry.getValue() >= 23450) )
                    sleepers.add( entry.getKey() );
            }


            // Find the skip cause
            TimeSetToDayEvent.Cause cause;

            // Caused by BetterSleeping?
            if ( timeChanger.wasTimeSetToDay() )
                cause = TimeSetToDayEvent.Cause.SLEEPING;
            // Natural passing of time?
            else if ( newTime == 0 && oldTime == 23999 )
                cause = TimeSetToDayEvent.Cause.NATURAL;
            // Caused by all players in a world sleeping -> Time is set to day instantly
            else if ( areAllPlayersSleeping )
                cause = TimeSetToDayEvent.Cause.SLEEPING;
            // Caused by some time setter?
            else
                cause = TimeSetToDayEvent.Cause.OTHER;


            if (cause == TimeSetToDayEvent.Cause.SLEEPING)
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
        {
            timeChanger.tick(sleepers.size(), numNeeded);

            if (notifyEnoughSleeping) {
                int remaining = numNeeded > sleepers.size() ? numNeeded - sleepers.size() : 0;
                List<? extends CommandSender> players = world.getPlayers();
                messenger.sendMessage(players, "enough_sleeping",
                        new MsgEntry("<num_sleeping>",      "" + sleepers.size()),
                        new MsgEntry("<needed_sleeping>",   "" + numNeeded),
                        new MsgEntry("<remaining_sleeping>","" + remaining));
                notifyEnoughSleeping = false;
            }
        } else if (!notifyEnoughSleeping && sleepers.size() > 0)
        {
            int remaining = numNeeded - sleepers.size();
            List<? extends CommandSender> players = world.getPlayers();
            messenger.sendMessage(world.getPlayers(), "skipping_canceled",
                    new MsgEntry("<num_sleeping>", "" + sleepers.size()),
                    new MsgEntry("<needed_sleeping>", "" + numNeeded),
                    new MsgEntry("<remaining_sleeping>", "" + remaining));
            notifyEnoughSleeping = true;
        }
        

    }
}
