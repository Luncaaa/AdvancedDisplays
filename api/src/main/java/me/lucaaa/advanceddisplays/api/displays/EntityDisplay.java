package me.lucaaa.advanceddisplays.api.displays;

import me.lucaaa.advanceddisplays.api.actions.DisplayActions;
import me.lucaaa.advanceddisplays.api.conditions.Condition;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.api.displays.enums.EditorItem;
import me.lucaaa.advanceddisplays.api.displays.visibility.VisibilityManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The settings that Entity and Text, Item and Block displays have in common.
 */
@SuppressWarnings("unused")
public interface EntityDisplay {
    /**
     * Gets the name of the display.
     * @return The name of the display.
     */
    String getName();

    /**
     * Gets the type of the display.
     * @return The type of the display: Either BLOCK, ITEM or TEXT
     */
    DisplayType getType();

    /**
     * Gets the display's visibility manager.
     * @return The display's visibility manager.
     */
    VisibilityManager getVisibilityManager();

    /**
     * Opens the in-game editor for a player with all settings being enabled.
     * @param player The player for whom the editor will be opened.
     */
    void openEditor(Player player);

    /**
     * Opens the in-game editor for a player with the specified settings being disabled.
     * @param player The player for whom the editor will be opened.
     * @param disabledSettings The items the player won't be able to interact with.
     */
    void openEditor(Player player, List<EditorItem> disabledSettings);

    /**
     * Closes the in-game editor for a player.
     * @param player The player for whom the editor will be closed.
     */
    void closeEditor(Player player);

    /**
     * Gets the location of the display.
     * @return The location of the display.
     */
    Location getLocation();

    /**
     * Sets the location of the display.
     * @param location The new location.
     */
    void setLocation(Location location);

    /**
     * Centers the display on the block its on.
     * @return The centered location.
     */
    Location center();

    /**
     * Gets the display's yaw.
     * @return The display's yaw.
     */
    float getYaw();

    /**
     * Gets the display's pitch.
     * @return The display's pitch.
     */
    float getPitch();

    /**
     * Sets the display's rotation for everyone.
     * @param yaw The new yaw.
     * @param pitch The new pitch.
     */

    void setRotation(float yaw, float pitch);

    /**
     * Sets the display's rotation for a specific player.
     * @param yaw The new yaw.
     * @param pitch The new pitch.
     * @param player The player who will see the new rotation.
     */
    void setRotation(float yaw, float pitch, Player player);

    /**
     * Returns whether the display's glow is active or not.
     * @return Whether the display is glowing or not.
     */
    boolean isGlowing();

    /**
     * Makes the display glow for everyone.
     * @param isGlowing If the display will glow or not.
     */
    void setGlowing(boolean isGlowing);

    /**
     * Makes the display glow for a specific player.
     * @param isGlowing If the display will glow or not.
     * @param player The player who will see the glow.
     */
    void setGlowing(boolean isGlowing, Player player);

    /**
     * Gets the display's glow color.
     * @return The display's glow color.
     */
    ChatColor getGlowColor();

    /**
     * Sets the glow color of the display for everyone. Only visible if the display is glowing.
     * @param color The glow color of the display.
     */
    void setGlowColor(ChatColor color);

    /**
     * Sets the glow color of the display for a specific player. Only visible if the display is glowing.
     * @param color The glow color of the display.
     * @param player The player who will see the glow color.
     */
    void setGlowColor(ChatColor color, Player player);

    /**
     * Adds a condition that the player must meet to see the display.
     * @param condition The condition that the player must meet.
     */
    default void addViewCondition(Condition condition) {
        getVisibilityManager().addViewCondition(condition);
    }

    /**
     * Removes all the conditions.
     */
    default void clearConditions() {
        getVisibilityManager().clearConditions();
    }

    /**
     * Sets the code to run when the display is clicked.
     * @param actions The code to run.
     */
    void setClickActions(DisplayActions actions);

    /**
     * Deletes the hologram.
     * <p>
     * Online players will stop seeing it, and it won't be spawned for new players.
     */
    void remove();

    /**
     * Checks whether the hologram is removed or not.
     * @return Whether the hologram is removed or not.
     */
    boolean isRemoved();
}