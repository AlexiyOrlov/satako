package dev.buildtool.satako.platform;

import dev.buildtool.satako.Satako;
import dev.buildtool.satako.platform.services.IPlatformHooks;

import java.util.ServiceLoader;

public class Services {

    public static final IPlatformHooks PLATFORM = load(IPlatformHooks.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Satako.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}