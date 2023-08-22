package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;

public class ADItemDisplay extends BaseDisplay implements DisplayMethods {
    private ConfigurationSection settings = null;
    private Material material;
    private boolean enchanted;
    private ItemDisplay.ItemDisplayTransform itemTransformation;

    public ADItemDisplay(ConfigManager configManager, ItemDisplay display) {
        super(DisplayType.ITEM, configManager, display);
        this.settings = this.config.getConfigurationSection("settings");

        if (this.settings != null) {
            this.material = Material.valueOf(this.settings.getString("item"));
            this.enchanted = this.settings.getBoolean("enchanted");
            this.itemTransformation = ItemDisplay.ItemDisplayTransform.valueOf(this.settings.getString("itemTransformation"));
        }
    }
    public ADItemDisplay(ItemDisplay display) {
        super(DisplayType.ITEM, display);
    }

    @Override
    public void sendMetadataPackets(Player player) {
        this.sendBaseMetadataPackets(player);
        this.packets.setItem(this.displayId, this.material, this.enchanted, player);
        this.packets.setItemDisplayTransformation(this.displayId, this.itemTransformation, player);
    }

    public ADItemDisplay create(Material item) {
        if (this.config != null) this.settings = this.config.createSection("settings");
        this.setMaterial(item);
        this.setEnchanted(false);
        this.setItemTransformation(ItemDisplay.ItemDisplayTransform.FIXED);
        return this;
    }

    public Material getMaterial() {
        return this.material;
    }
    public boolean isEnchanted() {
        return this.enchanted;
    }
    public void setMaterial(Material material) {
        this.material = material;
        if (this.config != null) {
            this.settings.set("item", material.name());
            this.save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.setMaterial(material, onlinePlayer);
        }

    }
    public void setMaterial(Material material, Player player) {
        this.packets.setItem(this.displayId, material, this.enchanted, player);
    }

    public void setEnchanted(boolean enchanted) {
        this.enchanted = enchanted;
        if (this.config != null) {
            this.settings.set("enchanted", enchanted);
            this.save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.setEnchanted(enchanted, onlinePlayer);
        }

    }
    public void setEnchanted(boolean enchanted, Player player) {
        this.packets.setItem(this.displayId, this.material, enchanted, player);
    }

    private ItemDisplay.ItemDisplayTransform  getItemTransformation() {
        return this.itemTransformation;
    }
    public void setItemTransformation(ItemDisplay.ItemDisplayTransform transformation) {
        this.itemTransformation = transformation;
        if (this.config != null) {
            this.settings.set("itemTransformation", transformation.name());
            this.save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.setItemTransformation(transformation, onlinePlayer);
        }
    }
    public void setItemTransformation(ItemDisplay.ItemDisplayTransform transformation, Player player) {
        this.packets.setItemDisplayTransformation(this.displayId, transformation, player);
    }
}
