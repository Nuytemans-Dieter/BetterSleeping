package be.dezijwegel.files;

import be.dezijwegel.bettersleeping.BetterSleeping;
import be.dezijwegel.bettersleeping.Reloadable;

import java.util.HashMap;
import java.util.Map;

public class Config implements Reloadable {

    private BetterSleeping plugin;
    private ConfigAPI configAPI;
//    private Map<String, Object> config;

    public Config(BetterSleeping plugin)
    {
        this.plugin = plugin;

        configAPI = new ConfigAPI(ConfigAPI.FileType.CONFIG, plugin);
//        config = new HashMap<String,Object>();
//
//        configAPI.loadTypesFromFile(String.class, config);
//        configAPI.loadTypesFromFile(Boolean.class, config);
    }

    /**
     * Get the value of an option, if it exists
     * Will return null when the option was not found
     * @param path
     * @return
     */
    public Object getOption(String path)
    {
        if (configAPI.get(path) != null)
        {
            return configAPI.get(path);
        } else
        {
            return configAPI.getDefault(path);
        }
    }

    /**
     * Get a Boolean value of an option
     * Will look in default values when the option was not found
     * Will return null when the option was not a Boolean or was also not found in default file
     * @param path
     * @return
     */
    public Boolean getBoolean(String path)
    {
        Object obj = getOption(path);
        if (obj instanceof Boolean)
            return (boolean) obj;
        else return null;
    }

    /**
     * Get an Integer value of an option
     * Will return null when the option was not found or was not an Integer
     * @param path
     * @return
     */
    public Integer getInt(String path)
    {
        Object obj = getOption(path);
        if (getOption(path) instanceof Integer)
            return (Integer) obj;
        else return 0;
    }

    @Override
    public void reload() {
        configAPI = new ConfigAPI(ConfigAPI.FileType.CONFIG, plugin);
//        config = new HashMap<String,Object>();

//        configAPI.loadTypesFromFile(String.class, config);
//        configAPI.loadTypesFromFile(Boolean.class, config);
    }
}
