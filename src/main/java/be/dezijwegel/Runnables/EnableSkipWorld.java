package be.dezijwegel.Runnables;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class EnableSkipWorld extends BukkitRunnable {

    private ArrayList<World> disabledWorlds;
    private World enableWorld;

    /**
     * Will enable the given world after the time has ran out
     * @param disabledWorlds
     * @param enable
     */
    public EnableSkipWorld(ArrayList<World> disabledWorlds, World enable)
    {
        this.disabledWorlds = disabledWorlds;
        this.enableWorld = enable;
    }

    @Override
    public void run() {
        disabledWorlds.remove( enableWorld );
    }
}
