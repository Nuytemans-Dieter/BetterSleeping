package be.dezijwegel.bettersleeping.util;

import org.bukkit.Bukkit;

public class Debugger {

    public enum DebugLevel {
        OFF,
        INFORMATIVE,
        DETAILED,
    }

    static final DebugLevel DEBUG_LEVEL = DebugLevel.OFF;
    static final int DEBUG_LEVEL_VALUE = Debugger.DEBUG_LEVEL.ordinal();

    public void debug(String message, DebugLevel level)
    {
        if ( level.ordinal() <= Debugger.DEBUG_LEVEL_VALUE && level != DebugLevel.OFF )
            Bukkit.getLogger().info( "[BS3 Debug] " + level.toString().toLowerCase() + ": " + message);
    }

}
