package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.data.DisplayHeadType;
import me.lucaaa.advanceddisplays.data.Compatibility;
import me.lucaaa.advanceddisplays.data.HeadUtils;
import me.lucaaa.advanceddisplays.data.Utils;
import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.managers.DisplaysManager;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class ADItemDisplay extends ADBaseDisplay implements me.lucaaa.advanceddisplays.api.displays.ItemDisplay {
    private ItemStack item;
    private DisplayHeadType displayHeadType;
    private String displayHeadValue;
    private boolean enchanted;
    private ItemDisplay.ItemDisplayTransform itemTransformation;

    // Compatibility
    private String oraxenId;
    private String itemsAdderId;

    public ADItemDisplay(AdvancedDisplays plugin, DisplaysManager displaysManager, ConfigManager configManager, String name) {
        super(plugin, displaysManager, configManager, name, DisplayType.ITEM, EntityType.ITEM_DISPLAY);

        if (settings != null) {
            if (settings.isString("oraxen") && plugin.isIntegrationLoaded(Compatibility.ORAXEN)) {
                this.oraxenId = settings.getString("oraxen");
                this.item = plugin.getIntegration(Compatibility.ORAXEN).getItemStack(oraxenId);
            } else if (settings.isString("itemsAdder") && plugin.isIntegrationLoaded(Compatibility.ORAXEN)) {
                this.itemsAdderId = settings.getString("itemsAdder");
                this.item = plugin.getIntegration(Compatibility.ITEMS_ADDER).getItemStack(itemsAdderId);
            } else {
                String material = config.getOrDefault("item", Material.BARRIER.name(), settings);
                try {
                    this.item = new ItemStack(Material.valueOf(material));
                    Utils.loadItemData(item, settings, getLocation().getWorld(), plugin);
                } catch (IllegalArgumentException e) {
                    errors.add("Invalid material set: " + material);
                }
            }

            // The Utils.loadItemData method already set the enchantments
            this.enchanted = !item.getEnchantments().isEmpty();
            this.itemTransformation = ItemDisplay.ItemDisplayTransform.valueOf(config.getOrDefault("itemTransformation", ItemDisplay.ItemDisplayTransform.FIXED.name(), settings));

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

    public ADItemDisplay(AdvancedDisplays plugin, DisplaysManager displaysManager, String name, Location location, boolean saveToConfig) {
        super(plugin, displaysManager, name, location, DisplayType.ITEM, EntityType.ITEM_DISPLAY, saveToConfig);
    }

    @Override
    public void sendMetadataPackets(Player player) {
        super.sendMetadataPackets(player);
        if (item.getType() == Material.PLAYER_HEAD) {
            setHead(displayHeadType, displayHeadValue, player, enchanted);
        } else {
            packets.setMetadata(entityId, player, metadata.ITEM, item);
        }
        packets.setMetadata(entityId, player, metadata.ITEM_TRANSFORM, (byte) itemTransformation.ordinal());
    }

    public ADItemDisplay create(Material item) {
        create();
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
            // The "enchanted" setting is set in the Utils#saveItemData method.

            Utils.saveItemData(item, settings, plugin.getNmsVersion());
            save();
        }

        this.enchanted = !item.getEnchantments().isEmpty();

        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            setItem(item, onlinePlayer);
        }
    }
    @Override
    public void setItem(ItemStack item, Player player) {
        packets.setMetadata(entityId, player, metadata.ITEM, item);
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

        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            setBase64Head(base64, onlinePlayer);
        }
    }

    @Override
    public void setBase64Head(String base64, Player player) {
        setHead(DisplayHeadType.BASE64, base64, player, enchanted);
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

        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            setPlayerHead(playerName, onlinePlayer);
        }
    }

    @Override
    public void setPlayerHead(String playerName, Player player) {
        setHead(DisplayHeadType.PLAYER, playerName, player, enchanted);
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
        if (enchanted) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.addEnchant(Enchantment.MENDING, 1, true);
                item.setItemMeta(meta);
            }
        } else {
            for (Enchantment enchantment : item.getEnchantments().keySet()) {
                item.removeEnchantment(enchantment);
            }
        }
        if (config != null) {
            settings.set("enchanted", enchanted);
            save();
        }
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            // Calling setEnchanted(boolean, Player) would create a clone for every player which would actually
            // be the same since the item will be enchanted for everyone. Because of that, it'll be more efficient
            // to send the already-enchanted item to every online player.
            setItem(item, onlinePlayer);
        }

    }
    @Override
    public void setEnchanted(boolean enchanted, Player player) {
        if (item.getType() == Material.PLAYER_HEAD) {
            setHead(displayHeadType, displayHeadValue, player, enchanted);
        } else {
            ItemStack clone = item.clone();
            if (enchanted) clone.addUnsafeEnchantment(Enchantment.MENDING, 1);
            packets.setMetadata(entityId, player, metadata.ITEM, clone);
        }
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
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            setItemTransformation(transformation, onlinePlayer);
        }
    }
    @Override
    public void setItemTransformation(ItemDisplay.ItemDisplayTransform transformation, Player player) {
        packets.setMetadata(entityId, player, metadata.ITEM_TRANSFORM, (byte) transformation.ordinal());
    }

    // TODO: Extend head caching system here
    // When the class is initialized, save the head to the cache and then grab it so that players don't see
    // the loading head if not necessary.
    /*
    Pseudocode:

    public ADItemDisplay() {
        if (type == HEAD) HeadCache.save(this, HeadUtils.getHead(...));
    }


    private void setHead() {
        CompletableFuture future = HeadCache.getHead(this);

        if future is completed, grab the head. If it isn't set the loading head until it is.
    }
     */
    private void setHead(DisplayHeadType type, String value, Player player, boolean enchanted) {
        packets.setMetadata(entityId, player, metadata.ITEM, plugin.cachedHeads.LOADING);

        // Run async because of the HTTP request to parse the head.
        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack head = HeadUtils.getHead(type, value, enchanted, player, plugin);
                packets.setMetadata(entityId, player, metadata.ITEM, head);
            }
        }.runTaskAsynchronously(plugin);
    }
}