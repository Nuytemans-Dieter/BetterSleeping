package be.dezijwegel.bettersleeping.configuration;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class YamlReader {

    private final Map<String, Object> contents = new HashMap<>();

    public YamlReader(File file) throws FileNotFoundException
    {
        this( new FileInputStream( file ) );
    }

    public YamlReader(InputStream fis)
    {
//        DumperOptions options = new DumperOptions();
//        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
//        Yaml yaml = new Yaml(options);

        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setAllowRecursiveKeys(true);

        Yaml yaml = new Yaml(loaderOptions);
        Map<String, Object> yamlContent = yaml.load(fis);
//        this.contents = yamlContent != null ? yamlContent : new HashMap<>();

        System.out.println("Reading new file...");
        for (Map.Entry<String, Object> entry : yamlContent.entrySet())
        {
            addRecursiveContents(entry, "");
        }
    }

    public Map<String, Object> getContents()
    {
        return this.contents;
    }

    private void addRecursiveContents(Map.Entry<String, Object> entry, String path)
    {
        String key = entry.getKey();
        Object value = entry.getValue();

        final String newPath = path.equals("") ? key : path + "." + key;

        if (value instanceof Map)
        {
            Map<String, Object> map = (Map<String, Object>) value;
            for (Map.Entry<String, Object> subEntry : map.entrySet())
                addRecursiveContents( subEntry, newPath);
        }
        else
        {
            contents.put(newPath, value);
        }
    }
}