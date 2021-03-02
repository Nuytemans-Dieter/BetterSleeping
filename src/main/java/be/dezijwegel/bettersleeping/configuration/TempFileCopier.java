package be.dezijwegel.bettersleeping.configuration;

import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class TempFileCopier {

    private final File copiedFile;

    /**
     * Copy the specified file to a relative location of the plugins config folder
     * Provide an empty string if you want to copy to /plugins/YOURPLUGIN/fileName
     * fileName will be equal to the one provided in the constructor
     *
     * @param plugin the plugin for which we want to copy a file
     * @param resourcePath the path in the resources folder, must start and end with a slash. Eg. /somefolder/ OR can be an empty String
     * @param fileName the name of the file in this folder
     * @param path the path that specifies the folder to copy to. MUST end with a forward slash, but none in front eg. subfolder/
     */
    public TempFileCopier(Plugin plugin, String resourcePath, String fileName, String path) throws IOException {

//        resourcePath = resourcePath.replace("/", File.separator);
//        path = path.replace( "/", File.separator);

        BufferedReader defaultReader = new BufferedReader( new InputStreamReader(Objects.requireNonNull(plugin.getResource( resourcePath + fileName)), StandardCharsets.UTF_8) );
        String tempPath = plugin.getDataFolder() + File.separator + path;
        File tempFolder = new File(tempPath);
        tempFolder.mkdir();
        BufferedWriter tempWriter = new BufferedWriter( new FileWriter( tempPath + fileName) );
        while ( defaultReader.ready() )
        {
            String line = defaultReader.readLine();
            tempWriter.write( line );
            tempWriter.newLine();
        }
        tempWriter.close();

        copiedFile = new File( tempPath + File.separator + fileName);

    }


    /**
     * Delete the copied file
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void deleteCopiedFile()
    {
        this.copiedFile.delete();
    }

}
