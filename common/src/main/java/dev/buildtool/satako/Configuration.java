package dev.buildtool.satako;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

/**
 * Json-based configuration
 */
public class Configuration {
    private final HashMap<String, Object> options=new HashMap<>();
    private final Gson gson=new GsonBuilder().setPrettyPrinting().create();
    private final Path path;

    /**
     * @param fileName without extension
     */
    public Configuration(String fileName) throws IOException {
        Path config = Path.of("config");
        path = Path.of(config.toString(), fileName+".json");
        if(!Files.exists(config))
            Files.createDirectory(config);
        if(!Files.exists(path)) {
            Files.createFile(path);
        }
    }

    /**
     * (Over)writes configuration file
     */
    public void save() throws IOException {
        Files.write(path, List.of(gson.toJson(options)));
    }

    public int getInt(String name,int defaultValue,int min,int max)
    {
        return (int) options.compute(name,(s, objects) ->   objects ==null ? defaultValue:Math.clamp(defaultValue,min,max));
    }
}
