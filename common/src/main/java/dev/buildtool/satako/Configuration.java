package dev.buildtool.satako;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.util.Mth;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

/**
 * Json-based configuration
 */
public class Configuration {
    private HashMap<String, Object> options=new HashMap<>();
    private final Gson gson=new GsonBuilder().setPrettyPrinting().create();
    private final Path path;

    /**
     * @param fileName without extension
     */
    public Configuration(String fileName) throws IOException {
        Path config = Path.of("config");
        path = Path.of(config.toString(), fileName+".json");
        if(!Files.exists(config)) {
            Files.createDirectory(config);
        }
        if(!Files.exists(path)) {
            Files.createFile(path);
        }
        else {
            String s=Files.readString(path);
            options=gson.fromJson(s,HashMap.class);
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
        Double compute = (Double) options.compute(name, (s, objects) -> objects == null ? defaultValue : Math.clamp((double) objects, min, max));
        return compute.intValue();
    }

    public boolean getBoolean(String name,boolean defaultValue)
    {
        return (boolean) options.compute(name,(s, o) -> o==null ?defaultValue :o);
    }

    public List<String> getList(String name,List<String> defaultValue)
    {
        return (List<String>) options.compute(name,(s, l)->l==null ?defaultValue:l);
    }

    public float getFloat(String name,float defaultValue,float min,float max)
    {
        return (float) options.compute(name, (s, o) -> o == null ? defaultValue : Mth.clamp((float) o, min, max));
    }
}
