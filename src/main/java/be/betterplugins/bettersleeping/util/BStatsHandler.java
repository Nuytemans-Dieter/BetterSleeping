package be.betterplugins.bettersleeping.util;

import be.betterplugins.bettersleeping.model.ConfigContainer;
import be.betterplugins.bettersleeping.hooks.EssentialsHook;
import be.betterplugins.bettersleeping.listeners.BuffsHandler;
import be.betterplugins.bettersleeping.listeners.TimeSetToDayCounter;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.DrilldownPie;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import javax.inject.Inject;
import java.util.*;

public class BStatsHandler {

    @Inject
    public BStatsHandler
    (
            JavaPlugin plugin,
            ConfigContainer configContainer,
            EssentialsHook essentialsHook, BuffsHandler buffsHandler, TimeSetToDayCounter timeSetToDayCounter,
            List<World> worlds
    )
    {

        // Get YamlConfigurations
        YamlConfiguration config = configContainer.getConfig();
        YamlConfiguration sleepingSettings = configContainer.getSleeping_settings();
        YamlConfiguration bypassing = configContainer.getBypassing();

        // Report plugin and server metrics

        Metrics metrics = new Metrics(plugin, 7414);

        metrics.addCustomChart(new SimplePie("spigot_version", () -> plugin.getServer().getBukkitVersion().split("-")[0]));

        String lang = config.getString("language");
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

        metrics.addCustomChart(new SimplePie("server_has_essentials", () -> essentialsHook.isHooked() ? "Yes" : "No"));

        metrics.addCustomChart(new SimplePie("server_has_placeholderapi", () -> plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI")!=null ? "Yes" : "No"));


        String counter = sleepingSettings.getString("sleeper_calculator");
        if (counter != null)
        {
            final int needed = sleepingSettings.getInt("needed");

            metrics.addCustomChart(new DrilldownPie("sleepers_calculator_drilldown", () -> {
                Map<String, Map<String, Integer>> map = new HashMap<>();
                Map<String, Integer> entry = new HashMap<>();

                String category;
                if (counter.equalsIgnoreCase( "absolute" ) || counter.equalsIgnoreCase( "percentage" ))
                    category = counter;
                else category = "Other";

                entry.put("" + needed, 1);
                map.put(category, entry);
                return map;
            }));
        }

        metrics.addCustomChart(
            new DrilldownPie("day_length",
                () ->
                {
                    Map<String, Map<String, Integer>> map = new HashMap<>();
                    Map<String, Integer> entry = new HashMap<>();

                    int length = sleepingSettings.getInt("day_length");
                    String category = length == 700 ? "Default" : "Modified";

                    entry.put("" + length, 1);
                    map.put(category, entry);
                    return map;
                }
            )
        );

        metrics.addCustomChart(
            new DrilldownPie("night_length",
                () ->
                {
                    Map<String, Map<String, Integer>> map = new HashMap<>();
                    Map<String, Integer> entry = new HashMap<>();

                    int length = sleepingSettings.getInt("night_length");
                    String category = length == 500 ? "Default" : "Modified";

                    entry.put("" + length, 1);
                    map.put(category, entry);
                    return map;
                }
            )
        );

        metrics.addCustomChart(
            new DrilldownPie("night_skip_length",
                () ->
                {
                    Map<String, Map<String, Integer>> map = new HashMap<>();
                    Map<String, Integer> entry = new HashMap<>();

                    int length = sleepingSettings.getInt("night_skip_length");
                    String category = length == 10 ? "Default" : "Modified";

                    entry.put("" + length, 1);
                    map.put(category, entry);
                    return map;
                }
            )
        );

        metrics.addCustomChart(new SimplePie("uses_bossbar", () -> config.getBoolean("enable_bossbar") ? "Yes" : "No"));

        metrics.addCustomChart(new SimplePie("update_notifier", () -> config.getBoolean("update_notifier") ? "Enabled" : "Disabled"));

        metrics.addCustomChart(new SingleLineChart("number_of_nights_skipped", timeSetToDayCounter::resetCounter));

        metrics.addCustomChart(new SimplePie("enable_bypass", () -> bypassing.getBoolean("enable_bypass_permissions") ? "Yes" : "No"));

        List<GameMode> bypassedGamemodes = new ArrayList<>();
        for (String path : bypassing.getKeys(false))
        {
            if (path.contains("ignore_") && bypassing.getBoolean(path))
            {
                path = path.replace("ignore_", "");
                try
                {
                    GameMode gm = GameMode.valueOf(path.toUpperCase());
                    bypassedGamemodes.add( gm );
                } catch(IllegalArgumentException ignored) {}
            }
        }

        metrics.addCustomChart(new AdvancedPie("bypassed_gamemodes",
            () -> {
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
            }
        ));

        Set<PotionEffect> buffs = buffsHandler.getBuffs();
        metrics.addCustomChart(
            new DrilldownPie(
            "has_buffs",
                () -> {
                    Map<String, Map<String, Integer>> map = new HashMap<>();
                    Map<String, Integer> entry = new HashMap<>();

                    String isEnabled = buffs.size() > 0 ? "Yes": "No";

                    for (PotionEffect buff : buffs)
                    {
                        String name = buff.toString().split(":")[0].toLowerCase().replace("_", " ");
                        entry.put(name, 1);
                    }

                    map.put(isEnabled, entry);
                    return map;
                }
            )
        );

        Set<PotionEffect> debuffs = buffsHandler.getDebuffs();
        metrics.addCustomChart(
            new DrilldownPie(
                "has_debuffs",
                () -> {
                    Map<String, Map<String, Integer>> map = new HashMap<>();
                    Map<String, Integer> entry = new HashMap<>();

                    String isEnabled = debuffs.size() > 0 ? "Yes": "No";

                    for (PotionEffect buff : debuffs)
                    {
                        String name = buff.toString().split(":")[0].toLowerCase().replace("_", " ");
                        entry.put(name, 1);
                    }

                    map.put(isEnabled, entry);
                    return map;
                }
            )
        );

        long numWorlds = worlds.stream().filter( world -> world.getEnvironment() == World.Environment.NORMAL || world.getEnvironment() == World.Environment.CUSTOM ).count();
        metrics.addCustomChart(new SimplePie("is_multiworld", () -> numWorlds > 1 ? "Yes" : "No" ));

        metrics.addCustomChart(new SimplePie("is_premium", () -> "No" ));


    }

}
