package me.lucaaa.advanceddisplays.events;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.ADAPIImplementation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        boolean isEditing = plugin.getInventoryManager().isPlayerEditing(event.getPlayer());
        if (!isEditing) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getInventoryManager().handleEdit(event.getPlayer(), event.getMessage());
            }
        }.runTask(plugin);
        event.setCancelled(true);
    }
}