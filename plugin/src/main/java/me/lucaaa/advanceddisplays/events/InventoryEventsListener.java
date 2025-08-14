package me.lucaaa.advanceddisplays.events;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;

public class InventoryEventsListener implements Listener {
    private final AdvancedDisplays plugin;

    public InventoryEventsListener(AdvancedDisplays plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        plugin.getInventoryManager().handleClick(event);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        plugin.getInventoryManager().handleClose(event);
    }

    @EventHandler
    public void onCreativeEvent(InventoryCreativeEvent event) {
        if (plugin.getPlayersManager().getPlayerData((Player) event.getWhoClicked()).isEditing()) event.setCancelled(true);
    }
}