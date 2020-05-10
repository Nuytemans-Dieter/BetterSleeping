package be.dezijwegel.bettersleeping.util;

import be.dezijwegel.bettersleeping.events.handlers.BedEventHandler;
import be.dezijwegel.bettersleeping.hooks.EssentialsHook;
import be.dezijwegel.bettersleeping.timechange.TimeChanger;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class MetricsHandler {

    public MetricsHandler(JavaPlugin plugin, String localised, boolean autoAddMissingOptions, EssentialsHook essentialsHook, String sleeperCalculatorType,
                          TimeChanger.TimeChangeType timeChangerType, int percentageNeeded, int absoluteNeeded, boolean enableBypassing, FileConfiguration bypassing)
    {

        // Report plugin and server metrics

        Metrics metrics = new Metrics(plugin, 7414);

        metrics.addCustomChart(new Metrics.SimplePie("spigot_version", () -> plugin.getServer().getBukkitVersion().split("-")[0]));

        String[] substr = localised.split("-");
        String localisation = substr.length >= 2 ? substr[0].toLowerCase() + "-" + substr[1].toUpperCase() : "en-US";
        metrics.addCustomChart(new Metrics.SimplePie("localization", () -> localisation));

        metrics.addCustomChart(new Metrics.SimplePie("auto_add_missing_options", () -> String.valueOf(autoAddMissingOptions)));

        metrics.addCustomChart(new Metrics.SimplePie("server_has_essentials", () -> String.valueOf(essentialsHook.isHooked())));

        if (sleeperCalculatorType != null)
        {
            metrics.addCustomChart(new Metrics.SimplePie("sleepers_calculator", sleeperCalculatorType::toLowerCase));

            if (sleeperCalculatorType.equalsIgnoreCase("percentage"))
            {
                metrics.addCustomChart(new Metrics.SimplePie("percentage_needed", () -> String.valueOf(percentageNeeded)));
            }
            else if (sleeperCalculatorType.equalsIgnoreCase("absolute"))
            {
                metrics.addCustomChart(new Metrics.SimplePie("absolute_needed", () -> String.valueOf(absoluteNeeded)));
            }
        }

        metrics.addCustomChart(new Metrics.SimplePie("time_changer_type", () -> timeChangerType.name().toLowerCase()));

        metrics.addCustomChart(new Metrics.SimplePie("enable_bypass", () -> String.valueOf(bypassing.getBoolean("enable_bypass_permissions"))));

        metrics.addCustomChart(new Metrics.SimplePie("bypass_survival", () -> String.valueOf( bypassing.getBoolean("ignore_survival") )));

        metrics.addCustomChart(new Metrics.SimplePie("bypass_creative", () -> String.valueOf( bypassing.getBoolean("ignore_creative") )));

        metrics.addCustomChart(new Metrics.SimplePie("is_premium", () -> "No" ));

    }

}
