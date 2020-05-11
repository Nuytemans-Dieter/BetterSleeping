package be.dezijwegel.bettersleeping.util;

import be.dezijwegel.bettersleeping.events.handlers.BuffsHandler;
import be.dezijwegel.bettersleeping.hooks.EssentialsHook;
import be.dezijwegel.bettersleeping.permissions.BypassChecker;
import be.dezijwegel.bettersleeping.timechange.TimeChanger;
import org.bstats.bukkit.Metrics;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class MetricsHandler {

    public MetricsHandler(JavaPlugin plugin, String localised, boolean autoAddMissingOptions, EssentialsHook essentialsHook, String sleeperCalculatorType,
                          TimeChanger.TimeChangeType timeChangerType, int percentageNeeded, int absoluteNeeded, BypassChecker bypassChecker,
                          BuffsHandler buffsHandler)
    {

        // Report plugin and server metrics

        Metrics metrics = new Metrics(plugin, 7414);

        metrics.addCustomChart(new Metrics.SimplePie("spigot_version", () -> plugin.getServer().getBukkitVersion().split("-")[0]));

        String[] substr = localised.split("-");
        String localisation = substr.length >= 2 ? substr[0].toLowerCase() + "-" + substr[1].toUpperCase() : "en-US";
        metrics.addCustomChart(new Metrics.SimplePie("localization", () -> localisation));

        metrics.addCustomChart(new Metrics.SimplePie("auto_add_missing_options", () -> String.valueOf(autoAddMissingOptions)));

        metrics.addCustomChart(new Metrics.SimplePie("server_has_essentials", () -> essentialsHook.isHooked() ? "Yes" : "No"));

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

        metrics.addCustomChart(new Metrics.SimplePie("enable_bypass", () -> bypassChecker.isEnabled() ? "Yes" : "No"));

        metrics.addCustomChart(new Metrics.AdvancedPie("bypassed_gamemodes", () -> {
            Map<String, Integer> valueMap = new HashMap<>();

            // Add all bypassed gamemodes
            for (GameMode mode : bypassChecker.getBypassedGamemodes())
            {
                valueMap.put( mode.toString().toLowerCase(), 1 );
            }

            // Report when no game modes are bypassed
            if (valueMap.size() == 0)
                valueMap.put("None", 1);

            return valueMap;
        }));

        metrics.addCustomChart(new Metrics.SimplePie("uses_buffs", () -> buffsHandler.getBuffs().size() != 0 ? "Yes" : "No"));

        metrics.addCustomChart(new Metrics.SimplePie("uses_debuffs", () -> buffsHandler.getDebuffs().size() != 0 ? "Yes" : "No" ));

        metrics.addCustomChart(new Metrics.SimplePie("is_premium", () -> "No" ));

    }

}
