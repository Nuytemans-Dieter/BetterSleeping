package be.dezijwegel.bettersleeping.commands.bscommands;

import be.dezijwegel.bettersleeping.messaging.Messenger;
import be.dezijwegel.bettersleeping.messaging.MsgEntry;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoutCommand extends BsCommand{

    private final long cooldownMillis = 60000;
    private final Map<World, Long> cooldownMap;

    public ShoutCommand(Messenger messenger) {
        super(messenger);

        this.cooldownMap = new HashMap<>();
    }

    @Override
    public boolean execute(CommandSender commandSender, Command command, String alias, String[] arguments) {

        if (!(commandSender instanceof Player))
            return true;

        Player player = (Player) commandSender;
        World world = player.getWorld();

        long remainingCooldown = getRemainingCooldown( world );
        if ( remainingCooldown > 0 )
        {
            long seconds = remainingCooldown / 1000;
            messenger.sendMessage(player, "&cYou have to wait <time> more seconds before shouting in this world", true, new MsgEntry("<time>", "" + seconds));
            return true;
        }

        cooldownMap.put( world, System.currentTimeMillis() );
        messenger.sendMessage(world.getPlayers(), "Please go to bed, some players want to sleep.", false);
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
    public String getPermission() {
        return "bettersleeping.shout";
    }

    @Override
    public List<String> getDescription() {
        return new ArrayList<String>() {{
            add("Asks players to sleep");
            add("anonymous request!");
        }};
    }

    @Override
    public String getDescriptionAsString() {
        return "Requests all players in your world to go to bed anonymously.";
    }
}
