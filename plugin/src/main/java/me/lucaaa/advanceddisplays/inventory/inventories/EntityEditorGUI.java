package me.lucaaa.advanceddisplays.inventory.inventories;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.BaseEntity;
import me.lucaaa.advanceddisplays.api.displays.enums.EditorItem;
import me.lucaaa.advanceddisplays.api.displays.enums.NameVisibility;
import me.lucaaa.advanceddisplays.data.Utils;
import me.lucaaa.advanceddisplays.inventory.Button;
import me.lucaaa.advanceddisplays.inventory.InventoryMethods;
import me.lucaaa.advanceddisplays.inventory.items.EditorItems;
import me.lucaaa.advanceddisplays.inventory.items.Item;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class EntityEditorGUI extends InventoryMethods {
    private final BaseEntity display;
    private final DisplayEditorGUI previous;
    private final EditorItems items;
    private final List<Player> editingPlayers = new ArrayList<>();

    public EntityEditorGUI(AdvancedDisplays plugin, BaseEntity display, List<EditorItem> disabledSettings, DisplayEditorGUI previous) {
        super(plugin, Bukkit.createInventory(null, 27, Utils.getColoredText(("&6Editing " + display.getType().name() + " display: &e" + display.getName()))), disabledSettings);
        this.display = display;
        this.previous = previous;
        this.items = new EditorItems(display);
    }

    @Override
    public void decorate() {
        // ----[ FIRE & SPRINT ]----
        addIfAllowed(EditorItem.ON_FIRE, 0, new Button.InventoryButton<>(items.ON_FIRE) {
            @Override
            public void onClick(InventoryClickEvent event) {
                boolean newValue = getItem().changeValue();
                getInventory().setItem(0, getItem().getStack());
                display.setOnFire(newValue);
            }
        });

        addIfAllowed(EditorItem.SPRINTING, 9, new Button.InventoryButton<>(items.SPRINTING) {
            @Override
            public void onClick(InventoryClickEvent event) {
                boolean newValue = getItem().changeValue();
                getInventory().setItem(9, getItem().getStack());
                display.setSprinting(newValue);
            }
        });
        // ----------

        // ----[ CUSTOM NAME ]----
        addIfAllowed(EditorItem.CUSTOM_NAME, 1, new Button.InventoryButton<>(items.CUSTOM_NAME) {
            @Override
            public void onClick(InventoryClickEvent event) {
                editingPlayers.add((Player) event.getWhoClicked());
                plugin.getPlayersManager().getPlayerData((Player) event.getWhoClicked()).setChatEditing(true);
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().sendMessage(plugin.getMessagesManager().getColoredMessage("&6Enter the new custom name or \"cancel\" to keep the current one."));
            }
        });

        addIfAllowed(EditorItem.CUSTOM_NAME_VISIBILITY, 10, new Button.InventoryButton<>(items.CUSTOM_NAME_VISIBILITY) {
            @Override
            public void onClick(InventoryClickEvent event) {
                NameVisibility newValue = getItem().changeValue();
                getInventory().setItem(10, getItem().getStack());
                display.setCustomNameVisibility(newValue);
            }
        });
        // ----------

        // ----[ GLOW ]-----
        addIfAllowed(EditorItem.GLOW_TOGGLE, 2, new Button.InventoryButton<>(items.GLOW_TOGGLE) {
            @Override
            public void onClick(InventoryClickEvent event) {
                boolean newValue = getItem().changeValue();
                getInventory().setItem(2, getItem().getStack());
                display.setGlowing(newValue);
            }
        });

        addIfAllowed(EditorItem.GLOW_TOGGLE, 11, new Button.InventoryButton<>(items.GLOW_COLOR) {
            @Override
            public void onClick(InventoryClickEvent event) {
                ChatColor newValue = getItem().changeValue();
                while (!newValue.isColor()) {
                    newValue = getItem().changeValue();
                }
                getInventory().setItem(11, getItem().getStack());
                display.setGlowColor(newValue);
            }
        });
        // ----------

        // ----[ LOCATION ]-----
        addIfAllowed(EditorItem.TELEPORT, 18, new Button.InventoryButton<>(items.TELEPORT) {
            @Override
            public void onClick(InventoryClickEvent event) {
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().teleport(display.getLocation());
            }
        });

        addIfAllowed(EditorItem.MOVE_HERE, 19, new Button.InventoryButton<>(items.MOVE_HERE) {
            @Override
            public void onClick(InventoryClickEvent event) {
                event.getWhoClicked().closeInventory();
                display.setLocation(event.getWhoClicked().getLocation());
                Location loc = event.getWhoClicked().getLocation();
                String location = BigDecimal.valueOf(loc.getX()).setScale(2, RoundingMode.HALF_UP).doubleValue() + ";" + BigDecimal.valueOf(loc.getY()).setScale(2, RoundingMode.HALF_UP).doubleValue() + ";" + BigDecimal.valueOf(loc.getZ()).setScale(2, RoundingMode.HALF_UP).doubleValue();
                getItem().setValue(location);
                getInventory().setItem(19, getItem().getStack());
            }
        });

        addIfAllowed(EditorItem.CENTER, 20, new Button.InventoryButton<>(items.CENTER) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Location loc = display.center();
                String location = loc.getX() + ";" + loc.getY() + ";" + loc.getZ();
                getItem().setValue(location);
                getInventory().setItem(20, getItem().getStack());
            }
        });
        // ----------

        addIfAllowed(EditorItem.REMOVE, 13, new Button.InventoryButton<>(items.REMOVE) {
            @Override
            public void onClick(InventoryClickEvent event) {
                plugin.getDisplaysManager().removeDisplay(display.getName());
                event.getWhoClicked().closeInventory();
            }
        });

        super.decorate();
    }

    @Override
    public void handleChatEdit(Player player, String input) {
        if (!input.equalsIgnoreCase("cancel")) {
            display.setCustomName(input);
            @SuppressWarnings("unchecked")
            Item<String> item = (Item<String>) getButton(1).getItem();
            item.setValue(input);
            getInventory().setItem(1, item.getStack());
        }

        editingPlayers.remove(player);
        plugin.getInventoryManager().handleOpen(player, this);
    }

    @Override
    public void onClose(Player player) {
        if (previous != null && !editingPlayers.contains(player) && !display.isRemoved()) {
            // The task is run so that the InventoryCloseEvent is fully run before opening a new inventory.
            // Otherwise, the inventory will open but won't be registered as a plugin's GUI.
            new BukkitRunnable() {
                @Override
                public void run() {
                    previous.updateGlowToggleItem(); // Both inventories have a glow toggle button
                    plugin.getInventoryManager().handleOpen(player, previous);
                }
            }.runTask(plugin);
        }
    }
}