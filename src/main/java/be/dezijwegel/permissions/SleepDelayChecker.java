package be.dezijwegel.permissions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SleepDelayChecker {


    private final Map<UUID, Long> sleepList;    // Keeps track of when a player went last to sleep
    private final int bedEnterDelay;           // The delay between bed enter attempts


    /**
     * Keeps track of when players last entered their bed
     * @param bedEnterDelay the delay in seconds before a player can enter their bed again
     */
    public SleepDelayChecker(int bedEnterDelay)
    {
        this.bedEnterDelay = bedEnterDelay;

        sleepList = new HashMap<>();
    }


    /**
     * Check in how many seconds a player can sleep again
     * Will be 0 when the player is allowed to sleep right now
     * @param uuid the UUID of the player
     * @return the minimum waiting delay
     */
    public long whenCanPlayerSleep(UUID uuid)
    {
        if ( ! sleepList.containsKey(uuid))
            return 0L;

        long currentTime = System.currentTimeMillis() / 1000L;
        long deltaTime = currentTime - sleepList.get(uuid);

        if(deltaTime < bedEnterDelay) {
            return (long) bedEnterDelay - deltaTime;
        }

        return 0L;
    }


    /**
     * Mark a player that entered their bed
     * This will reset their waiting delay
     * @param uuid the uuid of the player
     */
    public void bedEnterEvent(UUID uuid)
    {
        long currentTime = System.currentTimeMillis() / 1000L;
        sleepList.put(uuid, currentTime);
    }


    /**
     * Mark a player as logged out
     * Removes the player from the Map
     * @param uuid uuid of the player
     */
    public void logOutEvent(UUID uuid)
    {
        sleepList.remove(uuid);
    }
}
