package be.dezijwegel.files;

import be.dezijwegel.util.ConsoleLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class ConfigAPI {

    private FileConfiguration configuration;
    private File file;
    private JavaPlugin plugin;

    String fileName = "";

    private FileConfiguration defaultConfig;

    public enum FileType {
        CONFIG,
        LANG,
        BUFFS,
        CONSOLE,
        EVENTS
    }

    /**
     * Create a configAPI instance
     * @param type the type of config file
     * @param addMissingOptions choose whether or not to add missing options (if this setting is enabled)
     * @param plugin instance of the plugin
     */
    public ConfigAPI(FileType type, boolean addMissingOptions, JavaPlugin plugin) {
        this.plugin = plugin;

        switch (type)
        {
            case CONFIG :
                fileName = "config.yml";
                break;
            case LANG:
                fileName = "lang.yml";
                break;
            case BUFFS:
                fileName = "buffs.yml";
                break;
            case CONSOLE:
                fileName = "console.yml";
                break;
            case EVENTS:
                fileName = "events.yml";
                break;
        }

        this.file = new File(plugin.getDataFolder(), fileName);
        this.configuration = YamlConfiguration.loadConfiguration(file);

        //Copy contents of internal config file
        Reader defaultStream = null;
        try {
            defaultStream = new InputStreamReader(plugin.getResource(fileName), "UTF8");
            if (defaultStream != null) {
                this.defaultConfig = YamlConfiguration.loadConfiguration(defaultStream);
            }
        } catch (Exception ex) {}

        saveDefaultConfig();

        if (addMissingOptions)
        {
            addMissingOptions();
        }
    }

    /**
     * Get an Object from a file
     * @param path
     * @return Object
     */
    public Object get(String path) {
        if (configuration.contains(path))
            return configuration.get(path);
        else return defaultConfig.get(path);
    }

    /**
     * Get the default Object from file in jar
     * @param path
     * @return
     */
    @Deprecated
    public Object getDefault(String path) {return defaultConfig.get(path); }

    /**
     * Get a String from a file
     * @param path
     * @return String
     */
    public String getString(String path) {

        String string;

        if (configuration.contains(path))
            string = configuration.getString(path);
        else string = defaultConfig.getString(path);

        if (string != null)
            if (string.contains("&")) string = string.replaceAll("&", "§");

        return string;
    }

    /**
     * Get an integer from a file
     * @param path
     * @return int
     */
    public int getInt(String path)
    {
        if (configuration.contains(path))
            return configuration.getInt(path);
        else return defaultConfig.getInt(path);
    }

    /**
     * Get a boolean from a file
     * @param path
     * @return boolean
     */
    public boolean getBoolean(String path)
    {
        if (configuration.contains(path))
            return configuration.getBoolean(path);
        else return defaultConfig.getBoolean(path);
    }

    /**
     * Get a long from a file
     * @param path
     * @return
     */
    public long getLong(String path)
    {
        if (configuration.contains(path))
            return configuration.getLong(path);
        return defaultConfig.getLong(path);
    }

    /**
     * Check if the file contains a specific path
     * Does not check the default configuration file
     * @param path
     * @return
     */
    public boolean contains(String path)
    {
        return configuration.contains(path);
    }

    /**
     * Check if the file contains a specific path
     * And choose to ignore the default file
     * @param path
     * @return
     */
    public boolean containsIgnoreDefault(String path) { return configuration.contains(path, true); }

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
        try {
                defConfigStream = new InputStreamReader(plugin.getResource(fileName), "UTF8");
        } catch (UnsupportedEncodingException ex) {}

        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            configuration.setDefaults(defConfig);
        }
    }

    public void saveDefaultConfig() {
        if (!file.exists())
        {
            ConsoleLogger.logPositive("Copying a new " + fileName + " ...", ChatColor.GREEN);
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
     * Gets the ConfigurationSection that equals the path
     * @param path
     */
    public ConfigurationSection getConfigurationSection(String path)
    {
        return configuration.getConfigurationSection(path);
    }

    public List<String> getMissingOptionPaths()
    {
        List<String> missingOptions = new ArrayList<>();

        Reader defConfigStream = null;
        defConfigStream = new InputStreamReader(Objects.requireNonNull(plugin.getResource(fileName)), StandardCharsets.UTF_8);

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
     */
    public void addMissingOptions()
    {
        // Stop execution if auto adding is disabled
        ConfigAPI config = new ConfigAPI(FileType.CONFIG, false, plugin);
        boolean autoAdd = config.getBoolean("auto_add_missing_options");
        if (!autoAdd)
        {
            return;
        }

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

        ConsoleLogger.logNegative(missingMessage, ChatColor.DARK_RED);
        ConsoleLogger.logNegative("Automagically adding missing options...", ChatColor.DARK_RED);

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
            ConsoleLogger.logNegative("Setting " + path + " to " + newValue, ChatColor.RED);
        }

        try {
            configuration.save(file);
        } catch (IOException e) {}
    }


    /**
     * Compare the default file with the one on the server and report every missing option
     */
    public void reportMissingOptions()
    {
        // Get the missing configuration options that are not configuration sections
        List<String> missingOptions = getMissingOptionPaths();

        // Report to console
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        Console consoleConfig = new Console(plugin);

        // Stop execution if settings are added automatically
        ConfigAPI config = new ConfigAPI(FileType.CONFIG, false, plugin);
        boolean autoAdd = config.getBoolean("auto_add_missing_options");
        if (autoAdd)
        {
            return;
        }

        if (missingOptions.size() > 0)
        {
            String message1 = "A missing option has been found in " + fileName + "!";
            String message2 = missingOptions.size() + " Missing options have been found in " + fileName + "!";
            String message3 = "Please add the missing option(s) manually or delete this file and perform a reload (/bs reload)";
            String message4 = "The default values will be used until then";

            if (consoleConfig.isNegativeRed()) {
                if (missingOptions.size() == 1)
                    console.sendMessage("[BetterSleeping] " + ChatColor.RED + message1);
                else console.sendMessage("[BetterSleeping] " + ChatColor.RED + message2);

                console.sendMessage("[BetterSleeping] " + ChatColor.RED + message3);
                console.sendMessage("[BetterSleeping] " + ChatColor.RED + message4);
            } else {
                if (missingOptions.size() == 1)
                    console.sendMessage("[BetterSleeping] " + message1);
                else console.sendMessage("[BetterSleeping] " + message2);

                console.sendMessage("[BetterSleeping] " + message3);
                console.sendMessage("[BetterSleeping] " + message4);
            }

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
                String indentation = "";                        // Indentation for the current level
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
                            String message = indentation + "In subsection: \'" + section + "\'";
                            if (consoleConfig.isNegativeRed())
                                console.sendMessage("[BetterSleeping] " + ChatColor.DARK_RED + message);
                            else
                                console.sendMessage("[BetterSleeping] " + message);
                        }

                        indentation += "  ";
                        index++;
                    }
                }

                path += "'" + sections[0] + "'";                 // Get the actual setting name

                // Print the missing option with its defaul value
                String message = indentation + "Missing option: " + path + " with default value: " + value;
                if (consoleConfig.isNegativeRed())
                    console.sendMessage("[BetterSleeping] " + ChatColor.DARK_RED + message);
                else
                    console.sendMessage("[BetterSleeping] " + message);
            }
        } else {

            String message = "No missing options were found in " + fileName + "!";

            if (consoleConfig.isPositiveGreen())
                console.sendMessage("[BetterSleeping] " + ChatColor.GREEN + message);
            else
                console.sendMessage("[BetterSleeping] " + message);
        }
    }
}