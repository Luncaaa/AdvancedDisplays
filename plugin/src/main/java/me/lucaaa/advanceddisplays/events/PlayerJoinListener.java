package me.lucaaa.advanceddisplays.events;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        AdvancedDisplays.displaysManager.spawnDisplays(event.getPlayer());
    }
}