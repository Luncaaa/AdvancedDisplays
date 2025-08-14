package me.lucaaa.advanceddisplays.data;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.BaseEntity;
import me.lucaaa.advanceddisplays.api.displays.enums.EditorItem;
import me.lucaaa.advanceddisplays.displays.ADTextDisplay;
import me.lucaaa.advanceddisplays.displays.AnimatedTextRunnable;
import me.lucaaa.advanceddisplays.inventory.inventories.PlayerEditorInv;
import me.lucaaa.advanceddisplays.managers.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PlayerData {
    private final AdvancedDisplays plugin;
    private final Player player;
    private final ConfigManager savesConfig;
    private BaseEntity editingDisplay;
    private ItemStack[] savedInventory;
    private PlayerEditorInv playerEditorInventory;
    private boolean isChatEditing = false;
    private final Map<ADTextDisplay, AnimatedTextRunnable> runnables = new HashMap<>();

    public PlayerData(Player player, AdvancedDisplays plugin) {
        this.plugin = plugin;
        this.player = player;
        this.savesConfig = plugin.getSavesConfig();
    }

    public void startEditing(BaseEntity display, List<EditorItem> disabledSettings) {
        if (isEditing()) finishEditing();

        this.editingDisplay = display;
        this.savedInventory = player.getInventory().getContents();

        YamlConfiguration config = savesConfig.getConfig();
        if (!config.contains("saved")) config.createSection("saved");
        ConfigurationSection saved = Objects.requireNonNull(config.getConfigurationSection("saved"));

        Map<Integer, ItemStack> items = new HashMap<>();
        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            if (player.getInventory().getItem(i) == null) continue;

            items.put(i, player.getInventory().getItem(i));
        }
        saved.set(player.getName(), items);
        savesConfig.save();

        this.playerEditorInventory = new PlayerEditorInv(plugin, player, display, disabledSettings);
    }

    public void finishEditing() {
        player.closeInventory();
        player.getInventory().setContents(savedInventory);
        this.editingDisplay = null;

        YamlConfiguration config = savesConfig.getConfig();
        if (!config.contains("saved")) config.createSection("saved");
        Objects.requireNonNull(config.getConfigurationSection("saved")).set(player.getName(), null);
        savesConfig.save();
    }

    public boolean isEditing() {
        return editingDisplay != null;
    }

    public void setChatEditing(boolean chatEditing) {
        isChatEditing = chatEditing;
    }

    public boolean isChatEditing() {
        return isChatEditing;
    }

    public void handleClick(PlayerInteractEvent event) {
        playerEditorInventory.handleClick(player.getInventory().getHeldItemSlot(), event);
    }

    public BaseEntity getEditingDisplay() {
        return editingDisplay;
    }

    public AnimatedTextRunnable getRunnable(AdvancedDisplays plugin, ADTextDisplay display) {
        AnimatedTextRunnable runnable = (runnables.containsKey(display)) ? runnables.get(display) : new AnimatedTextRunnable(plugin, display, player);
        // No need to call for the "start" method since "setPage" already does that.
        if (!runnables.containsKey(display)) runnables.put(display, runnable);
        return runnable;
    }

    public void stopRunnable(ADTextDisplay display) {
        if (runnables.containsKey(display)) {
            runnables.remove(display).stop();
        }
    }

    /**
     * Resets the player's view of the displays.
     */
    public void stopRunnables() {
        for (AnimatedTextRunnable runnable : runnables.values()) {
            runnable.stop();
        }
        runnables.clear();
    }
}