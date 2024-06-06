package me.lucaaa.advanceddisplays.events;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.displays.ADBaseDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
        plugin.getApiDisplays().getDisplays().forEach(display -> ((ADBaseDisplay) display).spawnToPlayer(event.getPlayer()));
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        plugin.getDisplaysManager().spawnDisplays(event.getPlayer());
        plugin.getApiDisplays().getDisplays().forEach(display -> ((ADBaseDisplay) display).spawnToPlayer(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        plugin.getPacketsManager().remove(event.getPlayer());
    }
}