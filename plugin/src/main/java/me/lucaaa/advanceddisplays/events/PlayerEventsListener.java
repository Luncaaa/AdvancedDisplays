package me.lucaaa.advanceddisplays.events;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.ADAPIImplementation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings("unused")
public class PlayerEventsListener implements Listener {
    private final AdvancedDisplays plugin;

    public PlayerEventsListener(AdvancedDisplays plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getPacketsManager().add(event.getPlayer());
        plugin.getDisplaysManager().spawnDisplays(event.getPlayer());
        for (ADAPIImplementation implementation : plugin.getApiDisplays().getApiMap().values()) {
            implementation.getDisplaysManager().spawnDisplays(event.getPlayer());
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        plugin.getDisplaysManager().spawnDisplays(event.getPlayer());
        for (ADAPIImplementation implementation : plugin.getApiDisplays().getApiMap().values()) {
            implementation.getDisplaysManager().spawnDisplays(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        plugin.getPacketsManager().remove(event.getPlayer());
        if (plugin.getInventoryManager().isPlayerNotEditing(event.getPlayer())) return;
        plugin.getInventoryManager().getEditingPlayer(event.getPlayer()).finishEditing();
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (plugin.getInventoryManager().isPlayerNotEditing(event.getPlayer())) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getInventoryManager().handleChatEdit(event.getPlayer(), event.getMessage());
            }
        }.runTask(plugin);
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (plugin.getInventoryManager().isPlayerNotEditing(event.getPlayer()) || event.getHand() == EquipmentSlot.OFF_HAND) return;

        event.setCancelled(true);
        plugin.getInventoryManager().getEditingPlayer(event.getPlayer()).handleClick(event);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (plugin.getInventoryManager().isPlayerNotEditing(event.getPlayer())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (plugin.getInventoryManager().isPlayerNotEditing(player)) return;

        event.setCancelled(true);
    }
}