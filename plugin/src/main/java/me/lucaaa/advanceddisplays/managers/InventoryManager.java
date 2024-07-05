package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.inventory.InventoryMethods;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;

public class InventoryManager {
    private final Map<Player, InventoryMethods> openGUIs = new HashMap<>();
    private final Map<Player, InventoryMethods> editingData = new HashMap<>();

    public void handleOpen(Player player, InventoryMethods gui) {
        gui.onOpen();
        player.openInventory(gui.getInventory());
        this.openGUIs.put(player, gui);
    }

    public void handleClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!this.openGUIs.containsKey(player)) return;

        this.openGUIs.get(player).onClick(event);
    }

    public void handleClose(Player player) {
        if (!this.openGUIs.containsKey(player)) return;

        this.openGUIs.remove(player).onClose(player);
    }

    public void clearAll() {
        for (Player player : this.openGUIs.keySet()) {
            player.closeInventory();
        }
        this.openGUIs.clear();
    }

    public void addEditingPlayer(Player player, InventoryMethods inventory) {
        this.editingData.put(player, inventory);
    }

    public boolean isPlayerEditing(Player player) {
        return this.editingData.containsKey(player);
    }

    /**
     * Handles edition
     * @param player The player who edited
     * @param input The edition made
     */
    public void handleEdit(Player player, Object input) {
        if (!this.editingData.containsKey(player)) return;

        this.editingData.get(player).handleEdit(player, input);
        this.editingData.remove(player);
    }
}
