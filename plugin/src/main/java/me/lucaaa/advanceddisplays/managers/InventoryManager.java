package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.api.displays.enums.EditorItem;
import me.lucaaa.advanceddisplays.common.utils.Logger;
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
                    this.disabledItems.add(disabledItem);
                } catch(IllegalArgumentException exception) {
                    Logger.log(Level.WARNING, "Invalid item found in the \"disabledItems\" section in the config.yml file: " + item);
                }
            }
        }
    }

    public void handleOpen(Player player, InventoryMethods gui, BaseDisplay display) {
        gui.onOpen();
        player.openInventory(gui.getInventory());
        this.openGUIs.put(player, gui);
        if (this.editingData.containsKey(player)) {
            this.editingData.get(player).setEditingInventory(gui);
        } else {
            this.addEditingPlayer(player, gui.getDisabledSettings(), display);
        }
    }

    public void handleClick(InventoryClickEvent event) {
        if (editingData.containsKey((Player) event.getWhoClicked()) && Objects.requireNonNull(event.getClickedInventory()).getType() == InventoryType.PLAYER) {
            event.setCancelled(true);
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (!this.openGUIs.containsKey(player)) return;

        this.openGUIs.get(player).onClick(event);
    }

    public void handleClose(Player player) {
        if (!this.openGUIs.containsKey(player)) return;

        this.openGUIs.remove(player).onClose(player);
    }

    public void clearAll() {
        for (Player player : this.openGUIs.keySet()) {
            player.closeInventory();
        }
        this.openGUIs.clear();

        for (EditingPlayer player : this.editingData.values()) {
            player.finishEditing();
        }
        this.editingData.clear();
    }

    public void addEditingPlayer(Player player, List<EditorItem> disabledItems, BaseDisplay display) {
        if (this.editingData.containsKey(player)) this.editingData.get(player).finishEditing();
        this.editingData.put(player, new EditingPlayer(plugin, savesConfig, player, disabledItems, display));
    }

    public void removeEditingPlayer(Player player) {
        this.editingData.remove(player);
    }

    public boolean isPlayerNotEditing(Player player) {
        return !this.editingData.containsKey(player);
    }

    public EditingPlayer getEditingPlayer(Player player) {
        return this.editingData.get(player);
    }

    public void handleRemoval(BaseDisplay display) {
        for (EditingPlayer player : this.editingData.values()) {
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
        if (!this.editingData.containsKey(player) || !this.editingData.get(player).isChatEditing()) return;

        this.editingData.get(player).handleChatEdit(player, input);
    }

    public List<EditorItem> getDisabledItems() {
        return this.disabledItems;
    }
}