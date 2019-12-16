package be.dezijwegel.files;

import be.dezijwegel.BetterSleeping;
import be.dezijwegel.interfaces.Reloadable;
import org.bukkit.plugin.java.JavaPlugin;

public class Console implements Reloadable {

    private JavaPlugin plugin;
    private ConfigAPI configAPI;

    public Console(JavaPlugin plugin)
    {
        this.plugin = plugin;
        configAPI = new ConfigAPI(ConfigAPI.FileType.CONSOLE, plugin);
        // A check on missing options is redundant for this small config file
        //configAPI.reportMissingOptions();
    }


    /**
     * Get whether negative logs should be colored red
     * If false, all negative text will be white
     * @return a boolean: should the text be colored red?
     */
    public boolean isNegativeRed()
    {
        return configAPI.getBoolean("negative_red");
    }


    /**
     * Get whether positive logs should be colored green
     * If false, all positive text will be white
     * @return a boolean: should the text be colored green?
     */
    public boolean isPositiveGreen()
    {
        return configAPI.getBoolean("positive_green");
    }


    @Override
    public void reload() {
        configAPI = new ConfigAPI(ConfigAPI.FileType.CONSOLE, plugin);
        configAPI.reportMissingOptions();
    }
}
