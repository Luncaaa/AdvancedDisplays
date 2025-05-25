package me.lucaaa.advanceddisplays.api.actions;

import me.lucaaa.advanceddisplays.api.displays.EntityDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

/**
 * Contains a method that will be executed when a display created by the API is clicked.
 */
public interface DisplayActions {
    /**
     * The code to run when a display is clicked.
     * @param player The player who clicked the display.
     * @param clickType Button used to click the display and whether the player is crouching.
     * @param display The display that was clicked.
     */
    void onClick(Player player, ClickType clickType, EntityDisplay display);
}