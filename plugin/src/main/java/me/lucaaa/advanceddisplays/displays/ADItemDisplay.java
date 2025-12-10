package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.data.DisplayHeadType;
import me.lucaaa.advanceddisplays.data.HeadUtils;
import me.lucaaa.advanceddisplays.data.Utils;
import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.managers.DisplaysManager;
import me.lucaaa.advancedlinks.common.ITask;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ADItemDisplay extends ADBaseDisplay implements me.lucaaa.advanceddisplays.api.displays.ItemDisplay {
    private ItemStack item;
    private DisplayHeadType displayHeadType;
    private String displayHeadValue;
    private boolean enchanted;
    private ItemDisplay.ItemDisplayTransform itemTransformation;
    private final Map<Player, ITask> headLoadTasks = new HashMap<>();

    public ADItemDisplay(AdvancedDisplays plugin, DisplaysManager displaysManager, ConfigManager configManager, String name) {
        super(plugin, displaysManager, configManager, name, DisplayType.ITEM, EntityType.ITEM_DISPLAY);

        if (settings != null) {
            String material = config.getOrDefault("item", Material.BARRIER.name(), settings);
            try {
                this.item = new ItemStack(Material.valueOf(material));
                Utils.loadItemData(item, settings, getLocation().getWorld(), plugin);
            } catch (IllegalArgumentException e) {
                errors.add("Invalid material set: " + material);
            }

            // The Utils.loadItemData method already set the enchantments
            this.enchanted = !item.getEnchantments().isEmpty();
            this.itemTransformation = ItemDisplay.ItemDisplayTransform.valueOf(config.getOrDefault("itemTransformation", ItemDisplay.ItemDisplayTransform.FIXED.name(), settings));

            if (item.getType() == Material.PLAYER_HEAD && settings.contains("head")) {
                ConfigurationSection headSection = settings.getConfigurationSection("head");
                if (Objects.requireNonNull(headSection).contains("player"))  {
                    this.displayHeadType = DisplayHeadType.PLAYER;
                    this.displayHeadValue = headSection.getString("player");

                } else {
                    this.displayHeadType = DisplayHeadType.BASE64;
                    this.displayHeadValue = headSection.getString("base64");
                }

                this.item = plugin.getHeadCacheManager().LOADING;
                if (!displayHeadValue.equalsIgnoreCase("%player%")) {
                    plugin.getHeadCacheManager().loadHead(this, displayHeadType, displayHeadValue);
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
        if (displayHeadType == DisplayHeadType.PLAYER && displayHeadValue.equalsIgnoreCase("%player%")) {
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

        // Head cache manager will call the setHead method
        plugin.getHeadCacheManager().loadHead(this,  displayHeadType, displayHeadValue);
    }

    @Override
    public void setBase64Head(String base64, Player player) {
        setHead(DisplayHeadType.BASE64, base64, player, false);
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
            setHead(DisplayHeadType.PLAYER, playerName, onlinePlayer, true);
        }
    }

    @Override
    public void setPlayerHead(String playerName, Player player) {
        setHead(DisplayHeadType.PLAYER, playerName, player, false);
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
        ItemStack clone = item.clone();
        if (enchanted) clone.addUnsafeEnchantment(Enchantment.MENDING, 1);
        packets.setMetadata(entityId, player, metadata.ITEM, clone);
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

    @Override
    public void remove() {
        super.remove();
        plugin.getHeadCacheManager().cancelTask(this);

        for (ITask task : headLoadTasks.values()) {
            task.cancel();
        }
        headLoadTasks.clear();
    }

    /**
     * Used by the HeadCacheManager, sets the head once it's done loading.
     * @param item The head to set.
     */
    public void setHead(ItemStack item) {
        if (enchanted) item.addUnsafeEnchantment(Enchantment.MENDING, 1);
        this.item = item;

        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            setItem(item, onlinePlayer);
        }
    }

    /**
     * Sets the displayed head.
     * @param type The type.
     * @param value The value.
     * @param player The player whom it will be set for.
     * @param useCache True if the head was cached (global head or saved to config). False if the head was not cached (head for specific player).
     */
    private void setHead(DisplayHeadType type, String value, Player player, boolean useCache) {
        this.item = plugin.getHeadCacheManager().LOADING;
        packets.setMetadata(entityId, player, metadata.ITEM, item);

        if (type == DisplayHeadType.PLAYER && value.equalsIgnoreCase("%player%")) {
            ITask task = plugin.getTasksManager().runTaskAsynchronously(plugin, () -> {
                ItemStack head = HeadUtils.getPlayerHead(player.getName(), plugin);
                if (enchanted) head.addUnsafeEnchantment(Enchantment.MENDING, 1);
                packets.setMetadata(entityId, player, metadata.ITEM, head);
                headLoadTasks.remove(player);
            });
            headLoadTasks.put(player, task);

            return;
        }

        if (!useCache) {
           ITask task = plugin.getTasksManager().runTaskAsynchronously(plugin, () -> {
                ItemStack head;
                if (type == DisplayHeadType.PLAYER) {
                    head = HeadUtils.getPlayerHead(value, plugin);
                } else {
                    head = HeadUtils.getBase64Head(value, plugin);
                }

                if (enchanted) head.addUnsafeEnchantment(Enchantment.MENDING, 1);
                packets.setMetadata(entityId, player, metadata.ITEM, head);
                headLoadTasks.remove(player);
            });
            headLoadTasks.put(player, task);
        }
    }
}