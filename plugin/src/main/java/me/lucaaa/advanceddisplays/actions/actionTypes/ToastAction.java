package me.lucaaa.advanceddisplays.actions.actionTypes;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.actions.Action;
import me.lucaaa.advanceddisplays.api.util.ComponentSerializer;
import me.lucaaa.advanceddisplays.data.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.advancement.AdvancementDisplayType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.logging.Level;

public class ToastAction extends Action {
    private final AdvancedDisplays plugin;
    private final ItemStack item;
    private final String title;
    private final String description;
    private final AdvancementDisplayType frame;
    private final boolean animate;

    public ToastAction(AdvancedDisplays plugin, ConfigurationSection actionSection) {
        super(plugin, List.of("material", "enchanted", "title", "description", "frame", "animate"), actionSection);
        this.plugin = plugin;

        Material material;
        try {
            material = Material.valueOf(actionSection.getString("material"));
        } catch (IllegalArgumentException exception) {
            material = Material.AIR;
            plugin.log(Level.WARNING, "Invalid material found on action \"" + actionSection.getName() + "\": " + actionSection.getString("material"));
            this.isCorrect = false;
        }
        this.item = new ItemStack(material);
        if (actionSection.getBoolean("enchanted")) {
            item.addUnsafeEnchantment(Enchantment.MENDING, 1);
        }

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

        this.animate = actionSection.getBoolean("animate");
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
                ComponentSerializer.toJSON(title),
                ComponentSerializer.toJSON(description),
                frame
        );
    }
}