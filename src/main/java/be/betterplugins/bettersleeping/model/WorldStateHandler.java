package be.betterplugins.bettersleeping.model;

import org.bukkit.World;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldStateHandler
{

    private final Map<World, WorldState> worldWorldStateMap;

    @Inject
    public WorldStateHandler(List<World> worlds)
    {
        this.worldWorldStateMap = new HashMap<>();
        for (World world : worlds)
        {
            this.worldWorldStateMap.put(world, new WorldState( world ));
        }
    }

    /**
     * Make temporary world state changes to all worlds
     */
    public void setWorldStates(WorldState state)
    {
        for (World world : worldWorldStateMap.keySet())
        {
            state.applyState( world );
        }
    }

    /**
     * Revert all temporary changes to their original value
     */
    public void revertWorldStates()
    {
        for (Map.Entry<World, WorldState> entry : worldWorldStateMap.entrySet())
        {
            entry.getValue().applyState( entry.getKey() );
        }
    }

}
