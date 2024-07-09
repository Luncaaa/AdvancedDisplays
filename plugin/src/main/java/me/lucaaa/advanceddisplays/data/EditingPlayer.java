package me.lucaaa.advanceddisplays.data;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.inventory.InventoryMethods;
import me.lucaaa.advanceddisplays.inventory.inventories.PlayerInv;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EditingPlayer {
    private final AdvancedDisplays plugin;
    private final Player player;
    private final BaseDisplay editingDisplay;
    private final ItemStack[] savedInventory;
    private final PlayerInv editorInventory;
    private InventoryMethods editingInventory;
    private boolean isChatEditing = false;

    public EditingPlayer(AdvancedDisplays plugin, Player player, BaseDisplay display) {
        this.plugin = plugin;
        this.player = player;
        this.editingDisplay = display;
        this.savedInventory = player.getInventory().getContents();
        this.editorInventory = new PlayerInv(plugin, player, display);
    }

    public void setEditingInventory(InventoryMethods editingInventory) {
        this.editingInventory = editingInventory;
    }

    public void setChatEditing(boolean chatEditing) {
        this.isChatEditing = chatEditing;
    }

    public boolean isChatEditing() {
        return this.isChatEditing;
    }

    public void handleChatEdit(Player player, String input) {
        this.editingInventory.handleChatEdit(player, input);
    }

    public void handleClick(PlayerInteractEvent event) {
        this.editorInventory.handleClick(player.getInventory().getHeldItemSlot(), event);
    }

    public void finishEditing() {
        this.player.getInventory().setContents(savedInventory);
        plugin.getInventoryManager().removeEditingPlayer(this.player);
    }

    public BaseDisplay getEditingDisplay() {
        return this.editingDisplay;
    }
}