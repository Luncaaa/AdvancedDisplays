package me.lucaaa.advanceddisplays.inventory;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class InventoryMethods {
    protected final AdvancedDisplays plugin;
    private final Inventory inventory;
    private final Map<Integer, InventoryButton> buttons = new HashMap<>();
    private boolean loaded = false;

    public InventoryMethods(AdvancedDisplays plugin, Inventory inventory) {
        this.plugin = plugin;
        this.inventory = inventory;
    }

    public void onOpen() {
        if (loaded) return;
        this.decorate();
        loaded = true;
    }

    public void onClose(Player player) {}

    public void onClick(InventoryClickEvent event) {
        if (!this.buttons.containsKey(event.getSlot())) return;

        this.buttons.get(event.getSlot()).onClick(event);
    }

    protected void addButton(int slot, InventoryButton button) {
        this.buttons.put(slot, button);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void decorate() {
        this.buttons.forEach((slot, button) -> this.inventory.setItem(slot, button.getItem()));

        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = Objects.requireNonNull(filler.getItemMeta());
        meta.setDisplayName(" ");
        filler.setItemMeta(meta);

        for (int i = 0; i < getInventory().getSize(); i++) {
            if (this.getInventory().getItem(i) != null) continue;

            this.getInventory().setItem(i, filler);
        }
    }
}