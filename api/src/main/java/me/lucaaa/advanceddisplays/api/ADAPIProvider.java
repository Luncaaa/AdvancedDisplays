package me.lucaaa.advanceddisplays.api;

import me.lucaaa.advanceddisplays.api.conditions.ConditionsFactory;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * INTERNAL USE ONLY - DO NOT USE!
 * @hidden
 */
public abstract class ADAPIProvider {

    private static ADAPIProvider implementation;

    public static ADAPIProvider getImplementation() {
        if (ADAPIProvider.implementation == null) {
            throw new IllegalStateException("The AdvancedDisplays API implementation is not set yet.");
        }
        return ADAPIProvider.implementation;
    }

    public static void setImplementation(ADAPIProvider implementation) {
        if (ADAPIProvider.implementation != null) {
            throw new IllegalStateException("The AdvancedDisplays API implementation is already set.");
        }
        ADAPIProvider.implementation = implementation;
    }

    public abstract ADAPI getAPI(JavaPlugin plugin);

    public abstract ConditionsFactory getConditionsFactory();
}