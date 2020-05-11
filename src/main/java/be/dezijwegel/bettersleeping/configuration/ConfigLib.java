package be.dezijwegel.bettersleeping.configuration;

import be.dezijwegel.bettersleeping.messaging.ConsoleLogger;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ConfigLib {

    private final JavaPlugin plugin;
    private final ConsoleLogger logger;

    private File file;
    private FileConfiguration configuration;
    private FileConfiguration defaultConfig;

    private final String fileName;


    /**
     * Create a configLib instance for easy config reading / management
     * @param fileName the name of the configuration file
     * @param plugin instance of the plugin
     */
    public ConfigLib(boolean reportMissingOptions, @NotNull String fileName, @NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.logger = new ConsoleLogger(true);
        initialise(fileName, plugin, false, reportMissingOptions);
    }


    /**
     * Create a configLib instance for easy config reading / management
     * @param fileName the name of the configuration file
     * @param plugin instance of the plugin
     */
    public ConfigLib(@NotNull String fileName, @NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.logger = new ConsoleLogger(true);
        initialise(fileName, plugin, false, true);
    }


    /**
     * Create a configLib instance for easy config reading / management
     * @param fileName the name of the configuration file
     * @param plugin instance of the plugin
     * @param addMissingOptions whether or not to auto-add missing options to this file
     */
    public ConfigLib(@NotNull String fileName, @NotNull JavaPlugin plugin, boolean addMissingOptions) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.logger = new ConsoleLogger(true);
        initialise(fileName, plugin, addMissingOptions, true);
    }


    public void initialise(@NotNull String fileName, @NotNull JavaPlugin plugin, boolean addMissingOptions, boolean reportMissingOptions)
    {

        this.file = new File(plugin.getDataFolder(), fileName);
        this.configuration = YamlConfiguration.loadConfiguration(file);

        //Copy contents of internal config file
        Reader defaultStream = null;
        try {
            defaultStream = new InputStreamReader(Objects.requireNonNull(plugin.getResource(fileName), "[BetterSleeping] An internal file name '" + fileName + "' does not exist!"), StandardCharsets.UTF_8);
            this.defaultConfig = YamlConfiguration.loadConfiguration(defaultStream);
        } catch (Exception ignored) {}

        saveDefaultConfig();

        if (addMissingOptions)
            this.addMissingOptions();
        else if (reportMissingOptions)
            this.reportMissingOptions();

        if (!reportMissingOptions && !addMissingOptions)
            return;

        // Add missing options to the default config
        for (String path : defaultConfig.getKeys(true))
        {
            if ( ! configuration.contains(path) && ! defaultConfig.isConfigurationSection(path))
                configuration.set(path, defaultConfig.get(path));
        }
    }


    /**
     * Get the FileConfiguration with missing options set to their defaults
     * @return the FileConfiguration that contains all options
     */
    public FileConfiguration getConfiguration()
    {
        return configuration;
    }


    /**
     * This method will force the file back to its default
     * This is helpful when new options are added and comments are needed
     */
    public void forceDefaultConfig()
    {
        plugin.saveResource(fileName, true);
    }


    /**
     * Reload the config file
     */
    public void reloadFile() {
        if (configuration == null) {
            file = new File(plugin.getDataFolder(), fileName);
        }
        configuration = YamlConfiguration.loadConfiguration(file);

        // Look for defaults in the jar
        Reader defConfigStream = null;
        defConfigStream = new InputStreamReader(Objects.requireNonNull(plugin.getResource(fileName), "[BetterSleeping] An internal file name '" + fileName + "' does not exist!"), StandardCharsets.UTF_8);

        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        configuration.setDefaults(defConfig);
    }



    public void saveDefaultConfig() {
        if (!file.exists())
        {
            logger.log("Copying a new " + fileName + " ...", ChatColor.GREEN);
            plugin.saveResource(fileName, false);

            //Make sure the Configuration is up to date. Otherwise missing options may be reported!
            this.configuration = YamlConfiguration.loadConfiguration(file);
        }

        if (file == null)
        {
            file = new File(plugin.getDataFolder(), fileName);
        }
    }


    /**
     * Get all paths of missing options
     * @return a list of missing paths
     */
    public List<String> getMissingOptionPaths()
    {
        List<String> missingOptions = new ArrayList<>();

        Reader defConfigStream = new InputStreamReader(plugin.getResource(fileName), StandardCharsets.UTF_8);

        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        for (String path : defConfig.getKeys(true)) {
            if (!defConfig.isConfigurationSection(path)) {
                if (!configuration.contains(path)) {
                    missingOptions.add(path);
                }
            }
        }

        return missingOptions;
    }


    /**
     * Add all missing options to the config file, IF enabled
     * Will fail silently if disabled
     * Currently not in use, but functional
     */
    public void addMissingOptions()
    {
        List<String> missingOptions = getMissingOptionPaths();

        String missingMessage;
        if (missingOptions.size() == 0)
        {
            return;
        } else if (missingOptions.size() == 1)
        {
            missingMessage = "A missing option has been found in " + fileName + "!";
        } else {
            missingMessage = missingOptions.size() + " Missing options have been found in " + fileName + "!";
        }

        logger.log(missingMessage, ChatColor.DARK_RED);
        logger.log("Automagically adding missing options...", ChatColor.DARK_RED);

        // Set the defaults and report each setting to the console
        for (String path : missingOptions)
        {
            Object value = defaultConfig.get(path);     // Get the default value
            configuration.set(path, value);
            configuration.addDefault(path, value);

            // Report to console
            // Change formatting if String
            if (value instanceof String)
                value = "\"" + value + "\"";

            String newValue = "";
            if (value != null)
                newValue = value.toString();
            logger.log("Setting " + path + " to " + newValue, ChatColor.RED);
        }

        try {
            configuration.save(file);
        } catch (IOException ignored) {}
    }


    /**
     * Compare the default file with the one on the server and report every missing option
     */
    public void reportMissingOptions()
    {
        // Get the missing configuration options that are not configuration sections
        List<String> missingOptions = getMissingOptionPaths();


        if (missingOptions.size() > 0)
        {

            if (missingOptions.size() == 1)
                logger.log("A missing option has been found in " + fileName + "!", ChatColor.DARK_RED);
            else
                 logger.log(missingOptions.size() + " Missing options have been found in " + fileName + "!", ChatColor.DARK_RED);

            logger.log("Please add the missing option(s) manually or delete this file and perform a reload (/bs reload)", ChatColor.DARK_RED);
            logger.log("The default values will be used until then", ChatColor.DARK_RED);

            ArrayList<String> currentPath = new ArrayList<>();
            currentPath.add("");
            for (String path : missingOptions)
            {
                // Handle value
                Object value = defaultConfig.get(path);


                // Change formatting if the setting is a String
                if (value instanceof String)
                    value = "\"" + value + "\"";


                // Handle path
                String[] sections = path.split("\\.");   // Split the path in its sections
                path = "";                                      // Reset the path variable


                // Remove part of the path that is a deeper level than this setting can possibly be
                if (currentPath.size() > sections.length)
                {
                    currentPath.subList(sections.length, currentPath.size()).clear();
                }


                // Handle subsection logging and indentation
                int index = 0;                                  // Index of the current path level
                StringBuilder indentation = new StringBuilder();                        // Indentation for the current level
                for (String section : sections)
                {
                    if (index+1 != sections.length) {           // Prevents the option name itself to be viewed as part of path
                        if (index > currentPath.size() || !currentPath.get(index).equals(section))  // If the current option is in a different subsection
                        {
                            // Set the path change
                            if (index > currentPath.size())     // If index does NOT exist
                            {
                                // Add to the end of list
                                currentPath.add(section);
                            } else {                            // If index DOES exist
                                // Replace the element at index
                                currentPath.set(index, section);
                                // Remove deprecated sub-branches
                                currentPath.subList(index, currentPath.size()).clear();
                            }

                            // Print path change
                            String message = indentation + "In subsection: '" + section + "'";
                            logger.log(message, ChatColor.DARK_RED);
                        }

                        indentation.append("  ");
                        index++;
                    }
                }

                path += "'" + sections[sections.length-1] + "'";                 // Get the actual setting name

                // Print the missing option with its default value
                String message = indentation + "Missing option: " + path + " with default value: " + value;
                logger.log(message, ChatColor.RED);
            }
        } else {
            logger.log("No missing options were found in " + fileName + "!");
        }
    }
}