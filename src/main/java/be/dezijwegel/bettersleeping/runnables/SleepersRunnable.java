package be.dezijwegel.bettersleeping.runnables;

import be.dezijwegel.bettersleeping.events.custom.TimeSetToDayEvent;
import be.dezijwegel.bettersleeping.interfaces.SleepersNeededCalculator;
import be.dezijwegel.bettersleeping.messaging.Messenger;
import be.dezijwegel.bettersleeping.messaging.MsgEntry;
import be.dezijwegel.bettersleeping.sleepersneeded.AbsoluteNeeded;
import be.dezijwegel.bettersleeping.timechange.TimeChanger;
import be.dezijwegel.bettersleeping.util.SleepStatus;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SleepersRunnable extends BukkitRunnable {
    // Final data
    private final World world;
    private final Set<UUID> sleepers;
    private final HashMap<UUID, Long> bedLeaveTracker;

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
    public SleepersRunnable(World world, Messenger messenger, TimeChanger timeChanger, SleepersNeededCalculator sleepersCalculator) {
        this.world = world;
        this.messenger = messenger;
        this.oldTime = world.getTime();
        this.timeChanger = timeChanger;
        this.sleepersCalculator = sleepersCalculator;
        this.numNeeded = sleepersCalculator.getNumNeeded(world);
        this.sleepers = new HashSet<>();
        this.bedLeaveTracker = new HashMap<>();
    }

    /**
     * Mark a player as sleeping
     * A reference to the player will be stored
     * @param player the now sleeping player
     */
    public void playerEnterBed(Player player) {
        this.sleepers.add(player.getUniqueId());
        this.bedLeaveTracker.remove(player.getUniqueId());

        // Check whether all players are sleeping
        if (this.sleepers.size() == this.world.getPlayers().size()) {
            this.areAllPlayersSleeping = true;
        }

        this.numNeeded = this.sleepersCalculator.getNumNeeded(this.world);

        int remaining = Math.max(this.numNeeded - this.sleepers.size() , 0);

        this.messenger.sendMessage(
            player,"bed_enter_message", false,
            new MsgEntry("<num_sleeping>", "" + this.sleepers.size()),
            new MsgEntry("<needed_sleeping>", "" + this.numNeeded),
            new MsgEntry("<remaining_sleeping>", "" + remaining)
        );

        boolean isEnoughSleepingEmpty = false;
        if (this.sleepers.size() == this.numNeeded) {
            isEnoughSleepingEmpty = ! this.messenger.sendMessage(
                this.world.getPlayers(), "enough_sleeping", false,
                new MsgEntry("<player>", ChatColor.stripColor(player.getName())),
                new MsgEntry("<num_sleeping>", "" + this.sleepers.size()),
                new MsgEntry("<needed_sleeping>", "" + this.numNeeded),
                new MsgEntry("<remaining_sleeping>", "" + remaining)
            );
        }

        if ((isEnoughSleepingEmpty && this.sleepers.size() <= this.numNeeded) || this.sleepers.size() < this.numNeeded) {
            List<Player> players = this.world.getPlayers();
            players.remove( player );
            messenger.sendMessage(
                players, "bed_enter_broadcast", false,
                new MsgEntry("<player>", ChatColor.stripColor(player.getName())),
                new MsgEntry("<num_sleeping>", "" + this.sleepers.size()),
                new MsgEntry("<needed_sleeping>", "" + this.numNeeded),
                new MsgEntry("<remaining_sleeping>", "" + remaining)
            );
        }
    }

    public SleepStatus getSleepStatus() {
        int set = this.sleepersCalculator.getSetting();
        String setting = ((this.sleepersCalculator instanceof AbsoluteNeeded)
            ? "An absolute amount of players has to sleep: " + set
            : set + "% of players needs to sleep"
        );

        return new SleepStatus(this.sleepers.size(), this.numNeeded, this.world, this.timeChanger.getType(), setting);
    }

    /**
     * Mark a player as awake
     * The player's reference will be deleted
     * @param player the now awake player
     */
    public void playerLeaveBed(Player player)
    {
        if (this.areAllPlayersSleeping) {
            return;
        }

        int previousSize = this.sleepers.size();
        this.sleepers.remove(player.getUniqueId());
        this.bedLeaveTracker.put(player.getUniqueId(), this.world.getTime());

        this.numNeeded = this.sleepersCalculator.getNumNeeded(this.world);

        // Don't send cancelled messages when the time is not right
        if (this.world.getTime() < 20 || this.world.getTime() > 23450) {
            return;
        }

        // Check if enough players WERE sleeping but now not anymore
        if (
            this.sleepers.size() < previousSize &&
            previousSize >= this.numNeeded &&
            this.sleepers.size() < this.numNeeded &&
            !this.timeChanger.removedStorm(false)
        ) {
            int remaining = this.numNeeded - this.sleepers.size();
            this.messenger.sendMessage(
                this.world.getPlayers(), "skipping_canceled", false,
                new MsgEntry("<player>", ChatColor.stripColor(player.getDisplayName())),
                new MsgEntry("<num_sleeping>", "" + this.sleepers.size()),
                new MsgEntry("<needed_sleeping>", "" + this.numNeeded),
                new MsgEntry("<remaining_sleeping>", "" + remaining)
            );
        }
    }

    /**
     * Delete the player from all internal lists
     * @param player the player to be deleted
     */
    public void playerLogout(Player player) {
        this.playerLeaveBed(player);
        this.bedLeaveTracker.remove(player.getUniqueId());

        // Update the needed count when players leave their bed so that the count is adjusted
        this.numNeeded = this.sleepersCalculator.getNumNeeded(this.world);
    }

    @Override
    public void run() {
        // TIME DETECTOR

        // Time check subsystem: detect time set to day
        long currentTime = this.world.getTime();

        // True if time is set to day OR the storm was skipped during the day
        if (
            (currentTime < 10 && currentTime < this.oldTime) ||
            (this.timeChanger.removedStorm(true) && currentTime < 12000)
        ) {
            // Find players who slept
            if (this.areAllPlayersSleeping)
            {
                this.world.getPlayers().forEach(player -> this.sleepers.add( player.getUniqueId() ));
            }
            else
            {
                for (Map.Entry<UUID, Long> entry : this.bedLeaveTracker.entrySet()) {
                    if ((entry.getValue() < 10) || (entry.getValue() >= 23450)) {
                        this.sleepers.add(entry.getKey());
                    }
                }
            }

            // Find the skip cause
            TimeSetToDayEvent.Cause cause;
            if (timeChanger.wasTimeSetToDay()) { // Caused by BetterSleeping?
                cause = TimeSetToDayEvent.Cause.SLEEPING;
            } else if (areAllPlayersSleeping) { // Caused by all players in a world sleeping -> Time is set to day instantly
                cause = TimeSetToDayEvent.Cause.SLEEPING;
            } else if (currentTime == 0 && oldTime == 23999) { // Natural passing of time?
                cause = TimeSetToDayEvent.Cause.NATURAL;
            } else { // Caused by some time setter?
                cause = TimeSetToDayEvent.Cause.OTHER;
            }

            if (cause != TimeSetToDayEvent.Cause.NATURAL) {
                // Send good morning, only when the players slept
                messenger.sendMessage(this.world.getPlayers(), "morning_message", false);
            }

            // Throw event for other devs to handle (and to handle buffs internally)
            List<Player> nonSleepers = this.world.getPlayers();
            List<Player> actualSleepers = new ArrayList<>();
            this.sleepers.forEach( uuid -> actualSleepers.add( Bukkit.getPlayer( uuid ) ) );
            Event timeSetToDayEvent = new TimeSetToDayEvent(world, cause, actualSleepers, nonSleepers);
            Bukkit.getPluginManager().callEvent(timeSetToDayEvent);

            // Reset state
            this.areAllPlayersSleeping = false;
            this.bedLeaveTracker.clear();
            this.sleepers.clear();
        }

        this.oldTime = currentTime;

        // SLEEP HANDLER

        // Find all players that are no longer sleeping and remove them from the list
        List<UUID> awakePlayers = new ArrayList<>();
        for (UUID uuid : this.sleepers)
        {
            Player player = Bukkit.getPlayer( uuid );
            if (player != null && ! player.isSleeping())
                awakePlayers.add( uuid );
        }
        this.sleepers.removeAll( awakePlayers );

        if (this.sleepers.size() >= this.numNeeded) {
            this.timeChanger.tick(this.sleepers.size(), this.numNeeded);
        }
    }
}
