package me.lucaaa.advanceddisplays.api;

import me.lucaaa.advanceddisplays.api.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.api.displays.BlockDisplay;
import me.lucaaa.advanceddisplays.api.displays.ItemDisplay;
import me.lucaaa.advanceddisplays.api.displays.TextDisplay;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

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

    /**
     * Creates a block display at the given location.
     * @param name The name of the display.
     * @param location The location of the display.
     * @param value The block that will be displayed.
     * @return The created display or null if a display with that name already exists.
     */
    BlockDisplay createBlockDisplay(String name, Location location, BlockData value);

    /**
     * Creates an item display at the given location.
     * @param name The name of the display.
     * @param location The location of the display.
     * @param value The item that will be displayed.
     * @return The created display or null if a display with that name already exists.
     */
    ItemDisplay createItemDisplay(String name, Location location, Material value);

    /**
     * Creates a text display at the given location.
     * @param name The name of the display.
     * @param location The location of the display.
     * @param value The text that will be displayed. If the list has multiple elements, the display will be animated.
     * @return The created display or null if a display with that name already exists.
     */
    TextDisplay createTextDisplay(String name, Location location, List<String> value);

    /**
     * Gets a previously created display.
     * @param name The name of the display.
     * @return The display if it exists, null if it doesn't.
     */
    BaseDisplay getDisplay(String name);

    /**
     * Removes a previously created display. Deletes the entity from the world and removes the display from the list.
     * @param name The name of the display.
     */
    void removeDisplay(String name);

    /**
     * Spawns all the displays to a certain player. This method is useful, for example, when a player enters the server.
     * @param player The player that will see the displays.
     */
    void spawnDisplaysToPlayer(Player player);
}
