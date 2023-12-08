package me.lucaaa.advanceddisplays.api;

import org.bukkit.plugin.Plugin;

/**
 * AdvancedDisplays' API class.
*/
@SuppressWarnings("unused")
public interface ADAPI {

    /**
     * Gets an instance of the AdvancedDisplays plugin API.
     *
     * @param plugin An instance of your plugin
     * @return The instance of the API.
     */
    static ADAPI getInstance(Plugin plugin) {
        return ADAPIProvider.getImplementation().getAPI(plugin);
    }

    String testMethod();
}
