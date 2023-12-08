package me.lucaaa.advanceddisplays.api;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;

/**
 * INTERNAL USE ONLY - DO NOT USE!
 */
@ApiStatus.Internal
public abstract class ADAPIProvider {

    private static ADAPIProvider implementation;

    @ApiStatus.Internal
    public static ADAPIProvider getImplementation() {
        if (ADAPIProvider.implementation == null) {
            throw new IllegalStateException("The AdvancedDisplays API implementation is not set yet.");
        }
        return ADAPIProvider.implementation;
    }

    @ApiStatus.Internal
    public static void setImplementation(ADAPIProvider implementation) {
        if (ADAPIProvider.implementation != null) {
            throw new IllegalStateException("The AdvancedDisplays API implementation is already set.");
        }
        ADAPIProvider.implementation = implementation;
    }

    public abstract ADAPI getAPI(Plugin plugin);

}
