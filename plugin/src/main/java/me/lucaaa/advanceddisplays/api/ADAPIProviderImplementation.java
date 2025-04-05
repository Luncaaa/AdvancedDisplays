package me.lucaaa.advanceddisplays.api;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ADAPIProviderImplementation extends ADAPIProvider {
    private final AdvancedDisplays plugin;
    private final Map<Plugin, ADAPIImplementation> apiMap = new ConcurrentHashMap<>();

    public ADAPIProviderImplementation(AdvancedDisplays plugin) {
        this.plugin = plugin;
    }

    @Override
    public ADAPIImplementation getAPI(JavaPlugin apiPlugin) {
        return apiMap.computeIfAbsent(plugin, p -> new ADAPIImplementation(this.plugin, apiPlugin));
    }

    public Map<Plugin, ADAPIImplementation> getApiMap() {
        return apiMap;
    }
}
