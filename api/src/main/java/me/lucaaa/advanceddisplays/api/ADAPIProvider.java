package me.lucaaa.advanceddisplays.api;

import org.bukkit.plugin.Plugin;

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

    public abstract ADAPI getAPI(Plugin plugin);

}
