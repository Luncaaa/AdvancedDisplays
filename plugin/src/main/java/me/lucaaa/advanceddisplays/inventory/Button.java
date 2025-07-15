package me.lucaaa.advanceddisplays.inventory;

import me.lucaaa.advanceddisplays.inventory.items.Item;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class Button<T extends Item<?>> {
    private final T item;

    public Button(T item) {
        this.item = item;
    }

    public T getItem() {
        return item;
    }

    public abstract void onClick(InventoryClickEvent event);
    public abstract void onClick(PlayerInteractEvent event);

    public abstract static class InventoryButton<T extends Item<?>> extends Button<T> {
        public InventoryButton(T item) {
            super(item);
        }

        @Override
        public abstract void onClick(InventoryClickEvent event);

        @Override
        public final void onClick(PlayerInteractEvent event) {
            throw new UnsupportedOperationException("This button does not support PlayerInteractEvent");
        }
    }

    public static class Unclickable<T extends Item<?>> extends InventoryButton<T> {
        public Unclickable(T item) {
            super(item);
        }

        @Override
        public final void onClick(InventoryClickEvent event) {}
    }

    public abstract static class PlayerButton<T extends Item<?>> extends Button<T> {
        public PlayerButton(T item) {
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