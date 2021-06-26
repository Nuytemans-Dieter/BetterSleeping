package be.betterplugins.bettersleeping.model;

import org.bukkit.GameRule;
import org.bukkit.World;

public class WorldState
{

    private final boolean doDayLightCycle;

    /**
     * Capture the world state of a given world.
     * This state can be copied to any other world, or be used to make temporary state changes to the given world.
     *
     * @param world the world for which we want to capture the state.
     */
    public WorldState(final World world)
    {
        Boolean doTime = world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE);
        this.doDayLightCycle = doTime == null || doTime;
    }

    /**
     * Manually create a world state that can be applied to any world.
     *
     * @param doDayLightCycle the value to which DoDayLightCycle should be set in this state.
     */
    public WorldState(final boolean doDayLightCycle)
    {
        this.doDayLightCycle = doDayLightCycle;
    }

    /**
     * Update the given world to this state.
     *
     * @param world the world whose state is to be modified.
     */
    public void applyState(final World world)
    {
        world.setGameRule( GameRule.DO_DAYLIGHT_CYCLE, this.doDayLightCycle );
    }

}
