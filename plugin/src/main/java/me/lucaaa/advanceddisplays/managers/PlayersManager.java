package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.BaseEntity;
import me.lucaaa.advanceddisplays.data.PlayerData;
import me.lucaaa.advanceddisplays.displays.ADTextDisplay;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayersManager {
    private final AdvancedDisplays plugin;
    private final Map<Player, PlayerData> playersData = new HashMap<>();

    public PlayersManager(AdvancedDisplays plugin) {
        this.plugin = plugin;

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            playersData.put(player, new PlayerData(player, plugin));
        }
    }

    public void addPlayer(Player player) {
        if (!playersData.containsKey(player)) playersData.put(player, new PlayerData(player, plugin));
    }

    public void removePlayer(Player player) {
        PlayerData playerData = playersData.remove(player);
        playerData.stopRunnables();
        if (playerData.isEditing()) playerData.finishEditing();
    }

    public PlayerData getPlayerData(Player player) {
        return playersData.get(player);
    }

    public void resetDisplay(Player player, ADTextDisplay display) {
        PlayerData playerData = playersData.get(player);
        if (playerData == null) return;

        playerData.stopRunnable(display);
    }

    public void removeAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            removePlayer(player);
        }
    }

    public void handleDisplayRemoval(BaseEntity display) {
        for (PlayerData player : playersData.values()) {
            if (player.getEditingDisplay() != display) continue;

            player.finishEditing();
        }
    }
}