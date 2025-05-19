package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.common.utils.DisplayHeadType;
import me.lucaaa.advanceddisplays.data.Compatibility;
import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.managers.DisplaysManager;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class ADItemDisplay extends ADBaseDisplay implements me.lucaaa.advanceddisplays.api.displays.ItemDisplay {
    private ConfigurationSection settings = null;
    private ItemStack item;
    private DisplayHeadType displayHeadType;
    private String displayHeadValue;
    private boolean enchanted;
    private ItemDisplay.ItemDisplayTransform itemTransformation;

    // Compatibility
    private String oraxenId;
    private String itemsAdderId;

    public ADItemDisplay(AdvancedDisplays plugin, DisplaysManager displaysManager, ConfigManager configManager, String name, ItemDisplay display) {
        super(plugin, displaysManager, name, DisplayType.ITEM, configManager, display);
        settings = config.getSection("settings", false);

        if (settings != null) {
            if (settings.isString("oraxen") && plugin.isIntegrationLoaded(Compatibility.ORAXEN)) {
                this.oraxenId = settings.getString("oraxen");
                this.item = plugin.getIntegration(Compatibility.ORAXEN).getItemStack(oraxenId);
            } else if (settings.isString("itemsAdder") && plugin.isIntegrationLoaded(Compatibility.ORAXEN)) {
                this.itemsAdderId = settings.getString("itemsAdder");
                this.item = plugin.getIntegration(Compatibility.ITEMS_ADDER).getItemStack(itemsAdderId);
            } else {
                this.item = new ItemStack(Material.valueOf(settings.getString("item")));
                loadData(item, settings);
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

    public ADItemDisplay(AdvancedDisplays plugin, DisplaysManager displaysManager, String name, ItemDisplay display, boolean saveToConfig) {
        super(plugin, displaysManager, name, DisplayType.ITEM, display, saveToConfig);
    }

    @Override
    public void sendMetadataPackets(Player player) {
        super.sendMetadataPackets(player);
        if (item.getType() == Material.PLAYER_HEAD) packets.setHead(displayId, enchanted, displayHeadType, displayHeadValue, player);
        else packets.setItem(displayId, item, enchanted, player);
        packets.setItemDisplayTransformation(displayId, itemTransformation, player);
    }

    public ADItemDisplay create(Material item) {
        if (config != null) settings = config.getConfig().createSection("settings");
        if (item == Material.PLAYER_HEAD) setViewerHead();
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

            saveData(item, settings);
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
    public void setBase64Head(String base64) {
        item = new ItemStack(Material.PLAYER_HEAD);
        displayHeadType = DisplayHeadType.BASE64;
        displayHeadValue = base64;
        if (config != null) {
            settings.set("item", item.getType().name());
            ConfigurationSection headSection =  settings.createSection("head");
            headSection.set(displayHeadType.getConfigName(), base64);
            save();
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setBase64Head(base64, onlinePlayer);
        }
    }

    @Override
    public void setBase64Head(String base64, Player player) {
        packets.setHead(displayId, enchanted, displayHeadType, base64, player);
    }

    @Override
    public void setPlayerHead(String playerName) {
        item = new ItemStack(Material.PLAYER_HEAD);
        displayHeadType = DisplayHeadType.PLAYER;
        displayHeadValue = playerName;
        if (config != null) {
            settings.set("item", item.getType().name());
            ConfigurationSection headSection =  settings.createSection("head");
            headSection.set(displayHeadType.getConfigName(), playerName);
            save();
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setPlayerHead(playerName, onlinePlayer);
        }
    }

    @Override
    public void setPlayerHead(String playerName, Player player) {
        packets.setHead(displayId, enchanted, displayHeadType, playerName, player);
    }

    @Override
    public void setViewerHead() {
        setPlayerHead("%player%");
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

    @SuppressWarnings("UnstableApiUsage")
    private void saveData(ItemStack item, ConfigurationSection settings) {
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        int customModelData = (meta.hasCustomModelData()) ? meta.getCustomModelData() : 0;
        settings.set("customModelData", customModelData);

        if (meta instanceof PotionMeta potion && potion.getColor() != null) {
            settings.set("color",
                    potion.getColor().getRed() + ";" +
                    potion.getColor().getGreen() + ";" +
                    potion.getColor().getBlue());

        } else if (meta instanceof ArmorMeta armor) {
            if (armor.getTrim() != null) {
                settings.set("trim", null); // Deletes the setting if present

            } else {
                settings.set("trim", armor.getTrim().getPattern().getKey() + ":" + armor.getTrim().getMaterial().getKey());
            }

            if (meta instanceof LeatherArmorMeta leatherArmor) {
                settings.set("color",
                        leatherArmor.getColor().getRed() + ";" +
                        leatherArmor.getColor().getGreen() + ";" +
                        leatherArmor.getColor().getBlue());
            }

        } else if (meta instanceof BannerMeta banner) {
            List<String> patterns = new ArrayList<>();

            for (Pattern pattern : banner.getPatterns()) {
                patterns.add(pattern.getPattern().name() + ":" + pattern.getColor().name());
            }

            settings.set("patterns", patterns);

        } else if (meta instanceof CompassMeta compass) {
            if (compass.getLodestone() == null) {
                settings.set("lodestone", null); // Deletes the setting if present

            } else {
                Location lodestone = compass.getLodestone();
                settings.set("lodestone", lodestone.getX() + ";" + lodestone.getY() + ";" + lodestone.getZ());
            }

        } else if (meta instanceof BundleMeta bundle) {
            settings.set("hasItems", bundle.hasItems());
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    private void loadData(ItemStack item, ConfigurationSection settings) {
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

        int customModelData = settings.getInt("customModelData");
        if (customModelData > 0) {
            meta.setCustomModelData(customModelData);
        }

        if (meta instanceof PotionMeta potion && settings.isString("color")) {
            String[] saved = settings.getString("color", "255;255;255").split(";");
            Color color = Color.fromRGB(Integer.parseInt(saved[0]), Integer.parseInt(saved[1]), Integer.parseInt(saved[2]));
            potion.setColor(color);

        } else if (meta instanceof ArmorMeta armor) {
            if (settings.isString("trim")) {
                String[] trim = settings.getString("trim", "sentry:netherite").toLowerCase().split(":");
                TrimMaterial material = Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(trim[1]));
                TrimPattern pattern = Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(trim[0]));

                if (material == null || pattern == null) {
                    plugin.log(Level.WARNING, "Invalid armor trim for item display " + getName());
                } else {
                    armor.setTrim(new ArmorTrim(material, pattern));
                }
            }

            if (meta instanceof LeatherArmorMeta leatherArmor && settings.isString("color")) {
                String[] saved = settings.getString("color", "255;255;255").split(";");
                Color color = Color.fromRGB(Integer.parseInt(saved[0]), Integer.parseInt(saved[1]), Integer.parseInt(saved[2]));
                leatherArmor.setColor(color);
            }

        } else if (meta instanceof BannerMeta banner && settings.isList("patterns")) {
            List<String> patterns = settings.getStringList("patterns");

            for (String configPattern : patterns) {
                String[] parts = configPattern.split(":");

                PatternType pattern = PatternType.valueOf(parts[0]);
                DyeColor color = DyeColor.valueOf(parts[1]);

                banner.addPattern(new Pattern(color, pattern));
            }

        } else if (meta instanceof CompassMeta compass && settings.isString("lodestone")) {
            String[] location = settings.getString("lodestone", "0.0;0.0;0.0").split(";");
            Location lodestone = new Location(getLocation().getWorld(),
                    Double.parseDouble(location[0]),
                    Double.parseDouble(location[1]),
                    Double.parseDouble(location[1]));
            compass.setLodestone(lodestone);

        } else if (meta instanceof BundleMeta bundle) {
            if (settings.getBoolean("hasItems")) {
                bundle.addItem(new ItemStack(Material.DIAMOND));
            }
        }

        item.setItemMeta(meta);
    }
}