package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.enums.EditorItem;
import me.lucaaa.advanceddisplays.inventory.InventoryMethods;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.*;
import java.util.logging.Level;

public class InventoryManager {
    private final PlayersManager playersManager;
    private final List<EditorItem> disabledItems = new ArrayList<>();
    private final Map<Player, InventoryMethods> openGUIs = new HashMap<>();

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

    public void handleOpen(Player player, InventoryMethods gui) {
        gui.onOpen();
        player.openInventory(gui.getInventory());
        openGUIs.put(player, gui);
        playersManager.getPlayerData(player).setOpenInventory(gui);
    }

    public void handleClick(InventoryClickEvent event) {
        if (playersManager.getPlayerData((Player) event.getWhoClicked()).isEditing() && Objects.requireNonNull(event.getClickedInventory()).getType() == InventoryType.PLAYER) {
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
    }

    public List<EditorItem> getDisabledItems() {
        return disabledItems;
    }
}