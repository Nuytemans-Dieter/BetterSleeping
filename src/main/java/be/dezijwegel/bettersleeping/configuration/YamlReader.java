package be.dezijwegel.bettersleeping.configuration;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class YamlReader {

    private final Map<String, Object> contents = new HashMap<>();

    public YamlReader(File file) throws IOException
    {
        this( new FileInputStream( file ) );
    }

    public YamlReader(InputStream fis) throws IOException
    {
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setAllowRecursiveKeys(true);
        Yaml yaml = new Yaml(loaderOptions);
        Map<String, Object> yamlContent = yaml.load(fis);

        for (Map.Entry<String, Object> entry : yamlContent.entrySet())
        {
            addRecursiveContents(entry, "");
        }

        fis.close();
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
            @SuppressWarnings("unchecked")
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