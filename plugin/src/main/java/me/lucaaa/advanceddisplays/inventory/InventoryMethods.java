package me.lucaaa.advanceddisplays.inventory;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.enums.EditorItem;
import me.lucaaa.advanceddisplays.inventory.items.Item;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public abstract class InventoryMethods {
    protected final AdvancedDisplays plugin;
    private final Inventory inventory;
    protected final List<EditorItem> disabledSettings;
    private final Map<Integer, Button<?>> buttons = new HashMap<>();
    private boolean loaded = false;

    public InventoryMethods(AdvancedDisplays plugin, Inventory inventory, List<EditorItem> disabledSettings) {
        this.plugin = plugin;
        this.inventory = inventory;
        this.disabledSettings = disabledSettings;
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

    protected void addIfAllowed(EditorItem requirement, int slot, Button.InventoryButton<?> button) {
        if (!disabledSettings.contains(requirement)) {
            addButton(slot, button);
            return;
        }

        ItemStack itemStack = button.getItem().getStack();
        ItemMeta meta = Objects.requireNonNull(itemStack.getItemMeta());
        List<String> lore = new ArrayList<>();
        for (String line : Objects.requireNonNull(meta.getLore())) {
            if (!line.startsWith(ChatColor.YELLOW + "")) continue;

            lore.add(line);
        }

        lore.remove(lore.size() - 1);
        lore.add(ChatColor.RED + "" + ChatColor.BOLD + "Setting disabled!");
        lore.add(ChatColor.GRAY + "You won't be able to change it");
        lore.add("");
        lore.add(ChatColor.BLUE + "Current value: " + ChatColor.GRAY + button.getItem().getValue());
        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        addButton(slot, new Button.InventoryButton<Item<?>>(button.getItem()) {
            @Override
            public void onClick(InventoryClickEvent event) {}
        });
    }
}