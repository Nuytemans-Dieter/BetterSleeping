package be.betterplugins.bettersleeping.util;

import be.betterplugins.core.messaging.logging.BPLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

public class FileLogger extends BPLogger
{

    private final FileWriter writer;
    private Level fileLogLevel;


    /**
     * Provides the same functionality as BPlogger but will ALSO log to a file (all messages, even finest)
     * Different logging levels that we use:
     * During development: ALL
     * During beta testing: FINE
     * During live release: INFO
     *
     * @param logLevel the minimum logging level for messages to be shown
     * @param plugin the plugin in whose folder the logs should appear (/plugins/GIVEN_PLUGIN/logs)
     */
    public FileLogger(Level logLevel, JavaPlugin plugin) throws IOException
    {
        this(logLevel, plugin, "BetterPlugin");
    }

    /**
     * Enable logging for this logger
     * Different logging levels that we use:
     * During development: ALL
     * During beta testing: FINE
     * During live release: INFO
     *
     * @param logLevel the minimum logging level for messages to be shown
     * @param plugin the plugin in whose folder the logs should appear (/plugins/GIVEN_PLUGIN/logs)
     * @param pluginName the name of your plugin - will be displayed in all logs
     */
    public FileLogger(Level logLevel, JavaPlugin plugin, String pluginName) throws IOException
    {
        super(logLevel, pluginName);

        this.fileLogLevel = Level.ALL;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmssSSS");
        LocalDateTime now = LocalDateTime.now();
        String logName = dtf.format(now);

        String filename = plugin.getDataFolder().getAbsolutePath() + File.separator + "logs" + File.separator + logName + ".txt";
        this.writer = new FileWriter(filename);
    }

    /**
     * Change the file logging level & return the BPFileLogger object
     *
     * @param logLevel the minimum log level before a message is logged to the log
     * @return uses the builder pattern
     */
    public BPLogger setFileLogLevel(Level logLevel)
    {
        this.fileLogLevel = logLevel;
        return this;
    }

    public void close()
    {
        try
        {
            this.writer.close();
        }
        catch (IOException e)
        {
            super.log(Level.WARNING, "Something went wrong while trying to close the log writer");
            e.printStackTrace();
        }
    }

    @Override
    public void log(Level level, String message)
    {
        // Also log to console
        super.log(level, message);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");
        LocalDateTime now = LocalDateTime.now();
        String timeStamp = dtf.format(now);

        String logMessage = "[" + timeStamp + "] [" + level.toString() + "]: " + message;
        if (level.intValue() >= this.fileLogLevel.intValue())
        {
            try
            {
                this.writer.write( logMessage + "\n" );
            }
            catch (IOException e)
            {
                super.log(Level.WARNING, "An error occurred while trying to save a log message");
            }
        }
    }
}
