package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.enums.EditorItem;
import me.lucaaa.advanceddisplays.data.PlayerData;
import me.lucaaa.advanceddisplays.inventory.ADInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.*;
import java.util.logging.Level;

public class InventoryManager {
    private final PlayersManager playersManager;
    private final List<EditorItem> disabledItems = new ArrayList<>();
    private final Map<Player, ADInventory> openGUIs = new HashMap<>();

    public InventoryManager(AdvancedDisplays plugin, ConfigManager mainConfig) {
        this.playersManager = plugin.getPlayersManager();

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

    public void handleOpen(Player player, ADInventory gui) {
        gui.onOpen();
        player.openInventory(gui.getInventory());
        openGUIs.put(player, gui);
    }

    public void handleClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getClickedInventory().getType() == InventoryType.PLAYER) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (!openGUIs.containsKey(player)) return;

        openGUIs.get(player).onClick(event);
    }

    public void handleClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (!openGUIs.containsKey(player) || !event.getInventory().equals(openGUIs.get(player).getInventory())) return;

        if (playersManager.getPlayerData(player).isChatEditing()) return; // Don't run on close method if the inventory closed for chat edition
        openGUIs.remove(player).onClose(player);
    }

    /**
     * Removes the players from the map and list if he quits.
     * @param player The player who quit.
     */
    public void onQuit(Player player) {
        openGUIs.remove(player);
    }

    public void handleChatEdit(Player player, String input) {
        PlayerData playerData = playersManager.getPlayerData(player);
        if (!playerData.isChatEditing()) return;

        ADInventory openGUI = openGUIs.get(player);
        boolean stopChatEditing = openGUI.handleChatEdit(player, input);
        if (stopChatEditing) {
            playerData.setChatEditing(false);
            handleOpen(player, openGUI);
        }
    }

    public void clearAll() {
        for (Player player : openGUIs.keySet()) {
            player.closeInventory();
        }
        openGUIs.clear();
    }

    public List<EditorItem> getDisabledItems() {
        return disabledItems;
    }
}