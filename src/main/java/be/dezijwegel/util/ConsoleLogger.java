package be.dezijwegel.util;

import be.dezijwegel.files.Console;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class ConsoleLogger {


    static private Console config;      // Store the config file object


    /**
     * Set the console config file
     * @param config the Console config object
     */
    public static void setConfig(Console config)
    {
        ConsoleLogger.config = config;
    }


    /**
     * Log plain text to the console, colors will be maintained but this is discouraged
     * Use logPositive() or logNegative() instead when using colors
     * [BetterSleeping] will be added automatically
     * @param message logged message
     */
    public static void logInfo(String message)
    {
        logColored(message, ChatColor.WHITE, false);
    }


    /**
     * Log a message to the console with the given color, IF useColor is true.
     * Otherwise, the message won't use the provided color
     * @param message message to be logged
     * @param color color of the message (only used if useColor is true)
     * @param useColor decide whether to color the message
     */
    private static void logColored(String message, ChatColor color, boolean useColor)
    {
        String fullMessage = "[BetterSleeping] ";

        if (useColor)
        {
            fullMessage += color;
        }

        fullMessage += message;

        Bukkit.getConsoleSender().sendMessage(fullMessage);
    }


    /**
     * Log a positive message, a success or similar to the console with the given color
     * @param message message to be logged
     * @param color color of the message
     */
    public static void logPositive(String message, ChatColor color)
    {
        logColored(message, color, config.isPositiveGreen());
    }


    /**
     * Log a negative message, a success or similar to the console with the given color
     * @param message message to be logged
     * @param color color of the message
     */
    public static void logNegative(String message, ChatColor color)
    {
        logColored(message, color, config.isNegativeRed());
    }
}
