package me.lucaaa.advanceddisplays.api;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ADAPIProviderImplementation extends ADAPIProvider {
    private final AdvancedDisplays plugin;
    private final Map<Plugin, ADAPIImplementation> apiMap = new ConcurrentHashMap<>();

    public ADAPIProviderImplementation(AdvancedDisplays plugin) {
        this.plugin = plugin;
    }

    @Override
    public ADAPIImplementation getAPI(Plugin plugin) {
        return apiMap.computeIfAbsent(plugin, p -> new ADAPIImplementation(this.plugin, plugin.getName()));
    }

    public Map<Plugin, ADAPIImplementation> getApiMap() {
        return apiMap;
    }
}
