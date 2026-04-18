package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.BaseEntity;
import me.lucaaa.advanceddisplays.api.displays.enums.EditorItem;
import me.lucaaa.advanceddisplays.data.EditorData;
import me.lucaaa.advanceddisplays.data.PlayerData;
import me.lucaaa.advanceddisplays.displays.ADTextDisplay;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PlayersManager {
    private final AdvancedDisplays plugin;
    private final Map<Player, PlayerData> playersData = new HashMap<>();
    private final Map<Player, EditorData> editorsData = new HashMap<>();

    public PlayersManager(AdvancedDisplays plugin) {
        this.plugin = plugin;
    }

    public void removePlayer(Player player) {
        PlayerData playerData = playersData.remove(player);
        if (playerData != null) playerData.stopRunnables();

        EditorData editorData = editorsData.remove(player);
        if (editorData != null) editorData.finishEditing();
    }

    public void resetDisplay(Player player, ADTextDisplay display) {
        PlayerData playerData = playersData.get(player);
        if (playerData == null) return;

        playerData.stopRunnable(display);
    }

    public void removeAll() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            removePlayer(player);
        }
    }

    public void handleDisplayRemoval(BaseEntity display) {
        editorsData.entrySet().removeIf(entry -> {
           EditorData editorData = entry.getValue();

           if (editorData.getEditingDisplay() == display) {
               editorData.finishEditing();
               return true;
           }

           return false;
        });

        if (display instanceof ADTextDisplay textDisplay) {
            for (PlayerData playerData : playersData.values()) {
                playerData.stopRunnable(textDisplay);
            }
        }
    }

    public void startEditing(Player player, BaseEntity display, List<EditorItem> disabledSettings) {
        editorsData.computeIfAbsent(player, p -> new EditorData(player, plugin)).startEditing(display, disabledSettings);
    }

    public EditorData getEditor(HumanEntity player) {
        if (!(player instanceof Player)) return null;

        return editorsData.get((Player) player);
    }

    public boolean finishEditing(Player player) {
        EditorData editorData = editorsData.remove(player);
        if (editorData == null) return false;
        editorData.finishEditing();
        return true;
    }

    public PlayerData getOrCreatePlayerData(Player player) {
        return playersData.computeIfAbsent(player, p -> new PlayerData(player));
    }

    public void runIfPlayerRegistered(Player player, Consumer<PlayerData> toRun) {
        PlayerData playerData = playersData.get(player);
        if (playerData == null) return;
        toRun.accept(playerData);
    }
}