package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.utils.DisplayType;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;

public class ADItemDisplay extends BaseDisplay {
    private final ItemDisplay display;

    private Material material;
    private ItemDisplay.ItemDisplayTransform itemTransformation;

    public ADItemDisplay(ConfigManager configManager, ItemDisplay display) {
        super(DisplayType.ITEM, configManager, display);
        this.display = display;

        if (this.config.getString("item") != null) {
            this.material = Material.valueOf(this.config.getString("item"));
            this.display.setItemStack(new ItemStack(this.material));
        }

        if (this.config.getString("itemTransformation") != null) {
            this.itemTransformation = ItemDisplay.ItemDisplayTransform.valueOf(this.config.getString("itemTransformation"));
            this.setItemTransformation(this.itemTransformation);
        }
    }

    public ADItemDisplay create(Material item) {
        this.setMaterial(item);
        this.setItemTransformation(ItemDisplay.ItemDisplayTransform.FIXED);
        return this;
    }

    public Material getMaterial() {
        return this.material;
    }
    public void setMaterial(Material material) {
        this.material = material;
        this.config.set("item", material.name());
        this.display.setItemStack(new ItemStack(material));
        this.save();
    }

    private ItemDisplay.ItemDisplayTransform  getItemTransformation() {
        return this.itemTransformation;
    }
    public void setItemTransformation(ItemDisplay.ItemDisplayTransform transformation) {
        this.itemTransformation = transformation;
        this.config.set("itemTransformation", transformation.name());
        this.display.setItemDisplayTransform(transformation);
        this.save();
    }
}
