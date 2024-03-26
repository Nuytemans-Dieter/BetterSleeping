package be.betterplugins.bettersleeping.listeners;

import be.betterplugins.bettersleeping.api.BecomeDayEvent;
import be.betterplugins.bettersleeping.model.ConfigContainer;
import be.betterplugins.bettersleeping.model.permissions.BypassChecker;
import be.betterplugins.core.messaging.logging.BPLogger;
import be.betterplugins.core.messaging.messenger.Messenger;
import be.betterplugins.core.messaging.messenger.MsgEntry;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.logging.Level;

@Singleton
public class BuffsHandler implements Listener {


    private final BPLogger logger;
    private final Messenger messenger;
    private final BypassChecker bypassChecker;

    private final Set<PotionEffect> sleepingBuffs;
    private final Set<PotionEffect> sleepingDebuffs;

    private final List<String> sleepingCommands;
    private final List<String> nonSleepingCommands;

    private final HashMap<UUID,Integer> lastDisconnects;
    private int timesSlept = 0;


    /**
     * Event handler for {@link be.betterplugins.bettersleeping.api.BecomeDayEvent}
     */
    @Inject
    public BuffsHandler(BPLogger logger, Messenger messenger, BypassChecker bypassChecker, ConfigContainer config)
    {
        this.logger = logger;
        this.messenger = messenger;
        this.bypassChecker = bypassChecker;

        YamlConfiguration buffsConfig = config.getBuffs();
        this.sleepingBuffs   = readPotions(buffsConfig, "sleeper_buffs");
        this.sleepingDebuffs = readPotions(buffsConfig, "non_sleeper_debuffs");

        this.sleepingCommands    = buffsConfig.getStringList( "sleeper_commands" );
        this.nonSleepingCommands = buffsConfig.getStringList( "non_sleeper_commands" );

        this.lastDisconnects = new HashMap<>();
    }


    public Set<PotionEffect> getBuffs()
    {
        return sleepingBuffs;
    }

    public Set<PotionEffect> getDebuffs()
    {
        return sleepingDebuffs;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        this.lastDisconnects.put(event.getPlayer().getUniqueId(),this.timesSlept);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        // apply debuffs if needed
        // check if in map
        if(!this.lastDisconnects.containsKey(event.getPlayer().getUniqueId()))
            return;

        // check if player joined the next day
        if(this.timesSlept == 1 + this.lastDisconnects.get(event.getPlayer().getUniqueId())){
            // apply debuffs
            if (bypassChecker.isPlayerBypassed( event.getPlayer()))
                return;

            giveEffects(Collections.singletonList(event.getPlayer()),sleepingDebuffs,nonSleepingCommands);
        }
    }


    @EventHandler
    public void onSetToDay(BecomeDayEvent event)
    {
        // Only handle buffs if players (possibly) slept
        if (event.getCause() == BecomeDayEvent.Cause.OTHER)
            return;

        timesSlept++;

        if (sleepingBuffs.size() > 0)
        {
            messenger.sendMessage(
                    event.getPlayersWhoSlept(),
                    "buff_received",
                    new MsgEntry("<var>", "" + sleepingBuffs.size())
            );
            giveEffects(event.getPlayersWhoSlept(), sleepingBuffs, sleepingCommands);
        }

        // TODO Apply debuffs to players that tried to disconnect to avoid sleeping
        // A player is thought to disconnect to avoid sleeping if he does this:
        // He disconnected before sleeping and returned at the next day (timesSlept + 1)
        if (sleepingDebuffs.size() > 0)
        {
            List<Player> nonSleepers = new ArrayList<>();
            for (Player player : event.getPlayersWhoDidNotSleep())
            {
                if ( ! bypassChecker.isPlayerBypassed( player ))
                    nonSleepers.add( player );
            }

            messenger.sendMessage(
                    nonSleepers,
                    "debuff_received",
                    new MsgEntry("<var>", "" + sleepingDebuffs.size())
            );
            giveEffects(nonSleepers, sleepingDebuffs, nonSleepingCommands);
        }
    }


    private void giveEffects(List<Player> players, Set<PotionEffect> effects, List<String> commands)
    {
        for (Player player : players)
        {
            // Execute each command
            for (String command : commands)
            {
                command = command.replace("<user>", player.getName());
                Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), command );
            }

            // Add (de)buffs
            player.addPotionEffects(effects);
        }
    }


    /**
     * Read all {@link PotionEffect}s in a configuration section
     * @param config the config file to be read
     * @param section the section to be searched for the BetterSleeping potion effect format
     * @return A set of all valid potions in this section
     */
    private Set<PotionEffect> readPotions(FileConfiguration config, String section)
    {
        Set<PotionEffect> potions = new HashSet<>();

        // Prevent reading faulty config
        if ( ! config.isConfigurationSection(section) )
            return potions;

        // Only read effects when the config section exists
        ConfigurationSection configSection = config.getConfigurationSection(section);
        if (configSection != null)
        {
            for (String path : configSection.getKeys(false)) {
                int time = config.getInt(section + "." + path + ".time");
                int level = config.getInt(section + "." + path + ".level");

                PotionEffectType type = PotionEffectType.getByName(path.toUpperCase());


                // Only add if all fields are valid
                if (type != null && time > 0 && level > 0)
                    potions.add(new PotionEffect(type, 20 * time, level - 1));
                else
                    logger.log(Level.CONFIG, "Faulty (de)buff: '" + path + "' of duration '" + time + "' and level '" + level + "'");
            }
        }

        return potions;
    }
}
