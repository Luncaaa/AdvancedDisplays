package me.lucaaa.advanceddisplays.data;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.api.displays.enums.EditorItem;
import me.lucaaa.advanceddisplays.inventory.InventoryMethods;
import me.lucaaa.advanceddisplays.inventory.inventories.PlayerInv;
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

public class EditingPlayer {
    private final ConfigManager savesConfig;
    private final Player player;
    private final BaseDisplay editingDisplay;
    private final ItemStack[] savedInventory;
    private final PlayerInv playerInventory;
    private InventoryMethods openInventory;
    private boolean isChatEditing = false;

    public EditingPlayer(AdvancedDisplays plugin, ConfigManager savesConfig, Player player, BaseDisplay display, List<EditorItem> disabledSettings) {
        this.savesConfig = savesConfig;
        this.player = player;
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

        this.playerInventory = new PlayerInv(plugin, player, display, disabledSettings);
    }

    public void setOpenInventory(InventoryMethods inventory) {
        openInventory = inventory;
    }

    public void setChatEditing(boolean chatEditing) {
        isChatEditing = chatEditing;
    }

    public boolean isChatEditing() {
        return isChatEditing;
    }

    public void handleChatEdit(Player player, String input) {
        openInventory.handleChatEdit(player, input);
    }

    public void handleClick(PlayerInteractEvent event) {
        playerInventory.handleClick(player.getInventory().getHeldItemSlot(), event);
    }

    public void finishEditing() {
        player.getInventory().setContents(savedInventory);

        YamlConfiguration config = savesConfig.getConfig();
        if (!config.contains("saved")) config.createSection("saved");
        Objects.requireNonNull(config.getConfigurationSection("saved")).set(player.getName(), null);
        savesConfig.save();
    }

    public BaseDisplay getEditingDisplay() {
        return editingDisplay;
    }
}