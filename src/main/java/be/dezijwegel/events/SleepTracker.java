package be.dezijwegel.events;

import be.dezijwegel.management.Management;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class SleepTracker {

    Plugin plugin;

    private Map<UUID, Long> sleepList;          // Keeps track of when a player went last to sleep
    private Map<World, Integer> numSleeping;    // Keeps track of the number of sleeping players in each world
    private Map<World, Long> lastSetToDay;      // Keeps track when the time in each world was last set to day

    private Management management;
    private DisableSkipTracker disableSkipTracker;
    private Essentials essentials = null;

    private boolean isEssentialsHooked;
    private int bedEnterDelay;
    private int percentageNeeded;

    public SleepTracker(Plugin plugin, Management management)
    {
        this.plugin = plugin;

        isEssentialsHooked = Bukkit.getPluginManager().isPluginEnabled("Essentials");
        if (isEssentialsHooked) {
            essentials = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");

            String message = "Successfully hooked into Essentials!";

            if (management.getConsoleConfig().isPositiveGreen())
                Bukkit.getConsoleSender().sendMessage("[BetterSleeping] " + ChatColor.GREEN + message);
            else
                Bukkit.getConsoleSender().sendMessage("[BetterSleeping] " + message);

        } else {
            Bukkit.getConsoleSender().sendMessage("[BetterSleeping] Essentials was not found on this server!");
        }

        sleepList = new HashMap<UUID, Long>();
        numSleeping = new HashMap<World, Integer>();
        lastSetToDay = new HashMap<World, Long>();

        this.management = management;
        boolean enableSkip = management.getBooleanSetting("enable_no_skip_night_command");
        int durationSkip = management.getIntegerSetting("disable_skip_time");
        this.disableSkipTracker = new DisableSkipTracker(plugin, management, this, enableSkip, durationSkip);

        this.bedEnterDelay = management.getIntegerSetting("bed_enter_delay");
        this.percentageNeeded = management.getIntegerSetting("percentage_needed");
        if (percentageNeeded > 100) percentageNeeded = 100;
        else if (percentageNeeded < 0) percentageNeeded = 0;
    }


    /**
     * Get how many seconds ago the time was set to day
     * @param world
     * @return
     */
    public int getTimeSinceLastSetToDay(World world)
    {
        if (lastSetToDay.containsKey(world)) {
            return (int) ( System.currentTimeMillis() / 1000 - lastSetToDay.get(world) );
        } else return 3600;
    }


    /**
     * Add the current time to the list of lastSetToDay
     * @param world
     */
    public void worldWasSetToDay(World world)
    {
        lastSetToDay.put(world, System.currentTimeMillis()/1000);
    }


    /**
     * Check whether or not a Player (by uuid) must wait a while before they can sleep (again)
     * @param uuid
     * @return
     */
    public boolean checkPlayerSleepDelay (UUID uuid)
    {
        long currentTime = System.currentTimeMillis() / 1000L;
        if (sleepList.containsKey(uuid))
        {
            if (whenCanPlayerSleep(uuid) == 0)
            {
                sleepList.put(uuid, currentTime);
                return true;
            } else {
                return false;
            }
        } else {
            sleepList.put(uuid, currentTime);
            return true;
        }
    }


    /**
     * Gets the time (seconds) a Player must wait before they can sleep again
     * @param uuid
     * @return
     */
    public long whenCanPlayerSleep(UUID uuid)
    {
        if (sleepList.containsKey(uuid))
        {
            long currentTime = System.currentTimeMillis() / 1000L;
            long deltaTime = currentTime - sleepList.get(uuid);
            if(deltaTime < bedEnterDelay) {
                long temp = bedEnterDelay - deltaTime;
                return temp;
            }else return 0L;

        } else {
            return 0L;
        }
    }


    /**
     * Get the number of players that should be sleeping for the night to be skipped
     * This method also considers the 'multiworld_support' setting in config.yml
     * @param world
     * @return
     */
    public int getTotalSleepersNeeded(World world)
    {

        int numPlayers = 0;
            for (Player player : Bukkit.getOnlinePlayers())
            {
                if (player.getWorld().getEnvironment() == World.Environment.NORMAL) {   // Don't count players in Nether or The End
                    if (player.getWorld().equals(world)) {                              // If the player is in the right world
                        if (!isAfk(player) && !isPlayerBypassed(player))                // If player is not afk and not a bypassed sleeper
                            numPlayers++;
                    }
                }
            }

        int numNeeded = (int) Math.ceil((double)(percentageNeeded * numPlayers) / 100);
        if (numNeeded > 1) return numNeeded;
        else return 1;
    }


    /**
     * Check whether or not the given player should be counted towards needed sleeping players
     * @param player
     * @return
     */
    private boolean isAfk( Player player )
    {
        boolean isAfk = false;

        if (isEssentialsHooked && management.getBooleanSetting("essentials_afk_support"))
        {
            User user = essentials.getUser( player );
            if (user.isAfk()) isAfk = true;
        }

        return isAfk;
    }


    /**
     * Get the number of relevant players that are currently sleeping
     * The 'multiworld_support' option will be considered
     * @param world
     * @return
     */
    public int getNumSleepingPlayers(World world)
    {
        int numSleeping;
        if (this.numSleeping.get(world) == null)
            numSleeping = 0;
        else
            numSleeping = this.numSleeping.get(world);

        return numSleeping;
    }


    /**
     * Add a player to the sleeping list
     * @param world
     */
    public void addSleepingPlayer(World world)
    {
        if (numSleeping.containsKey(world))
        {
            int num = numSleeping.get(world) + 1;
            numSleeping.put(world, num);
        } else
        {
            numSleeping.put(world, 1);
        }
    }


    /**
     * Remove a player from the sleeping list
     * @param world
     */
    public void removeSleepingPlayer(World world)
    {
        if (numSleeping.containsKey(world))
        {
            int num = numSleeping.get(world) -1;

            if (num < 0) num = 0;

            numSleeping.put(world, num);
        } else
        {
            numSleeping.put(world, 0);
        }
    }


    /**
     * Get a list of players that are relevant to the given world
     * @param world
     * @return
     */
    public List<Player> getRelevantPlayers(World world)
    {
        List<Player> list = new LinkedList<Player>();
        for (Player player : Bukkit.getOnlinePlayers())
            if (player.getLocation().getWorld().equals(world)) list.add(player);

        return list;
    }


    /**
     * Check if a Player meets the requirements to sleep
     * Also checks if the World meets the requirements
     * Sends messages to players if needed
     * @return
     */
    public boolean playerMaySleep(Player player)
    {
        World worldObj = player.getWorld();
        if (worldObj.getTime() > 12500 || worldObj.hasStorm() || worldObj.isThundering()) {

            UUID uuid = player.getUniqueId();

            if ( isPlayerBypassed(player) )
            {
                management.sendMessage("bypass_message", player);
                return false;
            }
            if (checkPlayerSleepDelay(uuid))
                return true;
            else {
                Map<String, String> replace = new LinkedHashMap<String, String>();
                long time = whenCanPlayerSleep(player.getUniqueId());
                replace.put("<time>", Long.toString(time));
                boolean isSingular;
                if (time == 1) isSingular = true; else isSingular = false;
                management.sendMessage("sleep_spam", player, replace, isSingular);
                return false;
            }
        }
        return false;
    }


    /**
     * Check if the player has any sleeping bypass permissions
     * @param player
     * @return
     */
    public boolean isPlayerBypassed(Player player)
    {
        // Permission based
        if ( management.getBooleanSetting("enable_bypass_permissions") )
        {
            if (isEssentialsHooked && player.hasPermission("essentials.sleepingignored"))   return true;
            if (player.hasPermission("bettersleeping.bypass"))                              return true;
        }

        // Gamemode based bypassing
        boolean ignoreCreative = management.getBooleanSetting("ignore_creative");
        if (ignoreCreative && player.getGameMode() == GameMode.CREATIVE) return true;

        boolean ignoreSpectator = management.getBooleanSetting("ignore_spectator");
        if (ignoreSpectator && player.getGameMode() == GameMode.SPECTATOR) return true;

        boolean ignoreAdventure = management.getBooleanSetting("ignore_adventure");
        if (ignoreAdventure && player.getGameMode() == GameMode.ADVENTURE) return true;

        boolean ignoreSurvival = management.getBooleanSetting("ignore_survival");
        if (ignoreSurvival && player.getGameMode() == GameMode.SURVIVAL) return true;

        boolean ignoreVanished = management.getBooleanSetting("ignore_vanished_players");
        if (ignoreVanished && isPlayerHidden(player)) return true;

        // Otherwise it is not a bypassed player
        return false;
    }


    /**
     * Check if a player is hidden by another plugin
     * Supports: Essentials, Essentials-compatible vanish plugins, PremiumVanish, SuperVanish, VanishNoPacket and more!
     * @param player
     * @return
     */
    private boolean isPlayerHidden(Player player)
    {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }

        if (isEssentialsHooked)
        {
            User user = essentials.getUser( player );
            if (user.isHidden())
                return true;
        }

        return false;
    }

    /**
     * Get the DisableSkipTracker to track disabled worlds
     * @return DisableSkipTracker
     */
    public DisableSkipTracker getDisableSkipTracker()
    {
        return disableSkipTracker;
    }


    /**
     * Stop keeping track of when the given Player last slept
     * @param player
     */
    public void playerLogout(Player player)
    {
        if (sleepList.containsKey(player.getUniqueId()))
            sleepList.remove(player.getUniqueId());
    }
}
