package be.dezijwegel.bettersleeping.util;

import be.dezijwegel.bettersleeping.events.handlers.BuffsHandler;
import be.dezijwegel.bettersleeping.events.handlers.TimeSetToDayCounter;
import be.dezijwegel.bettersleeping.hooks.EssentialsHook;
import be.dezijwegel.bettersleeping.interfaces.SleepersNeededCalculator;
import be.dezijwegel.bettersleeping.sleepersneeded.AbsoluteNeeded;
import be.dezijwegel.bettersleeping.sleepersneeded.PercentageNeeded;
import be.dezijwegel.bettersleeping.timechange.TimeChanger;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.DrilldownPie;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.GameMode;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class BStatsHandler {

    public BStatsHandler (
            JavaPlugin plugin,
            ConfigLib config, ConfigLib sleepingSettings, ConfigLib bypassing,
            EssentialsHook essentialsHook, BuffsHandler buffsHandler, TimeSetToDayCounter timeSetToDayCounter,
            boolean isMultiWorld
    ) {
        // Report plugin and server metrics

        Metrics metrics = new Metrics(plugin, 7414);

        metrics.addCustomChart(new SimplePie("spigot_version", () -> plugin.getServer().getBukkitVersion().split("-")[0]));

        String lang = config.getConfiguration().getString("language");
        String[] substr = lang != null ? lang.split("-") : new String[]{"en", "US"} ;
        String localisation = substr.length >= 2 ? substr[0].toLowerCase() + "-" + substr[1].toUpperCase() : "en-US";
        metrics.addCustomChart(new SimplePie("localization", () -> localisation));

        if (lang != null)
        {
            metrics.addCustomChart(new DrilldownPie("language_adv", () -> {
                Map<String, Map<String, Integer>> map = new HashMap<>();
                Map<String, Integer> entry = new HashMap<>();

                String main;
                if (lang.equals("en-US")) {
                    main = "en-US";
                }
                else {
                    main = "other";
                }

                entry.put(lang, 1);
                map.put(main, entry);
                return map;
            }));
        }

        metrics.addCustomChart(new SimplePie("auto_add_missing_options", () -> config.getConfiguration().getString("auto_add_missing_options")));

        metrics.addCustomChart(new SimplePie("server_has_essentials", () -> essentialsHook.isHooked() ? "Yes" : "No"));

        metrics.addCustomChart(new SimplePie("server_has_placeholderapi", () -> plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI")!=null ? "Yes" : "No"));


        String counter = sleepingSettings.getConfiguration().getString("sleeper_counter");
        if (counter != null)
        {
            metrics.addCustomChart(new SimplePie("sleepers_calculator", counter::toLowerCase));

            if (counter.equalsIgnoreCase("absolute"))
            {
                int needed = sleepingSettings.getConfiguration().getInt("absolute.needed");
                metrics.addCustomChart(new SimplePie("percentage_needed", () -> String.valueOf( needed )));
            }
            else if (counter.equalsIgnoreCase("percentage"))
            {
                int needed = sleepingSettings.getConfiguration().getInt("percentage.needed");
                metrics.addCustomChart(new SimplePie("absolute_needed", () -> String.valueOf( needed )));
            }

            metrics.addCustomChart(new DrilldownPie("sleepers_calculator_drilldown", () -> {
                Map<String, Map<String, Integer>> map = new HashMap<>();
                Map<String, Integer> entry = new HashMap<>();

                String category;
                if (counter.equalsIgnoreCase( "absolute" ) || counter.equalsIgnoreCase( "percentage" ))
                    category = counter;
                else category = "Other";

                entry.put(counter, 1);
                map.put(category, entry);
                return map;
            }));
        }

        metrics.addCustomChart(new SingleLineChart("number_of_nights_skipped", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int num = timeSetToDayCounter.getCounter();
                timeSetToDayCounter.resetCounter();
                return num;
            }
        }));

        String correctedTimePassMode;
        String timePassMode = sleepingSettings.getConfiguration().getString("mode");
        if (timePassMode == null || !( timePassMode.equalsIgnoreCase("setter") || timePassMode.equalsIgnoreCase("smooth") ))
            correctedTimePassMode = "faulty setting";
        else
            correctedTimePassMode = timePassMode;

        metrics.addCustomChart(new SimplePie("time_changer_type", correctedTimePassMode::toLowerCase));


        metrics.addCustomChart(new SimplePie("enable_bypass", () -> bypassing.getConfiguration().getBoolean("enable_bypass_permissions") ? "Yes" : "No"));

        List<GameMode> bypassedGamemodes = new ArrayList<>();
        for (String path : bypassing.getConfiguration().getKeys(false))
        {
            if (path.contains("ignore_") && bypassing.getConfiguration().getBoolean(path))
            {
                path = path.replace("ignore_", "");
                try
                {
                    GameMode gm = GameMode.valueOf(path.toUpperCase());
                    bypassedGamemodes.add( gm );
                } catch(IllegalArgumentException ignored) {}
            }
        }

        metrics.addCustomChart(new AdvancedPie("bypassed_gamemodes", () -> {
            Map<String, Integer> valueMap = new HashMap<>();

            // Add all bypassed gamemodes
            for (GameMode mode : bypassedGamemodes)
            {
                valueMap.put( mode.toString().toLowerCase(), 1 );
            }

            // Report when no game modes are bypassed
            if (valueMap.size() == 0)
                valueMap.put("None", 1);

            return valueMap;
        }));

        metrics.addCustomChart(new SimplePie("uses_buffs", () -> buffsHandler.getBuffs().size() != 0 ? "Yes" : "No"));

        metrics.addCustomChart(new SimplePie("uses_debuffs", () -> buffsHandler.getDebuffs().size() != 0 ? "Yes" : "No" ));

        metrics.addCustomChart(new SimplePie("is_multiworld", () -> isMultiWorld ? "Yes" : "No" ));

        metrics.addCustomChart(new SimplePie("is_premium", () -> "No" ));


    }

}
