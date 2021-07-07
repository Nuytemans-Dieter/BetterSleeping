package be.betterplugins.bettersleeping.commands;

import be.betterplugins.bettersleeping.model.SleepStatus;
import be.betterplugins.bettersleeping.model.sleeping.SleepWorldManager;
import be.betterplugins.core.commands.shortcuts.PlayerBPCommand;
import be.betterplugins.core.messaging.messenger.Messenger;
import be.betterplugins.core.messaging.messenger.MsgEntry;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoutCommand extends PlayerBPCommand
{

    private final long cooldownMillis = 60000;
    private final Map<World, Long> cooldownMap;
    private final SleepWorldManager sleepWorldManager;

    public ShoutCommand(Messenger messenger, SleepWorldManager sleepWorldManager)
    {
        super(messenger);

        this.cooldownMap = new HashMap<>();
        this.sleepWorldManager = sleepWorldManager;
    }

    @Override
    public @NotNull String getCommandName()
    {
        return "shout";
    }

    @Override
    public @NotNull List<String> getAliases()
    {
        return new ArrayList<>();
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull Command command, @NotNull String[] strings)
    {
        World world = player.getWorld();

        SleepStatus sleepStatus = sleepWorldManager.getSleepStatus( world );

        if (sleepStatus == null)
        {
            messenger.sendMessage(player, "world_disabled");
            return true;
        }

        long remainingCooldown = getRemainingCooldown( world );
        if ( remainingCooldown > 0 )
        {
            long seconds = remainingCooldown / 1000;
            messenger.sendMessage(player, "command_shout_cooldown", new MsgEntry("<time>", "" + seconds));
            return true;
        }

        cooldownMap.put( world, System.currentTimeMillis() );

        messenger.sendMessage(world.getPlayers(), "command_shout",
                new MsgEntry("<num_sleeping>", sleepStatus.getNumSleepers()),
                new MsgEntry("<needed_sleeping>", sleepStatus.getNumNeeded()),
                new MsgEntry("<remaining_sleeping>", sleepStatus.getNumMissing()));
        return true;
    }

    private long getRemainingCooldown(World world)
    {
        if (cooldownMap.containsKey( world ))
        {
            long delta = System.currentTimeMillis() - cooldownMap.get( world );
            return delta > cooldownMillis ? 0 : cooldownMillis - delta;
        }
        else return 0;
    }

    @Override
    public @NotNull String getPermission()
    {
        return "bettersleeping.shout";
    }
}
