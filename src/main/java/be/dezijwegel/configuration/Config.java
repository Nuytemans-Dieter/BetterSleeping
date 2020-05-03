package be.dezijwegel.configuration;

import org.bukkit.plugin.java.JavaPlugin;

public class Config {



    public Config (JavaPlugin plugin)
    {
        ConfigLib lib = new ConfigLib("config.yml", plugin);

    }

}
