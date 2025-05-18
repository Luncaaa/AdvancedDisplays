package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.api.displays.enums.EditorItem;
import me.lucaaa.advanceddisplays.data.EditingPlayer;
import me.lucaaa.advanceddisplays.inventory.InventoryMethods;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.*;
import java.util.logging.Level;

public class InventoryManager {
    private final AdvancedDisplays plugin;
    private final List<EditorItem> disabledItems = new ArrayList<>();
    private final ConfigManager savesConfig;
    private final Map<Player, InventoryMethods> openGUIs = new HashMap<>();
    private final Map<Player, EditingPlayer> editingData = new HashMap<>();

    public InventoryManager(AdvancedDisplays plugin, ConfigManager mainConfig, ConfigManager savesConfig) {
        this.plugin = plugin;
        this.savesConfig = savesConfig;

        if (!mainConfig.getConfig().isList("disabledItems")) {
            mainConfig.getConfig().set("disabledItems", List.of());
            mainConfig.getConfig().setComments("disabledItems", List.of(
                    " List of disabled settings in the editor menu. Visit the link below for a list of settings that can be disabled.",
                    "https://javadoc.jitpack.io/com/github/Luncaaa/AdvancedDisplays/main-SNAPSHOT/javadoc/me/lucaaa/advanceddisplays/api/displays/enums/EditorItem.html"
            ));
            mainConfig.save();
        } else {
            for (String item : mainConfig.getConfig().getStringList("disabledItems")) {
                try {
                    EditorItem disabledItem = EditorItem.valueOf(item.toUpperCase());
                    disabledItems.add(disabledItem);
                } catch(IllegalArgumentException exception) {
                    plugin.log(Level.WARNING, "Invalid item found in the \"disabledItems\" section in the config.yml file: " + item);
                }
            }
        }
    }

    public void handleOpen(Player player, InventoryMethods gui, BaseDisplay display) {
        gui.onOpen();
        player.openInventory(gui.getInventory());
        openGUIs.put(player, gui);
        if (editingData.containsKey(player)) {
            editingData.get(player).setEditingInventory(gui);
        } else {
            addEditingPlayer(player, display);
        }
    }

    public void handleClick(InventoryClickEvent event) {
        if (editingData.containsKey((Player) event.getWhoClicked()) && Objects.requireNonNull(event.getClickedInventory()).getType() == InventoryType.PLAYER) {
            event.setCancelled(true);
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (!openGUIs.containsKey(player)) return;

        openGUIs.get(player).onClick(event);
    }

    public void handleClose(Player player) {
        if (!openGUIs.containsKey(player)) return;

        openGUIs.remove(player).onClose(player);
    }

    public void clearAll() {
        for (Player player : openGUIs.keySet()) {
            player.closeInventory();
        }
        openGUIs.clear();

        for (EditingPlayer player : editingData.values()) {
            player.finishEditing();
        }
        editingData.clear();
    }

    public void addEditingPlayer(Player player, BaseDisplay display) {
        if (editingData.containsKey(player)) editingData.get(player).finishEditing();
        editingData.put(player, new EditingPlayer(plugin, savesConfig, player, display));
    }

    public void removeEditingPlayer(Player player) {
        editingData.remove(player);
    }

    public boolean isPlayerEditing(Player player) {
        return editingData.containsKey(player);
    }

    public EditingPlayer getEditingPlayer(Player player) {
        return editingData.get(player);
    }

    public void handleRemoval(BaseDisplay display) {
        for (EditingPlayer player : editingData.values()) {
            if (player.getEditingDisplay() != display) continue;

            player.finishEditing();
        }
    }

    /**
     * Handles edition
     * @param player The player who edited
     * @param input The edition made
     */
    public void handleChatEdit(Player player, String input) {
        if (!editingData.containsKey(player) || !editingData.get(player).isChatEditing()) return;

        editingData.get(player).handleChatEdit(player, input);
    }

    public List<EditorItem> getDisabledItems() {
        return disabledItems;
    }
}