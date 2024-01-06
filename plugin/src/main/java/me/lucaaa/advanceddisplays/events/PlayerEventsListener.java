package me.lucaaa.advanceddisplays.events;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.displays.ADBaseDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

@SuppressWarnings("unused")
public class PlayerEventsListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        AdvancedDisplays.displaysManager.spawnDisplays(event.getPlayer());
        AdvancedDisplays.apiDisplays.getDisplays().forEach(display -> ((ADBaseDisplay) display).spawnToPlayer(event.getPlayer()));
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        AdvancedDisplays.displaysManager.spawnDisplays(event.getPlayer());
        AdvancedDisplays.apiDisplays.getDisplays().forEach(display -> ((ADBaseDisplay) display).spawnToPlayer(event.getPlayer()));
    }
}