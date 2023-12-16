package me.lucaaa.advanceddisplays.api.displays;

import me.lucaaa.advanceddisplays.api.displays.enums.DisplayHeadType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;

public interface ItemDisplay extends BaseDisplay {
    /**
     * Gets the displayed item.
     * @return The displayed item.
     */
    Material getMaterial();
    /**
     * Sets the displayed item for everyone.
     * @param material The new displayed item.
     */
    void setMaterial(Material material);
    /**
     * Sets the displayed item for a specific player.
     * @param material The new displayed item.
     * @param player The player who will see the new displayed item.
     */
    void setMaterial(Material material, Player player);

    /**
     * Sets a head as the displayed item with a texture for everyone.
     * @param displayHeadType What the head will show: a player skin or a base64 texture.
     * @param value The name of a player or a base64 texture value.
     */
    void setMaterialHead(DisplayHeadType displayHeadType, String value);
    /**
     * Sets a head as the displayed item with a texture for a specific player.
     * @param displayHeadType What the head will show: a player skin or a base64 texture.
     * @param value The name of a player or a base64 texture value.
     * @param player The player who will see the head.
     */
    void setMaterialHead(DisplayHeadType displayHeadType, String value, Player player);

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
