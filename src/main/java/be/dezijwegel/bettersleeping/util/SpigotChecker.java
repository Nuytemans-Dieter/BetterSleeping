package be.dezijwegel.bettersleeping.util;

public class SpigotChecker {
    
    private static Boolean HAS_SPIGOT = null;
    
    public static boolean hasSpigot() {
        if (HAS_SPIGOT == null)
        {
            try
                {
                    Class.forName("org.spigotmc.SpigotConfig");
                    HAS_SPIGOT = true;
                } catch (ClassNotFoundException ignored)
                {
                    HAS_SPIGOT = false;
                }
        }
        return HAS_SPIGOT;
    }
    
}
