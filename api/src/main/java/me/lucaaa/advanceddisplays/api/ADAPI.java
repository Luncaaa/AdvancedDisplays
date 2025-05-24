package me.lucaaa.advanceddisplays.api;

import me.lucaaa.advanceddisplays.api.conditions.ConditionsFactory;
import me.lucaaa.advanceddisplays.api.displays.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.java.JavaPlugin;

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
    static ADAPI getInstance(JavaPlugin plugin) {
        return ADAPIProvider.getImplementation().getAPI(plugin);
    }

    /**
     * Gets an instance of the ConditionsFactory interface.
     * @return The instance of the ConditionsFactory interface.
     */
    static ConditionsFactory getConditionsFactory() {
        return ADAPIProvider.getImplementation().getConditionsFactory();
    }

    /**
     * Creates a block display at the given location.
     * @param name The name of the display.
     * @param location The location of the display.
     * @param value The block that will be displayed.
     * @return The created display or null if a display with that name already exists.
     */
    default BlockDisplay createBlockDisplay(String name, Location location, BlockData value) {
        return createBlockDisplay(name, location, value, false);
    }

    /**
     * Creates a block display at the given location.
     * <p>
     * For more information on how saving to config works, check this page.
     *
     * @param name The name of the display.
     * @param location The location of the display.
     * @param value The block that will be displayed.
     * @param saveToConfig Whether the display should be saved in a file or not.
     * @return The created display or null if a display with that name already exists.
     */
    BlockDisplay createBlockDisplay(String name, Location location, BlockData value, boolean saveToConfig);

    /**
     * Creates an item display at the given location.
     * @param name The name of the display.
     * @param location The location of the display.
     * @param value The item that will be displayed.
     * @return The created display or null if a display with that name already exists.
     */
    default ItemDisplay createItemDisplay(String name, Location location, Material value) {
        return createItemDisplay(name, location, value, false);
    }

    /**
     * Creates an item display at the given location.
     * <p>
     * For more information on how saving to config works, check this page.
     *
     * @param name The name of the display.
     * @param location The location of the display.
     * @param value The item that will be displayed.
     * @param saveToConfig Whether the display should be saved in a file or not.
     * @return The created display or null if a display with that name already exists.
     */
    ItemDisplay createItemDisplay(String name, Location location, Material value, boolean saveToConfig);

    /**
     * Creates a text display at the given location.
     * @param name The name of the display.
     * @param location The location of the display.
     * @param value The text that will be displayed. Every element of the list will be a new line.
     * @return The created display or null if a display with that name already exists.
     */
    default TextDisplay createTextDisplay(String name, Location location, List<String> value) {
        return createTextDisplay(name, location, value, false);
    }

    /**
     * Creates a text display at the given location.
     * <p>
     * For more information on how saving to config works, check this page.
     *
     * @param name The name of the display.
     * @param location The location of the display.
     * @param value The text that will be displayed. Every element of the list will be a new line.
     * @param saveToConfig Whether the display should be saved in a file or not.
     * @return The created display or null if a display with that name already exists.
     */
    TextDisplay createTextDisplay(String name, Location location, List<String> value, boolean saveToConfig);

    /**
     * Creates a text display at the given location.
     * @param name The name of the display.
     * @param location The location of the display.
     * @param value The text that will be displayed.
     * @return The created display or null if a display with that name already exists.
     */
    default TextDisplay createTextDisplay(String name, Location location, Component value) {
        return createTextDisplay(name, location, value, false);
    }

    /**
     * Creates a text display at the given location.
     * <p>
     * For more information on how saving to config works, check this page.
     *
     * @param name The name of the display.
     * @param location The location of the display.
     * @param value The text that will be displayed.
     * @param saveToConfig Whether the display should be saved in a file or not.
     * @return The created display or null if a display with that name already exists.
     */
    TextDisplay createTextDisplay(String name, Location location, Component value, boolean saveToConfig);

    /**
     * Gets a previously created display.
     * @param name The name of the display.
     * @return The display if it exists, null if it doesn't.
     */
    BaseEntity getDisplay(String name);

    /**
     * Gets a display within a specific distance from the given location.
     * @param location The location to check.
     * @param radius The maximum distance.
     * @return A display within a specific distance from the given location or null if none found.
     */
    default BaseEntity getDisplayFromLoc(Location location, double radius) {
        return getDisplayFromLoc(location, radius, true);
    }

    /**
     * Gets a display within a specific distance from the given location.
     * @param location The location to check.
     * @param radius The maximum distance.
     * @param closest Whether the display should be the closest one to the given location or the first one found.
     * @return A display within a specific distance from the given location or null if none found.
     */
    BaseEntity getDisplayFromLoc(Location location, double radius, boolean closest);

    /**
     * Removes a previously created display. Deletes the entity from the world and removes the display from the list.
     * @param name The name of the display.
     */
    void removeDisplay(String name);

    /**
     * Removes all displays.
     */
    void removeAll();
}