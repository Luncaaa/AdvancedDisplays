package me.lucaaa.advanceddisplays.api.conditions;

import me.lucaaa.advanceddisplays.api.displays.EntityDisplay;
import org.bukkit.entity.Player;

/**
 * Represents a condition that the player must meet to see a display.
 */
public interface Condition {
    /**
     * Checks whether a player meets the condition or not.
     * @param display The display that the condition is being checked for.
     * @param player The player to check.
     * @return Whether the player meets the condition or not.
     */
    boolean meetsCondition(EntityDisplay display, Player player);
}