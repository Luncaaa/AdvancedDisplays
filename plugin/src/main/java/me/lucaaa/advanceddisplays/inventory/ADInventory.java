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
    protected final ADInventory previous;
    protected boolean shouldOpenPrevious = true;
    protected final Runnable onDone;

    protected static final ItemStack FILLER;
    protected static final List<Integer> METADATA_SLOTS = List.of(8, 7, 6, 17, 16, 15, 26, 25, 24);

    static {
        FILLER = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
        ItemMeta meta = Objects.requireNonNull(FILLER.getItemMeta());
        meta.setDisplayName(" ");
        Utils.hideFlags(meta);
        FILLER.setItemMeta(meta);
    }

    public ADInventory(AdvancedDisplays plugin, Inventory inventory, List<EditorItem> disabledSettings, ADInventory previous) {
        this(plugin, inventory, disabledSettings, previous, null);
    }

    public ADInventory(AdvancedDisplays plugin, Inventory inventory, List<EditorItem> disabledSettings, ADInventory previous, Runnable onDone) {
        this.plugin = plugin;
        this.inventory = inventory;
        this.disabledSettings = disabledSettings;
        this.previous = previous;
        this.onDone = onDone;
    }

    public void onOpen() {
        if (loaded) return;
        decorate();
        loaded = true;
    }

    public void onClose(Player player) {
        if (previous != null && shouldOpenPrevious) {
            // The task is run so that the InventoryCloseEvent is fully run before opening a new inventory.
            // Otherwise, the inventory will open but won't be registered as a plugin's GUI.
            plugin.getTasksManager().runTask(plugin, () -> plugin.getInventoryManager().handleOpen(player, previous));
        }
    }

    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);

        Button<?> button = buttons.get(event.getSlot());
        if (button instanceof Button.InventoryButton<?> inventoryButton) {
            inventoryButton.onClick(event);
        }
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

            getInventory().setItem(i, FILLER);
        }
    }

    /**
     * Allows the player to input custom text which may not be possible through GUIs. If true, the GUI will reopen.
     * @param player The player who is chat editing.
     * @param input The text he inputted.
     * @return Whether the player is done editing or not (whether the chat event should call this method again or not)
     */
    public boolean handleChatEdit(Player player, String input) {
        return true;
    }

    protected void addIfAllowed(EditorItem requirement, int slot, Button.InventoryButton<?> button) {
        if (!disabledSettings.contains(requirement)) {
            addButton(slot, button);
            return;
        }

        button.getItem().disable(List.of());

        addButton(slot, new Button.Unclickable<>(button.getItem()));
    }
}