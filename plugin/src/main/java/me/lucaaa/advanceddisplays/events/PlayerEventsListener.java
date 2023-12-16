package me.lucaaa.advanceddisplays.events;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

@SuppressWarnings("unused")
public class PlayerEventsListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        AdvancedDisplays.displaysManager.spawnDisplays(event.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        AdvancedDisplays.displaysManager.spawnDisplays(event.getPlayer());
    }
}