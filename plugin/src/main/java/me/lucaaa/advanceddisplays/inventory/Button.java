package me.lucaaa.advanceddisplays.inventory;

import me.lucaaa.advanceddisplays.inventory.items.Item;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class Button {
    private final Item item;

    public Button(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public abstract void onClick(InventoryClickEvent event);
    public abstract void onClick(PlayerInteractEvent event);

    public abstract static class InventoryButton extends Button {
        public InventoryButton(Item item) {
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
        public PlayerButton(Item item) {
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
