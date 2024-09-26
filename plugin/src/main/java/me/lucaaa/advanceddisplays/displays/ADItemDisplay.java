package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayHeadType;
import me.lucaaa.advanceddisplays.data.Compatibility;
import me.lucaaa.advanceddisplays.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class ADItemDisplay extends ADBaseDisplay implements DisplayMethods, me.lucaaa.advanceddisplays.api.displays.ItemDisplay {
    private ConfigurationSection settings = null;
    private ItemStack item;
    private DisplayHeadType displayHeadType;
    private String displayHeadValue;
    private boolean enchanted;
    private ItemDisplay.ItemDisplayTransform itemTransformation;

    // Compatibility
    private String oraxenId;
    private String itemsAdderId;

    public ADItemDisplay(AdvancedDisplays plugin, ConfigManager configManager, String name, ItemDisplay display, boolean isApi) {
        super(plugin, name, DisplayType.ITEM, configManager, display, isApi);
        settings = config.getConfigurationSection("settings");

        if (settings != null) {
            if (settings.isString("oraxen") && plugin.isIntegrationLoaded(Compatibility.ORAXEN)) {
                this.oraxenId = settings.getString("oraxen");
                this.item = plugin.getIntegration(Compatibility.ORAXEN).getItemStack(oraxenId);
            } else if (settings.isString("itemsAdder") && plugin.isIntegrationLoaded(Compatibility.ORAXEN)) {
                this.itemsAdderId = settings.getString("itemsAdder");
                this.item = plugin.getIntegration(Compatibility.ITEMS_ADDER).getItemStack(itemsAdderId);
            } else {
                this.item = new ItemStack(Material.valueOf(settings.getString("item")));

                int customModelData = settings.getInt("customModelData");
                if (customModelData > 0) {
                    ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
                    meta.setCustomModelData(customModelData);
                    item.setItemMeta(meta);
                }
            }

            this.enchanted = settings.getBoolean("enchanted");
            this.itemTransformation = ItemDisplay.ItemDisplayTransform.valueOf(settings.getString("itemTransformation"));

            if (settings.contains("head")) {
                ConfigurationSection headSection = settings.getConfigurationSection("head");
                if (Objects.requireNonNull(headSection).contains("player"))  {
                    this.displayHeadType = DisplayHeadType.PLAYER;
                    this.displayHeadValue = headSection.getString("player");

                } else {
                    this.displayHeadType = DisplayHeadType.BASE64;
                    this.displayHeadValue = headSection.getString("base64");
                }
            }
        }
    }
    public ADItemDisplay(AdvancedDisplays plugin, String name, ItemDisplay display) {
        super(plugin, name, DisplayType.ITEM, display);
    }

    @Override
    public void sendMetadataPackets(Player player) {
        sendBaseMetadataPackets(player);
        if (item.getType() == Material.PLAYER_HEAD) packets.setHead(displayId, enchanted, displayHeadType, displayHeadValue, player);
        else packets.setItem(displayId, item, enchanted, player);
        packets.setItemDisplayTransformation(displayId, itemTransformation, player);
    }

    public ADItemDisplay create(Material item) {
        if (config != null) settings = config.createSection("settings");
        if (item == Material.PLAYER_HEAD) setMaterialHead(DisplayHeadType.PLAYER, "%player%");
        else setItem(new ItemStack(item));
        setEnchanted(false);
        setItemTransformation(ItemDisplay.ItemDisplayTransform.FIXED);
        return this;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }
    @Override
    public void setItem(ItemStack item) {
        this.item = item;
        if (config != null) {
            if (oraxenId != null) settings.set("oraxen", oraxenId);
            if (itemsAdderId != null) settings.set("itemsAdder", itemsAdderId);
            settings.set("item", item.getType().name());

            ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
            int customModelData = (meta.hasCustomModelData()) ? meta.getCustomModelData() : 0;
            settings.set("customModelData", customModelData);
            save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setItem(item, onlinePlayer);
        }
    }
    @Override
    public void setItem(ItemStack item, Player player) {
        packets.setItem(displayId, item, enchanted, player);
    }

    @Override
    public void setMaterialHead(DisplayHeadType displayHeadType, String value) {
        item = new ItemStack(Material.PLAYER_HEAD);
        this.displayHeadType = displayHeadType;
        displayHeadValue = value;
        if (config != null) {
            settings.set("item", item.getType().name());
            ConfigurationSection headSection =  settings.createSection("head");
            headSection.set(displayHeadType.getConfigName(), value);
            save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setMaterialHead(displayHeadType, value, onlinePlayer);
        }
    }
    @Override
    public void setMaterialHead(DisplayHeadType displayHeadType, String value, Player player) {
        packets.setHead(displayId, enchanted, displayHeadType, value, player);
    }

    @Override
    public boolean isEnchanted() {
        return enchanted;
    }
    @Override
    public void setEnchanted(boolean enchanted) {
        this.enchanted = enchanted;
        if (config != null) {
            settings.set("enchanted", enchanted);
            save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setEnchanted(enchanted, onlinePlayer);
        }

    }
    @Override
    public void setEnchanted(boolean enchanted, Player player) {
        if (item.getType() == Material.PLAYER_HEAD) packets.setHead(displayId, enchanted, displayHeadType, displayHeadValue, player);
        else packets.setItem(displayId, item, enchanted, player);
    }

    @Override
    public ItemDisplay.ItemDisplayTransform getItemTransformation() {
        return itemTransformation;
    }
    @Override
    public void setItemTransformation(ItemDisplay.ItemDisplayTransform transformation) {
        itemTransformation = transformation;
        if (config != null) {
            settings.set("itemTransformation", transformation.name());
            save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setItemTransformation(transformation, onlinePlayer);
        }
    }
    @Override
    public void setItemTransformation(ItemDisplay.ItemDisplayTransform transformation, Player player) {
        packets.setItemDisplayTransformation(displayId, transformation, player);
    }
}
