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
    public Configuration(String fileName) {
        Path config = Path.of("config");
        path = Path.of(config.toString(), fileName+".json");
        if(!Files.exists(config)) {
            try {
                Files.createDirectory(config);
            } catch (IOException e) {
                throw new RuntimeException("Couldn't create config directory");
            }
        }
        if(!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException("Couldn't create "+path);
            }
        }
    }

    /**
     * (Over)writes configuration file
     */
    public void save() {
        try {
            Files.write(path, List.of(gson.toJson(options)));
        } catch (IOException e) {
            throw new RuntimeException("Couldn't save configuration "+path);
        }
    }

    public int getInt(String name,int defaultValue,int min,int max)
    {
        return (int) options.compute(name,(s, objects) ->   objects ==null ? defaultValue:Math.clamp((int) objects,min,max));
    }

    public boolean getBoolean(String name,int defaultValue)
    {
        return (boolean) options.compute(name,(s, o) -> o==null ?defaultValue :o);
    }
}
