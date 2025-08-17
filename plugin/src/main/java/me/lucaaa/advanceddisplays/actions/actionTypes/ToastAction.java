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

public class ToastAction extends Action {
    private final ItemStack item;
    private final String title;
    private final String description;
    private final AdvancementDisplayType frame;
    private final boolean animate;

    public ToastAction(AdvancedDisplays plugin, ConfigurationSection actionSection, BaseEntity display) {
        super(
                plugin,
                ActionType.TOAST,
                actionSection,
                List.of(
                        new Field("item", ConfigurationSection.class),
                        new Field("title", String.class),
                        new Field("description", String.class),
                        new Field("frame", String.class)
                )
        );

        ConfigurationSection itemSection = actionSection.getConfigurationSection("item");
        if (itemSection == null) {
            this.item = new ItemStack(Material.BARRIER);
        } else {
            Material material;
            try {
                material = Material.valueOf(itemSection.getString("material"));
            } catch (IllegalArgumentException exception) {
                material = Material.BARRIER;
                errors.add("Invalid material: " + actionSection.getString("material"));
            }
            this.item = Utils.loadItemData(new ItemStack(material), itemSection, display.getLocation().getWorld(), plugin);
        }

        this.title = actionSection.getString("title", "No message set");
        this.description = actionSection.getString("description", "No message set");

        AdvancementDisplayType frame;
        try {
            frame = AdvancementDisplayType.valueOf(actionSection.getString("frame"));
        } catch (IllegalArgumentException exception) {
            frame = AdvancementDisplayType.TASK;
            errors.add("Invalid frame: " + actionSection.getString("frame"));
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