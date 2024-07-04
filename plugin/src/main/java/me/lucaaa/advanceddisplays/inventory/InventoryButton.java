package me.lucaaa.advanceddisplays.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryButton {
    private final ItemStack item;

    public InventoryButton(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public abstract void onClick(InventoryClickEvent event);
}
