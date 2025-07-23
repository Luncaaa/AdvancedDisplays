package me.lucaaa.advanceddisplays.api.displays;

import me.lucaaa.advanceddisplays.api.actions.DisplayActions;
import me.lucaaa.advanceddisplays.api.conditions.Condition;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.api.displays.enums.EditorItem;
import me.lucaaa.advanceddisplays.api.displays.enums.NameVisibility;
import me.lucaaa.advanceddisplays.api.displays.visibility.VisibilityManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.List;

/**
 * The settings that Entity and Text, Item and Block displays have in common.
 * <p>
 * All "missing" properties, such as "is silent", do not affect the entity's appearance so they were not added.
 */
@SuppressWarnings("unused")
public interface BaseEntity {
    /**
     * Minimessage variable. Internal use only.
     * @hidden 
     */
    @ApiStatus.Internal
    MiniMessage minimessage = MiniMessage.miniMessage();
    
    /**
     * Gets the name of the display.
     * @return The name of the display.
     */
    String getName();

    /**
     * Gets the type of the display.
     * @return The type of the display: Either BLOCK, ENTITY, ITEM or TEXT
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
     * Deletes the display.
     * <p>
     * Online players will stop seeing it, and it won't be spawned for new players.
     */
    void remove();

    /**
     * Checks whether the display is removed or not.
     * @return Whether the display is removed or not.
     */
    boolean isRemoved();

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
     * Returns whether the entity appears to be on fire or not.
     * @return Whether the entity appears to be on fire or not.
     */
    boolean isOnFire();

    /**
     * Makes the entity appear to be on fire for everyone.
     * <p>
     * This is just visual - it won't damage the entity.
     * @param onFire Whether the entity will appear to be on fire or not.
     */
    void setOnFire(boolean onFire);

    /**
     * Makes the entity appear to be on fire for a specific player.
     * <p>
     * This is just visual - it won't damage the entity.
     * @param onFire Whether the entity will appear to be on fire or not.
     * @param player The player who will see the entity on fire.
     */
    void setOnFire(boolean onFire, Player player);

    /**
     * Returns whether the entity appears to be sprinting or not.
     * @return Whether the entity appears to be sprinting or not.
     */
    boolean isSprinting();

    /**
     * Makes the entity appear to be sprinting for everyone.
     * <p>
     * This just adds particles to the feet of the entity.
     * @param sprinting Whether the entity will appear to be sprinting or not.
     */
    void setSprinting(boolean sprinting);

    /**
     * Makes the entity appear to be sprinting for a specific player.
     * <p>
     * This just adds particles to the feet of the entity.
     * @param sprinting Whether the entity will appear to be sprinting or not.
     * @param player The player who will see the entity sprint.
     */
    void setSprinting(boolean sprinting, Player player);

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
     * <p>
     * Formats (such as bold or underlined) will be ignored.
     * @param color The glow color of the display.
     */
    void setGlowColor(ChatColor color);

    /**
     * Sets the glow color of the display for a specific player. Only visible if the display is glowing.
     * <p>
     * Formats (such as bold or underlined) will be ignored.
     * @param color The glow color of the display.
     * @param player The player who will see the glow color.
     */
    void setGlowColor(ChatColor color, Player player);

    /**
     * Returns the display's custom name.
     * <p>
     * This may be different from the value returned by {@link #getName()} and may be formatted.
     * It's the text shown on top of the entity.
     * @return The display's custom name.
     */
    @Nullable
    String getCustomName();

    /**
     * Sets the display's custom name for everyone.
     * <p>
     * Placeholders and colors will be parsed.
     * @param customName The display's custom name.
     */
    default void setCustomName(@Nullable String customName) {
        setCustomName(customName, getCustomNameVisibility());
    }

    /**
     * Sets the display's custom name for a specific player.
     * <p>
     * Placeholders and colors will be parsed.
     * Important! Custom name visibility will be reset. Consider using {@link #setCustomName(String, NameVisibility, Player)} instead.
     * @param customName The display's custom name.
     * @param player The player who will see the custom name.
     */
    default void setCustomName(@Nullable String customName, Player player) {
        setCustomName(customName, getCustomNameVisibility(), player);
    }

    /**
     * Sets the display's custom name for everyone.
     * <p>
     * Placeholders and colors will be parsed.
     * @param customName The display's custom name.
     */
    default void setCustomName(Component customName) {
        setCustomName(minimessage.serialize(customName));
    }

    /**
     * Sets the display's custom name for a specific player.
     * <p>
     * Placeholders and colors will be parsed.
     * Important! Custom name visibility will be reset. Consider using {@link #setCustomName(Component, NameVisibility, Player)} instead.
     * @param customName The display's custom name.
     * @param player The player who will see the custom name.
     */
    default void setCustomName(Component customName, Player player) {
        setCustomName(minimessage.serialize(customName), player);
    }

    /**
     * Sets the display's custom name and its visibility for everyone.
     * <p>
     * Placeholders and colors will be parsed.
     * @param customName The display's custom name.
     * @param visibility The display's custom name visibility.
     */
    void setCustomName(String customName, NameVisibility visibility);

    /**
     * Sets the display's custom name and its visibility for everyone.
     * <p>
     * Placeholders and colors will be parsed.
     * @param customName The display's custom name.
     * @param visibility The display's custom name visibility.
     */
    default void setCustomName(Component customName, NameVisibility visibility) {
        setCustomName(minimessage.serialize(customName), visibility);
    }

    /**
     * Sets the display's custom name and its visibility for everyone.
     * <p>
     * Placeholders and colors will be parsed.
     * @param customName The display's custom name.
     * @param visibility The display's custom name visibility.
     * @param player The player who will see the custom name.
     */
    void setCustomName(String customName, NameVisibility visibility, Player player);

    /**
     * Sets the display's custom name and its visibility for everyone.
     * <p>
     * Placeholders and colors will be parsed.
     * @param customName The display's custom name.
     * @param visibility The display's custom name visibility.
     * @param player The player who will see the custom name.
     */
    default void setCustomName(Component customName, NameVisibility visibility, Player player) {
        setCustomName(minimessage.serialize(customName), visibility, player);
    }

    /**
     * Returns the display's custom name visibility.
     * @return The display's custom name visibility.
     */
    NameVisibility getCustomNameVisibility();

    /**
     * Makes the display's custom name be visible for everyone.
     * @param visibility If the custom name will be visible or not.
     */
    default void setCustomNameVisibility(NameVisibility visibility) {
        setCustomName(getCustomName(), visibility);
    }

    /**
     * Makes the display's custom name be visible for a specific player.
     * @param visibility If the custom name will be visible or not.
     * @param player The player who will see the custom name.
     */
    default void setCustomNameVisibility(NameVisibility visibility, Player player) {
        setCustomName(getCustomName(), visibility, player);
    }
}