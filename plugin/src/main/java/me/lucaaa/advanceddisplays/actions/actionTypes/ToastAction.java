package me.lucaaa.advanceddisplays.actions.actionTypes;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.actions.Action;
import me.lucaaa.advanceddisplays.api.displays.BaseEntity;
import me.lucaaa.advanceddisplays.api.util.ComponentSerializer;
import me.lucaaa.advanceddisplays.data.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.advancement.AdvancementDisplayType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.logging.Level;

public class ToastAction extends Action {
    private final AdvancedDisplays plugin;
    private final ItemStack item;
    private final String title;
    private final String description;
    private final AdvancementDisplayType frame;
    private final boolean animate;

    public ToastAction(AdvancedDisplays plugin, ConfigurationSection actionSection, BaseEntity display) {
        super(plugin, List.of("item", "title", "description", "frame"), actionSection);
        this.plugin = plugin;

        ConfigurationSection itemSection = actionSection.getConfigurationSection("item");
        if (itemSection == null) {
            plugin.log(Level.WARNING, "Missing \"item\" section for toast action! Creating an empty one...");
            itemSection = actionSection.createSection("item");
            isCorrect = false;
        }

        Material material;
        try {
            material = Material.valueOf(itemSection.getString("material"));
        } catch (IllegalArgumentException exception) {
            material = Material.BARRIER;
            plugin.log(Level.WARNING, "Invalid material found on action \"" + actionSection.getName() + "\": " + actionSection.getString("material"));
            this.isCorrect = false;
        }
        this.item = Utils.loadItemData(new ItemStack(material), itemSection, display.getLocation().getWorld(), plugin);

        this.title = actionSection.getString("title", "No message set");
        this.description = actionSection.getString("description", "No message set");

        AdvancementDisplayType frame;
        try {
            frame = AdvancementDisplayType.valueOf(actionSection.getString("frame"));
        } catch (IllegalArgumentException exception) {
            frame = AdvancementDisplayType.TASK;
            plugin.log(Level.WARNING, "Invalid frame type found on action \"" + actionSection.getName() + "\": " + actionSection.getString("frame"));
            this.isCorrect = false;
        }
        this.frame = frame;

        this.animate = actionSection.getBoolean("animate", true);
        int customModelData = actionSection.getInt("customModelData", 0);

        if (customModelData > 0) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setCustomModelData(customModelData);
                item.setItemMeta(meta);
            }
        }
    }

    @Override
    public void runAction(Player clickedPlayer, Player actionPlayer) {
        Component title = getText(this.title, clickedPlayer, actionPlayer);
        Component description = getText(this.description, clickedPlayer, actionPlayer);

        if (animate) {
            title = Utils.combine(title, description);
        }

        plugin.getPacketsManager().getPackets().sendToast(
                plugin,
                actionPlayer,
                item,
                ComponentSerializer.toJSONString(title),
                ComponentSerializer.toJSONString(description),
                frame
        );
    }
}