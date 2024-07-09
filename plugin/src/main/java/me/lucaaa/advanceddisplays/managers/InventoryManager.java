package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.data.EditingPlayer;
import me.lucaaa.advanceddisplays.inventory.InventoryMethods;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InventoryManager {
    private final AdvancedDisplays plugin;
    private final Map<Player, InventoryMethods> openGUIs = new HashMap<>();
    private final Map<Player, EditingPlayer> editingData = new HashMap<>();

    public InventoryManager(AdvancedDisplays plugin) {
        this.plugin = plugin;
    }

    public void handleOpen(Player player, InventoryMethods gui, BaseDisplay display) {
        gui.onOpen();
        player.openInventory(gui.getInventory());
        this.openGUIs.put(player, gui);
        if (this.editingData.containsKey(player)) {
            this.editingData.get(player).setEditingInventory(gui);
        } else {
            this.addEditingPlayer(player, display);
        }
    }

    public void handleClick(InventoryClickEvent event) {
        if (editingData.containsKey((Player) event.getWhoClicked()) && Objects.requireNonNull(event.getClickedInventory()).getType() == InventoryType.PLAYER) {
            event.setCancelled(true);
            return;
        }

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

        for (EditingPlayer player : this.editingData.values()) {
            player.finishEditing();
        }
        this.editingData.clear();
    }

    public void addEditingPlayer(Player player, BaseDisplay display) {
        if (this.editingData.containsKey(player)) this.editingData.get(player).finishEditing();
        this.editingData.put(player, new EditingPlayer(plugin, player, display));
    }

    public void removeEditingPlayer(Player player) {
        this.editingData.remove(player);
    }

    public boolean isPlayerNotEditing(Player player) {
        return !this.editingData.containsKey(player);
    }

    public EditingPlayer getEditingPlayer(Player player) {
        return this.editingData.get(player);
    }

    public void handleRemoval(BaseDisplay display) {
        for (EditingPlayer player : this.editingData.values()) {
            if (player.getEditingDisplay() != display) continue;

            player.finishEditing();
        }
    }

    /**
     * Handles edition
     * @param player The player who edited
     * @param input The edition made
     */
    public void handleChatEdit(Player player, String input) {
        if (!this.editingData.containsKey(player) || !this.editingData.get(player).isChatEditing()) return;

        this.editingData.get(player).handleChatEdit(player, input);
    }
}
