package be.dezijwegel.files;

import be.dezijwegel.Runnables.DateChecker;
import be.dezijwegel.interfaces.Reloadable;
import org.bukkit.plugin.java.JavaPlugin;

public class EventsConfig implements Reloadable {

    private JavaPlugin plugin;
    private ConfigAPI configAPI;

    public EventsConfig(JavaPlugin plugin)
    {
        this.plugin = plugin;
        configAPI = new ConfigAPI(ConfigAPI.FileType.EVENTS, plugin);
    }


    /**
     * Check whether a certain event is enabled on this server
     * @return true if enabled, false otherwise
     */
    public boolean isEnabled(DateChecker.EventType type)
    {
        String path = "enable_";
        path += type.toString().toLowerCase();
        return configAPI.getBoolean(path);
    }


    @Override
    public void reload() {
        configAPI = new ConfigAPI(ConfigAPI.FileType.EVENTS, plugin);
    }
}
