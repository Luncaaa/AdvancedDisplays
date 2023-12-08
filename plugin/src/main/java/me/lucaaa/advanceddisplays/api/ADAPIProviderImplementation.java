package me.lucaaa.advanceddisplays.api;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ADAPIProviderImplementation extends ADAPIProvider {

    private final AdvancedDisplays ad;
    private final Map<Plugin, ADAPIImplementation> apiMap = new ConcurrentHashMap<>();

    public ADAPIProviderImplementation(AdvancedDisplays ad) {
        this.ad = ad;
    }

    @Override
    public ADAPIImplementation getAPI(Plugin plugin) {
        return this.apiMap.computeIfAbsent(plugin, p -> new ADAPIImplementation(this.ad));
    }
}
