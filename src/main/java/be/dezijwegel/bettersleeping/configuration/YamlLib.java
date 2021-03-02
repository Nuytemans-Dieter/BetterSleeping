package be.dezijwegel.bettersleeping.configuration;

import org.apache.commons.lang.StringUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class YamlLib {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public YamlLib(String name, JavaPlugin plugin) throws IOException {

        //
        // Copy temp files
        //

        TempFileCopier defaultCopy = new TempFileCopier( plugin, "", name, "temp" + File.separator );
        TempFileCopier templateCopy = new TempFileCopier( plugin, "templates/", name, "temp" + File.separator + "templates" + File.separator);


        //
        // Get a MAP that contains all key-values pairs: Existing values where possible, defaults otherwise
        //


        // Get config options on the live server
        File liveConfig = new File(plugin.getDataFolder(), name);
        Map<String, Object> liveContents;
        if (liveConfig.exists())
            liveContents = new YamlReader(liveConfig).getContents();
        else
            liveContents = new HashMap<>();

        // Get config with default values
        File defaultFile = new File(plugin.getDataFolder() + File.separator + "temp", name);
        Map<String, Object> defaultContents = new YamlReader( defaultFile ).getContents();

        // Merge live and default values
        YamlMerger merger = new YamlMerger( defaultContents );
        Map<String, Object> newContents = merger.merge( liveContents );


        //
        // Write the template and replace all tags by their corresponding values in the map if available
        //


        // Prepare file writer and template reader
        BufferedWriter writer = Files.newBufferedWriter(Paths.get( plugin.getDataFolder() + File.separator + name ));
        BufferedReader reader = new BufferedReader( new FileReader( new File(plugin.getDataFolder() + File.separator + "temp/templates/" + name) ) );

        // Prepare YAMLSnake
//        DumperOptions options = new DumperOptions();
//        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
//        Yaml yaml = new Yaml(options);
        Yaml yaml = new Yaml();

        // Go through the template and replace all placeholders
        String line;
        while ((line = reader.readLine()) != null) {

            // Replace placeholders
            String[] replaceThis = StringUtils.substringsBetween(line, "{", "}");
            if (replaceThis != null)
            {
                for (String tag : replaceThis)
                {
                    if ( newContents.containsKey( tag ) )
                    {
                        String placeholder = "{" + tag + "}";
                        String dumped = yaml.dump( newContents.get( tag ) );
                        line = line.replace( placeholder, dumped );;
                    }
                }
            }

            // Write to file
            writer.append(line);
            writer.newLine();
        }

        writer.close();

        //
        //  Cleanup: Delete temp folders
        //

        templateCopy.deleteCopiedFile();
        defaultCopy.deleteCopiedFile();

        new File(plugin.getDataFolder() + File.separator + "temp" + File.separator + "templates" + File.separator).delete();
        new File(plugin.getDataFolder() + File.separator + "temp" + File.separator).delete();
    }

}
