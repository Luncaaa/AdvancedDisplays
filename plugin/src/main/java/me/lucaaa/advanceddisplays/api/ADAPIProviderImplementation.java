package me.lucaaa.advanceddisplays.api;

import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ADAPIProviderImplementation extends ADAPIProvider {
    private final Map<Plugin, ADAPIImplementation> apiMap = new ConcurrentHashMap<>();

    @Override
    public ADAPIImplementation getAPI(Plugin plugin) {
        return this.apiMap.computeIfAbsent(plugin, p -> new ADAPIImplementation(plugin.getName()));
    }
}
