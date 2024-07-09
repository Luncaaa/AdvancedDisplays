package me.lucaaa.advanceddisplays.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public abstract class Button {
    private final ItemStack item;

    public Button(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public abstract void onClick(InventoryClickEvent event);
    public abstract void onClick(PlayerInteractEvent event);

    public abstract static class InventoryButton extends Button {
        public InventoryButton(ItemStack item) {
            super(item);
        }

        @Override
        public abstract void onClick(InventoryClickEvent event);

        @Override
        public final void onClick(PlayerInteractEvent event) {
            throw new UnsupportedOperationException("This button does not support PlayerInteractEvent");
        }
    }

    public abstract static class PlayerButton extends Button {
        public PlayerButton(ItemStack item) {
            super(item);
        }

        @Override
        public abstract void onClick(PlayerInteractEvent event);

        @Override
        public final void onClick(InventoryClickEvent event) {
            throw new UnsupportedOperationException("This button does not support InventoryClickEvent");
        }
    }
}
