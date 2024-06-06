package me.lucaaa.advanceddisplays.api;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ADAPIProviderImplementation extends ADAPIProvider {
    private final AdvancedDisplays adPlugin;
    private final Map<Plugin, ADAPIImplementation> apiMap = new ConcurrentHashMap<>();

    public ADAPIProviderImplementation(AdvancedDisplays plugin) {
        this.adPlugin = plugin;
    }

    @Override
    public ADAPIImplementation getAPI(Plugin plugin) {
        return this.apiMap.computeIfAbsent(plugin, p -> new ADAPIImplementation(this.adPlugin, plugin.getName()));
    }
}
