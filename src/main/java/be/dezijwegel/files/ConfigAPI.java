package be.dezijwegel.files;

import be.dezijwegel.bettersleeping.BetterSleeping;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConfigAPI {

    private FileConfiguration configuration;
    private File file;
    public FileType type;
    private BetterSleeping plugin;

    public enum FileType {
        CONFIG,
        LANG
    }

    /**
     * @param type of file
     * @param plugin
     */
    public ConfigAPI(FileType type, BetterSleeping plugin) {
        this.plugin = plugin;

        switch (type) {
            case CONFIG:
                this.file = new File(plugin.getDataFolder(), "config.yml");
                this.configuration = YamlConfiguration.loadConfiguration(file);
                this.type = type;
                break;
            case LANG:
                this.file = new File(plugin.getDataFolder(), "lang.yml");
                this.configuration = YamlConfiguration.loadConfiguration(file);
                this.type = type;
                break;
        }

    }

    /**
     * Get an Object from a file
     * @param path
     * @return Object
     */
    public Object get(String path) {
        return configuration.get(path);
    }

    /**
     * Get a String from a file
     * @param path
     * @return String
     */
    public String getString(String path) {
        String string = configuration.getString(path);
        if (string != null)
        {
            if (string.contains("&")) string = string.replaceAll("&", "§");
        }
        return string;
    }

    /**
     * Get an integer from a file
     * @param path
     * @return int
     */
    public int getInt(String path)
    {
        return configuration.getInt(path);
    }

    /**
     * Get a boolean from a file
     * @param path
     * @return boolean
     */
    public boolean getBoolean(String path)
    {
        return configuration.getBoolean(path);
    }

    /**
     * Get a long from a file
     * @param path
     * @return
     */
    public long getLong(String path)
    {
        return configuration.getLong(path);
    }

    /**
     * Check if the file contains a specific path
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
        switch (type) {
            case CONFIG:
                plugin.saveResource("config.yml", true);
                break;
            case LANG:
                plugin.saveResource("lang.yml", true);
                break;
        }
    }

    /**
     * Reload the config file
     */
    public void reloadFile() {
        if (configuration == null) {
            switch (type) {
                case CONFIG:
                    file = new File(plugin.getDataFolder(), "config.yml");
                    break;
                case LANG:
                    file = new File(plugin.getDataFolder(), "lang.yml");
                    break;
            }
        }
        configuration = YamlConfiguration.loadConfiguration(file);

        // Look for defaults in the jar
        Reader defConfigStream = null;
        try {
            switch (type) {
                case CONFIG:
                    defConfigStream = new InputStreamReader(plugin.getResource("config.yml"), "UTF8");
                    break;
                case LANG:
                    defConfigStream = new InputStreamReader(plugin.getResource("lang.yml"), "UTF8");
                    break;
            }
        } catch (UnsupportedEncodingException ex) {}

        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            configuration.setDefaults(defConfig);
        }
    }

    public void saveDefaultConfig() {
        if (file == null) {
            switch (type) {
                case CONFIG:
                    file = new File(plugin.getDataFolder(), "config.yml");
                    break;
                case LANG:
                    file = new File(plugin.getDataFolder(), "lang.yml");
                    break;
            }
        }
        if (!file.exists()) {
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            switch (type) {
                case CONFIG:
                    console.sendMessage("[BetterSleeping] " + ChatColor.GREEN + "Copying a new configuration file...");
                    plugin.saveResource("config.yml", false);
                    break;
                case LANG:
                    console.sendMessage("[BetterSleeping] " + ChatColor.GREEN + "Copying a new language file...");
                    plugin.saveResource("lang.yml", false);
                    break;
            }
        }
    }

    /**
     * Load all values that are an instance of a given type into the given list
     * @param type
     * @param map
     */
    public void loadTypesFromFile(Class type, Map<String, Object> map)
    {
        File file = null;
        switch (this.type)
        {
            case CONFIG:
                file = new File("config.yml");
                break;
            case LANG:
                file = new File("lang.yml");
                break;
        }
        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(file);

        for (String path : configuration.getKeys(true))
        {
            boolean found = false;

            if (!configuration.isConfigurationSection(path)) {
                if (configuration.contains(path)) {
                    if (configuration.get(path) != null) {
                        if (type.isInstance(configuration.get(path))) {
                            map.put(path, configuration.get(path));
                            found = true;

                            //Give the console messages if needed (regarding configuration options)
                            performPathCheck(path);
                        }
                    }
                }
            }

            if (!found) {
                if (!defaultConfig.isConfigurationSection(path)) {
                    if (defaultConfig.contains(path)) {
                        if (defaultConfig.get(path) != null) {
                            if (type.isInstance(defaultConfig.get(path))) {
                                ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                                map.put(path, defaultConfig.get(path));
                                console.sendMessage("[BetterSleeping] " + Color.RED + "A missing config option (" + path + ") has been found in " + getConfigName() + ". Now using default value: " + defaultConfig.get(path));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks a path and send specific messages to the console regarding to the configuration
     * @param path
     */
    public void performPathCheck(String path)
    {
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        if (type == FileType.CONFIG) {
            if (path.equalsIgnoreCase("world_specific_behavior")) {
                String append = "";
                if (configuration.get(path) != null) {
                    append = " Your preferred value will still be used: " + configuration.get(path);
                } else {
                    append = " The default value will be used: true";
                }
                console.sendMessage("[BetterSleeping] " + Color.RED + "Your config file contains \'world_specific_behavior\', please replace this with \'multiworld_support\'." + append);
            }
        } else if (type == FileType.LANG)
        {
            if (path.equalsIgnoreCase("prefix"))
            {
                if (!configuration.getString(path).toLowerCase().contains("bettersleeping"))
                {
                    console.sendMessage("[BetterSleeping] Please consider keeping \'BetterSleeping\' in the prefix, as it would support my plugin.");
                    console.sendMessage("[BetterSleeping] Of course you are not obliged to do so, since I gave you the option but it would be greatly appreciated! :-)");
                }
            }
        }
    }

    /**
     * Get the name of this config file
     * @return
     */
    public String getConfigName()
    {
        switch(type)
        {
            case LANG:   return "lang.yml";
            case CONFIG: return "config.yml";
        }

        return "";
    }
}