package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.utils.DisplayType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;

public class ADItemDisplay extends BaseDisplay implements DisplayMethods {
    private ConfigurationSection settings;
    private Material material;
    private ItemDisplay.ItemDisplayTransform itemTransformation;

    public ADItemDisplay(ConfigManager configManager, ItemDisplay display) {
        super(DisplayType.ITEM, configManager, display);
        this.settings = this.config.getConfigurationSection("settings");

        if (this.settings != null) {
            this.material = Material.valueOf(this.settings.getString("item"));
            this.itemTransformation = ItemDisplay.ItemDisplayTransform.valueOf(this.settings.getString("itemTransformation"));
        }
    }

    @Override
    public void sendMetadataPackets(Player player) {
        this.sendBaseMetadataPackets(player);
        this.packets.setItem(this.displayId, this.material, player);
        this.packets.setItemDisplayTransformation(this.displayId, this.itemTransformation, player);
    }

    public ADItemDisplay create(Material item) {
        this.settings = this.config.createSection("settings");
        this.setMaterial(item);
        this.setItemTransformation(ItemDisplay.ItemDisplayTransform.FIXED);
        return this;
    }

    public Material getMaterial() {
        return this.material;
    }
    public void setMaterial(Material material) {
        this.material = material;
        this.settings.set("item", material.name());
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.packets.setItem(this.displayId, material, onlinePlayer);
        }
        this.save();
    }

    private ItemDisplay.ItemDisplayTransform  getItemTransformation() {
        return this.itemTransformation;
    }
    public void setItemTransformation(ItemDisplay.ItemDisplayTransform transformation) {
        this.itemTransformation = transformation;
        this.settings.set("itemTransformation", transformation.name());
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.packets.setItemDisplayTransformation(this.displayId, transformation, onlinePlayer);
        }
        this.save();
    }
}
