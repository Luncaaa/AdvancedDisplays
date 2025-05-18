package me.lucaaa.advanceddisplays.inventory;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.enums.EditorItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class InventoryMethods {
    protected final AdvancedDisplays plugin;
    protected final List<EditorItem> disabledSettings;
    private final Inventory inventory;
    private final Map<Integer, Button<?>> buttons = new HashMap<>();
    private boolean loaded = false;

    public InventoryMethods(AdvancedDisplays plugin, Inventory inventory) {
        this.plugin = plugin;
        this.inventory = inventory;
        this.disabledSettings = plugin.getInventoryManager().getDisabledItems();
    }

    public void onOpen() {
        if (loaded) return;
        decorate();
        loaded = true;
    }

    public void onClose(Player player) {}

    public void onClick(InventoryClickEvent event) {
        if (!buttons.containsKey(event.getSlot())) return;

        buttons.get(event.getSlot()).onClick(event);
    }

    protected void addButton(int slot, Button<?> button) {
        buttons.put(slot, button);
    }

    protected Button<?> getButton(int slot) {
        return buttons.get(slot);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void decorate() {
        buttons.forEach((slot, button) -> inventory.setItem(slot, button.getItem().getStack()));

        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = Objects.requireNonNull(filler.getItemMeta());
        meta.setDisplayName(" ");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        filler.setItemMeta(meta);

        for (int i = 0; i < getInventory().getSize(); i++) {
            if (getInventory().getItem(i) != null) continue;

            getInventory().setItem(i, filler);
        }
    }

    public void handleChatEdit(Player player, String input) {}
}