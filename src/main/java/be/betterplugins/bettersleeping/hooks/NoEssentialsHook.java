package be.betterplugins.bettersleeping.hooks;

import be.betterplugins.bettersleeping.model.ConfigContainer;
import be.betterplugins.core.messaging.logging.BPLogger;
import org.bukkit.entity.Player;

public class NoEssentialsHook extends EssentialsHook
{
    public NoEssentialsHook(ConfigContainer config, BPLogger logger)
    {
        super(config, logger);
    }

    @Override
    public boolean isHooked()
    {
        return false;
    }

    @Override
    public boolean isAfk(Player player)
    {
        return false;
    }

    @Override
    public boolean isVanished(Player player)
    {
        return false;
    }
}
