package me.lucaaa.advanceddisplays.api;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.conditions.ConditionsFactory;
import me.lucaaa.advanceddisplays.conditions.ADConditionsFactory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ADAPIProviderImplementation extends ADAPIProvider {
    private final AdvancedDisplays plugin;
    private final ConditionsFactory conditionsFactory;
    private final Map<Plugin, ADAPIImplementation> apiMap = new ConcurrentHashMap<>();

    public ADAPIProviderImplementation(AdvancedDisplays plugin) {
        this.plugin = plugin;
        this.conditionsFactory = new ADConditionsFactory();
    }

    @Override
    public ADAPIImplementation getAPI(JavaPlugin apiPlugin) {
        return apiMap.computeIfAbsent(plugin, p -> new ADAPIImplementation(plugin, apiPlugin));
    }

    @Override
    public ConditionsFactory getConditionsFactory() {
        return conditionsFactory;
    }

    public Map<Plugin, ADAPIImplementation> getApiMap() {
        return apiMap;
    }
}