package me.lucaaa.advanceddisplays.api.displays;

import org.bukkit.entity.Player;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.inventory.ItemStack;

/**
 * An entity which displays an item.
 */
@SuppressWarnings("unused")
public interface ItemDisplay extends BaseDisplay {
    /**
     * Gets the displayed item.
     * @return The displayed item.
     */
    ItemStack getItem();
    /**
     * Sets the displayed item for everyone.
     * @param item The new displayed item.
     */
    void setItem(ItemStack item);
    /**
     * Sets the displayed item for a specific player.
     * <p>
     * This will also change the value returned by {@link #isEnchanted()}
     * @param item The new displayed item.
     * @param player The player who will see the new displayed item.
     */
    void setItem(ItemStack item, Player player);

    /**
     * Sets a head as the displayed item with a texture for everyone.
     * @param base64 The base64 texture value.
     */
    void setBase64Head(String base64);
    /**
     * Sets a head as the displayed item with a texture for a specific player.
     * @param base64 The base64 texture value.
     * @param player The player who will see the head.
     */
    void setBase64Head(String base64, Player player);
    /**
     * Sets a head as the displayed item with a player skin for everyone.
     * @param playerName The name of the displayed player.
     */
    void setPlayerHead(String playerName);
    /**
     * Sets a head as the displayed item with a player skin for a specific player.
     * @param playerName The name of the displayed player.
     * @param player The player who will see the head.
     */
    void setPlayerHead(String playerName, Player player);
    /**
     * Sets the head texture to the skin of the player who is viewing it.
     */
    void setViewerHead();

    /**
     * Gets whether the display item is enchanted.
     * @return If the displayed item is enchanted.
     */
    boolean isEnchanted();
    /**
     * Makes the displayed item be enchanted or not for everyone.
     * @param enchanted Whether the item is enchanted.
     */
    void setEnchanted(boolean enchanted);
    /**
     * Makes the displayed item be enchanted or not for everyone.
     * @param enchanted Whether the item is enchanted.
     * @param player The player who will see the item enchanted or not.
     */
    void setEnchanted(boolean enchanted, Player player);

    /**
     * Gets the item's transformation.
     * @return The item's transformation.
     */
    ItemDisplayTransform getItemTransformation();
    /**
     * Sets the item's transformation for everyone.
     * @param transformation The new transformation.
     */
    void setItemTransformation(ItemDisplayTransform transformation);
    /**
     * Sets the item's transformation for everyone.
     * @param transformation The new transformation.
     * @param player The player who will see the new transformation.
     */
    void setItemTransformation(ItemDisplayTransform transformation, Player player);
}