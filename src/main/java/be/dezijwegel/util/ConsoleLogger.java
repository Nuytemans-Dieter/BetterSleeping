package be.dezijwegel.util;

import jdk.internal.jline.internal.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class ConsoleLogger {


    private final boolean enableColors;


    /**
     * This instance can log messages to the console
     * @param enableColors whether console messages may be coloured or not
     */
    public ConsoleLogger(boolean enableColors)
    {
        this.enableColors = enableColors;
    }


    /**
     * Log a message to the console
     * @param message the message to be logged
     * @param color the color to be used, if colors are enabled. Passing a null value is allowed and the color will be ignored
     */
    public void log(String message, @Nullable ChatColor color)
    {
        String fullMessage = "[BetterSleeping]";
        if (enableColors && color != null)
            fullMessage += color;
        fullMessage += message;
        Bukkit.getLogger().info(fullMessage);
    }


    /**
     * Log a message to the console in its default color
     * @param message the message
     */
    public void log (String message)
    {
        this.log(message, null);
    }


}
