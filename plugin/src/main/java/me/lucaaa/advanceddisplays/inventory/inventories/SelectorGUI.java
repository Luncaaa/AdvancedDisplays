package me.lucaaa.advanceddisplays.inventory.inventories;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.data.Utils;
import me.lucaaa.advanceddisplays.inventory.ADInventory;
import me.lucaaa.advanceddisplays.inventory.Button;
import me.lucaaa.advanceddisplays.inventory.items.Item;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A GUI with all options from an Enum so that the player can select one of them.
 */
public class SelectorGUI<T> extends ADInventory {
    private final ADInventory previous;
    private final T[] values;
    private final Function<T, ItemStack> getItem;
    private final Consumer<T> onSelect;
    private final Runnable afterClosing;

    public SelectorGUI(AdvancedDisplays plugin, ADInventory previous, T[] values, Function<T, ItemStack> getItem, Consumer<T> onSelect, Runnable afterClosing) {
        super(plugin, Bukkit.createInventory(null, 54, Utils.getColoredText(("&6Banner editor"))), List.of());

        this.previous = previous;
        this.values = values;
        this.getItem = getItem;
        this.onSelect = onSelect;
        this.afterClosing = afterClosing;
    }

    @Override
    public void decorate() {
        int slot = 0;
        for (T value : values) {
            String title = (value instanceof Keyed) ? ((Keyed) value).getKey().getKey().toUpperCase() : value.toString();
            addButton(slot, new Button.InventoryButton<>(new Item.ClickableItem(getItem.apply(value), title, List.of("Click to select"), null)) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    onSelect.accept(value);
                    onClose((Player) event.getWhoClicked());
                }
            });

            slot++;
        }

        super.decorate();
    }

    @Override
    public void onClose(Player player) {
        // The task is run so that the InventoryCloseEvent is fully run before opening a new inventory.
        // Otherwise, the inventory will open but won't be registered as a plugin's GUI.
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getInventoryManager().handleOpen(player, previous);

                // Same as the comment before: run the method in a runnable so that "InventoryCloseEvent" is fully run.
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        afterClosing.run();
                    }
                }.runTask(plugin);
            }
        }.runTask(plugin);
    }
}