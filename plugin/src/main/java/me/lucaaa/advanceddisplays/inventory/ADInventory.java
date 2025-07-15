package me.lucaaa.advanceddisplays.inventory;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.enums.EditorItem;
import me.lucaaa.advanceddisplays.data.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public abstract class ADInventory {
    protected final AdvancedDisplays plugin;
    private final Inventory inventory;
    protected final List<EditorItem> disabledSettings;
    private final Map<Integer, Button<?>> buttons = new HashMap<>();
    private boolean loaded = false;
    protected final ItemStack filler;
    protected static final List<Integer> metadataSlots = List.of(8, 7, 6, 17, 16, 15, 26, 25, 24);

    public ADInventory(AdvancedDisplays plugin, Inventory inventory, List<EditorItem> disabledSettings) {
        this.plugin = plugin;
        this.inventory = inventory;
        this.disabledSettings = disabledSettings;

        this.filler = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
        ItemMeta meta = Objects.requireNonNull(filler.getItemMeta());
        meta.setDisplayName(" ");
        Utils.hideFlags(meta);
        filler.setItemMeta(meta);
    }

    public void onOpen() {
        if (loaded) return;
        decorate();
        loaded = true;
    }

    public void onClose(Player player) {}

    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);

        if (!buttons.containsKey(event.getSlot())) return;
        buttons.get(event.getSlot()).onClick(event);
    }

    protected void addButton(int slot, Button<?> button) {
        buttons.put(slot, button);
        inventory.setItem(slot, button.getItem().getStack());
    }

    protected void removeButton(int slot) {
        getInventory().setItem(slot, null);
        buttons.remove(slot);
    }

    protected void clearButtons() {
        for (int slot : buttons.keySet()) {
            getInventory().setItem(slot, null);
        }
        buttons.clear();
    }

    protected Button<?> getButton(int slot) {
        return buttons.get(slot);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void decorate() {
        for (int i = 0; i < getInventory().getSize(); i++) {
            if (getInventory().getItem(i) != null) continue;

            getInventory().setItem(i, filler);
        }
    }

    public void handleChatEdit(Player player, String input) {}

    protected void addIfAllowed(EditorItem requirement, int slot, Button.InventoryButton<?> button) {
        if (!disabledSettings.contains(requirement)) {
            addButton(slot, button);
            return;
        }

        button.getItem().disable(List.of());

        addButton(slot, new Button.Unclickable<>(button.getItem()));
    }
}