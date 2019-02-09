package be.dezijwegel.events;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SleepTracker {

    private Map<UUID, Long> sleepList;
    private Map<String, Integer> numSleeping;

    private boolean multiworld;
    private int bedEnterDelay;

    public SleepTracker(boolean multiworld, int bedEnterDelay)
    {
        sleepList = new HashMap<UUID, Long>();

        this.multiworld = multiworld;
        this.bedEnterDelay = bedEnterDelay;
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
            if (whenCanPlayerSleep(uuid) < 1)
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
            if(bedEnterDelay < currentTime - sleepList.get(uuid))
                return bedEnterDelay - (currentTime - sleepList.get(uuid));
            else return 0L;

        } else {
            return 0L;
        }
    }
}
