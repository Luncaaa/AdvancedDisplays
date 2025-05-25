package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.actions.ActionsHandler;
import me.lucaaa.advanceddisplays.actions.actionTypes.ActionType;
import me.lucaaa.advanceddisplays.api.actions.DisplayActions;
import me.lucaaa.advanceddisplays.api.displays.EntityDisplay;
import me.lucaaa.advanceddisplays.api.displays.BlockDisplay;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.api.displays.enums.EditorItem;
import me.lucaaa.advanceddisplays.api.displays.visibility.VisibilityManager;
import me.lucaaa.advanceddisplays.data.Ticking;
import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.managers.DisplaysManager;
import me.lucaaa.advanceddisplays.nms_common.PacketInterface;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.ClickType;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class ADEntityDisplay extends Ticking implements EntityDisplay {
    protected final AdvancedDisplays plugin;
    private final DisplaysManager displaysManager;
    protected final PacketInterface packets;
    protected final ConfigManager config;
    private final ActionsHandler actionsHandler;
    private final ADVisibilityManager visibilityManager;

    protected ConfigurationSection entitySection = null;

    private final String name;
    protected final DisplayType type;
    protected Entity entity;
    protected final EntityType entityType;
    protected int entityId;
    private final boolean isApi;
    private boolean isRemoved = false;

    protected Location location;
    protected float yaw;
    protected float pitch;
    protected boolean isGlowing;
    protected ChatColor glowColor;

    public ADEntityDisplay(AdvancedDisplays plugin, DisplaysManager displaysManager, ConfigManager config, String name, DisplayType type, Entity entity) {
        super(plugin);
        startTicking();

        this.plugin = plugin;
        this.displaysManager = displaysManager;
        this.packets = plugin.getPacketsManager().getPackets();
        this.name = name;
        this.type = type;
        this.entity = entity;
        this.entityType = entity.getType();
        this.entityId = entity.getEntityId();
        this.isApi = displaysManager.isApi();

        this.config = config;
        this.actionsHandler = new ActionsHandler(plugin, this, config);
        this.visibilityManager = new ADVisibilityManager(plugin, this);

        this.entitySection = config.getSection("entity", false, config.getConfig());

        ConfigurationSection locationSection = config.getSection("location");
        String world = locationSection.getString("world", Bukkit.getWorlds().get(0).getName());
        double x = locationSection.getDouble("x");
        double y = locationSection.getDouble("y");
        double z = locationSection.getDouble("z");
        this.location = new Location(Bukkit.getWorld(world), x, y, z);

        ConfigurationSection rotationSection = config.getSection("rotation");
        this.yaw = (float) config.getOrDefault("yaw", 0.0, rotationSection).doubleValue();
        this.pitch = (float) config.getOrDefault("pitch", 0.0, rotationSection).doubleValue();

        ConfigurationSection glowSection = config.getSection("glow", entitySection);
        this.isGlowing = config.getOrDefault("glowing", false, glowSection);
        this.glowColor = ChatColor.valueOf(config.getOrDefault("color", ChatColor.GOLD.name(), glowSection));
    }

    public ADEntityDisplay(AdvancedDisplays plugin, DisplaysManager displaysManager, String name, DisplayType type, Entity entity, boolean saveToConfig) {
        super(plugin);
        startTicking();

        this.plugin = plugin;
        this.displaysManager = displaysManager;
        this.packets = plugin.getPacketsManager().getPackets();
        this.name = name;
        this.type = type;
        this.entity = entity;
        this.entityType = entity.getType();
        this.entityId = entity.getEntityId();
        this.isApi = displaysManager.isApi();

        this.config = (saveToConfig) ? createConfig(entity.getLocation()) : null;
        this.actionsHandler = new ActionsHandler(plugin, this, config);
        this.visibilityManager = new ADVisibilityManager(plugin, this);

        // Even though it may have been set by the createConfig() method, they are set back to "null"
        // Again when the body of this constructor starts running.
        if (config != null) {
            entitySection = config.getSection("entity", false, config.getConfig());
        }

        this.location = entity.getLocation();
        this.yaw = entity.getLocation().getYaw();
        this.pitch = entity.getLocation().getPitch();
        this.isGlowing = entity.isGlowing();
        this.glowColor = ChatColor.GOLD;
    }

    protected ConfigManager createConfig(Location location) {
        ConfigManager displayConfigManager = new ConfigManager(plugin, displaysManager.getConfigsFolder() + File.separator + name + ".yml", false);
        YamlConfiguration displayConfig = displayConfigManager.getConfig();

        // Set properties in the display file.
        displayConfig.set("type", type.name());

        ConfigurationSection viewConditionsSection = displayConfig.createSection("view-conditions");
        viewConditionsSection.set("distance", 0.0);
        viewConditionsSection.set("has-permission", "none");
        viewConditionsSection.set("lacks-permission", "none");

        ConfigurationSection locationSection = displayConfig.createSection("location");
        locationSection.set("world", Objects.requireNonNull(location.getWorld()).getName());
        locationSection.set("x", location.getX());
        locationSection.set("y", location.getY());
        locationSection.set("z", location.getZ());

        ConfigurationSection rotationSection = displayConfig.createSection("rotation");
        rotationSection.set("yaw", 0.0);
        rotationSection.set("pitch", 0.0);

        ConfigurationSection entitySection = displayConfig.createSection("entity");
        entitySection.set("type", entityType.name());

        ConfigurationSection glowSection = entitySection.createSection("glow");
        glowSection.set("glowing", false);
        glowSection.set("color", ChatColor.GOLD.name());

        ConfigurationSection actionsSection = displayConfig.createSection("actions");
        ConfigurationSection anySection = actionsSection.createSection("ANY");
        ConfigurationSection actionSetting = anySection.createSection("messagePlayer");
        actionSetting.set("type", ActionType.MESSAGE.getConfigName());
        actionSetting.set("message", "You clicked me, %player_name%!");
        actionSetting.set("delay", 0);
        actionSetting.set("global", false);
        actionSetting.set("global-placeholders", true);
        actionSetting.setInlineComments("delay", List.of("In ticks"));
        displayConfigManager.save();

        return displayConfigManager;
    }

    public void sendMetadataPackets(Player player) {
        packets.setLocation(entity, player);
        packets.setRotation(entityId, yaw, pitch, player);
        packets.setGlowing(entity, isGlowing, glowColor, player);
    }

    public ADEntityDisplay create(EntityType type) {
        if (config != null) {
            entitySection.set("type", type.name());
            save();
        }

        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DisplayType getType() {
        return type;
    }

    @Override
    public VisibilityManager getVisibilityManager() {
        return visibilityManager;
    }

    @Override
    public void openEditor(Player player) {
        openEditor(player, List.of());
    }

    @Override
    public void openEditor(Player player, List<EditorItem> disabledSettings) {
        plugin.getInventoryManager().addEditingPlayer(player, this, disabledSettings);
        player.sendMessage(plugin.getMessagesManager().getColoredMessage("&aYou are now editing the display &e" + name + "&a. Run &e/ad finish &ato get your old inventory back."));
    }

    @Override
    public void closeEditor(Player player) {
        if (!plugin.getInventoryManager().isPlayerEditing(player)) return;

        plugin.getInventoryManager().finishEditing(player);
        player.sendMessage(plugin.getMessagesManager().getColoredMessage("&aYour old inventory has been successfully given back to you."));
    }

    @Override
    public Location getLocation() {
        return location;
    }
    @Override
    public void setLocation(Location location) {
        if (config != null) {
            ConfigurationSection locationSection = config.getSection("location");
            locationSection.set("world", Objects.requireNonNull(location.getWorld()).getName());
            locationSection.set("x", location.getX());
            locationSection.set("y", location.getY());
            locationSection.set("z", location.getZ());
            save();
        }

        location.setYaw(yaw);
        location.setPitch(pitch);

        if (this.location.getWorld() == location.getWorld()) {
            entity.teleport(location);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                packets.setLocation(entity, onlinePlayer);
            }
        } else {
            // Because entities cannot be teleported across worlds, the old one is removed and a new one is created
            // in the new location (another world)
            packets.removeEntity(entityId);
            entity = packets.createEntity(entityType, location);
            entityId = entity.getEntityId();

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (this instanceof ADTextDisplay textDisplay) {
                    textDisplay.restartRunnable();
                }
                sendMetadataPackets(onlinePlayer);
            }
        }
        this.location = location;
    }

    @Override
    public Location center() {
        Location centered = location.clone();

        centered.setX(location.getBlockX());
        centered.setY(location.getBlockY());
        centered.setZ(location.getBlockZ());

        if (this instanceof BlockDisplay) {
            centered.add(0.5, 0.0, 0.5);
        }

        setLocation(centered);
        return centered;
    }

    @Override
    public float getYaw() {
        return yaw;
    }
    @Override
    public float getPitch() {
        return pitch;
    }
    @Override
    public void setRotation(float yaw, float pitch) {
        location.setYaw(yaw);
        this.yaw = yaw;
        location.setPitch(pitch);
        this.pitch = pitch;
        if (config != null) {
            ConfigurationSection rotationSection = config.getSection("rotation");
            rotationSection.set("yaw", yaw);
            rotationSection.set("pitch", pitch);
            save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setRotation(yaw, pitch, onlinePlayer);
        }

    }
    @Override
    public void setRotation(float yaw, float pitch, Player player) {
        packets.setRotation(entityId, yaw, pitch, player);
    }

    @Override
    public boolean isGlowing() {
        return isGlowing;
    }

    @Override
    public void setGlowing(boolean isGlowing) {
        this.isGlowing = isGlowing;
        if (config != null) {
            ConfigurationSection glowSection = config.getSection("glow");
            glowSection.set("glowing", isGlowing);
            save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setGlowing(isGlowing, onlinePlayer);
        }
    }

    @Override
    public void setGlowing(boolean isGlowing, Player player) {
        packets.setGlowing(entity, isGlowing, glowColor, player);
    }

    @Override
    public ChatColor getGlowColor() {
        return glowColor;
    }

    @Override
    public void setGlowColor(ChatColor color) {
        glowColor = color;
        if (config != null) {
            ConfigurationSection glowSection = config.getSection("glow");
            glowSection.set("color", color.name());
            save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setGlowColor(color, onlinePlayer);
        }
    }

    @Override
    public void setGlowColor(ChatColor color, Player player) {
        packets.setGlowing(entity, isGlowing, color, player);
    }

    @Override
    public void setClickActions(DisplayActions actions) {
        actionsHandler.setClickActions(actions);
    }

    public void runActions(Player player, ClickType clickType) {
        actionsHandler.runActions(player, clickType, this);
    }

    public void spawnToPlayer(Player player) {
        packets.spawnEntity(entity, player);
        sendMetadataPackets(player);
    }

    public void removeToPlayer(Player player) {
        packets.removeEntity(entityId, player);
    }

    public void destroy() {
        packets.removeEntity(entityId);
    }

    @Override
    public void remove() {
        displaysManager.removeDisplay(this, true, true);
    }

    public void setRemoved() {
        isRemoved = true;
    }

    @Override
    public boolean isRemoved() {
        return isRemoved;
    }

    public boolean isApi() {
        return isApi;
    }

    public ConfigManager getConfigManager() {
        return config;
    }

    protected void save() {
        config.save();
    }

    @Override
    public void tick() {
        visibilityManager.updateVisibility();
    }
}