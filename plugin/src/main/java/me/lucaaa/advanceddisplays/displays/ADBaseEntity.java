package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.actions.ActionsHandler;
import me.lucaaa.advanceddisplays.actions.actionTypes.ActionType;
import me.lucaaa.advanceddisplays.api.actions.DisplayActions;
import me.lucaaa.advanceddisplays.api.displays.BaseEntity;
import me.lucaaa.advanceddisplays.api.displays.BlockDisplay;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.api.displays.enums.EditorItem;
import me.lucaaa.advanceddisplays.api.displays.visibility.VisibilityManager;
import me.lucaaa.advanceddisplays.api.util.ComponentSerializer;
import me.lucaaa.advanceddisplays.data.PlayerData;
import me.lucaaa.advanceddisplays.data.Ticking;
import me.lucaaa.advanceddisplays.data.Utils;
import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.managers.DisplaysManager;
import me.lucaaa.advanceddisplays.nms_common.Metadata;
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

public class ADBaseEntity extends Ticking implements BaseEntity {
    protected final AdvancedDisplays plugin;
    protected final Metadata metadata;
    private final DisplaysManager displaysManager;
    protected final PacketInterface packets;
    protected final ConfigManager config;
    private final ActionsHandler actionsHandler;
    private final ADVisibilityManager visibilityManager;

    protected ConfigurationSection entitySection;

    private final String name;
    protected final DisplayType type;
    protected Entity entity;
    protected EntityType entityType;
    protected int entityId;
    private final boolean isApi;
    private boolean isRemoved = false;

    protected Location location;
    protected float yaw;
    protected float pitch;

    private boolean isOnFire;
    private boolean isSprinting;
    protected boolean isGlowing;
    protected ChatColor glowColor;
    private String customName;
    private boolean isCustomNameVisible;

    protected ADBaseEntity(AdvancedDisplays plugin, DisplaysManager displaysManager, ConfigManager config, String name, DisplayType type, EntityType entityType) {
        super(plugin);
        startTicking();

        this.plugin = plugin;
        this.metadata = plugin.metadata;
        this.displaysManager = displaysManager;
        this.packets = plugin.getPacketsManager().getPackets();
        this.name = name;
        this.type = type;
        this.entityType = entityType;
        this.isApi = displaysManager.isApi();

        this.config = config;
        this.actionsHandler = new ActionsHandler(plugin, this, config);
        this.visibilityManager = new ADVisibilityManager(plugin, this);

        this.entitySection = config.getSection("entity", false, config.getConfig());

        ConfigurationSection rotationSection = config.getSection("rotation");
        this.yaw = (float) config.getOrDefault("yaw", 0.0, rotationSection).doubleValue();
        this.pitch = (float) config.getOrDefault("pitch", 0.0, rotationSection).doubleValue();

        // Location is already validated.
        ConfigurationSection locationSection = config.getSection("location");
        String world = locationSection.getString("world", Bukkit.getWorlds().get(0).getName());
        double x = locationSection.getDouble("x");
        double y = locationSection.getDouble("y");
        double z = locationSection.getDouble("z");
        this.location = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);

        this.isOnFire = config.getOrDefault("onFire", false, entitySection);
        this.isSprinting = config.getOrDefault("sprinting", false, entitySection);

        ConfigurationSection glowSection = config.getSection("glow", entitySection);
        this.isGlowing = config.getOrDefault("glowing", false, glowSection);
        this.glowColor = ChatColor.valueOf(config.getOrDefault("color", ChatColor.GOLD.name(), glowSection));

        this.customName = config.getOrDefault("custom-name", entityType.name(), entitySection);
        this.isCustomNameVisible = config.getOrDefault("custom-name-visible", false, entitySection);

        this.entity = packets.createEntity(entityType, location);
        this.entityId = entity.getEntityId();
    }

    protected ADBaseEntity(AdvancedDisplays plugin, DisplaysManager displaysManager, String name, DisplayType type, EntityType entityType, Location location, boolean saveToConfig) {
        super(plugin);
        startTicking();

        this.plugin = plugin;
        this.metadata = plugin.metadata;
        this.displaysManager = displaysManager;
        this.packets = plugin.getPacketsManager().getPackets();
        this.name = name;
        this.type = type;
        this.entity = packets.createEntity(entityType, location);
        this.entityType = entityType;
        this.entityId = entity.getEntityId();
        this.isApi = displaysManager.isApi();

        this.config = (saveToConfig) ? createConfig(location) : null;
        this.actionsHandler = new ActionsHandler(plugin, this, config);
        this.visibilityManager = new ADVisibilityManager(plugin, this);

        this.location = location;
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        this.isGlowing = entity.isGlowing();
        this.glowColor = ChatColor.GOLD;
        this.customName = entityType.name();
        this.isCustomNameVisible = false;

        // Even though it may have been set by the createConfig() method, they are set back to "null"
        // Again when the body of this constructor starts running.
        if (config != null) {
            entitySection = config.getSection("entity", false, config.getConfig());
        }
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
        entitySection.set("onFire", false);
        entitySection.set("sprinting", false);
        entitySection.set("custom-name", entityType.name());
        entitySection.set("custom-name-visible", false);

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
        packets.setLocation(entity, location, player);
        packets.setMetadata(entityId, player,
                new Metadata.DataPair<>(metadata.PROPERTIES, Metadata.getProperties(isOnFire, isSprinting, isGlowing)),
                new Metadata.DataPair<>(metadata.CUSTOM_NAME, ComponentSerializer.deserialize(Utils.getColoredTextWithPlaceholders(player, customName))),
                new Metadata.DataPair<>(metadata.CUSTOM_NAME_VISIBLE, isCustomNameVisible)
        );
        packets.setGlowingColor(entity, glowColor, player);
    }

    public ADBaseEntity create() {
        if (config != null) {
            entitySection.set("type", entityType.name());
            save();
        }

        return this;
    }

    public int getInteractionId() {
        return entityId;
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
        plugin.getPlayersManager().getPlayerData(player).startEditing(this, disabledSettings);
        player.sendMessage(plugin.getMessagesManager().getColoredMessage("&aYou are now editing the display &e" + name + "&a. Run &e/ad finish &ato get your old inventory back."));
    }

    @Override
    public void closeEditor(Player player) {
        PlayerData playerData = plugin.getPlayersManager().getPlayerData(player);
        if (!playerData.isEditing()) return;

        playerData.finishEditing();
        player.sendMessage(plugin.getMessagesManager().getColoredMessage("&aYour old inventory has been successfully given back to you."));
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

    @Override
    public Location getLocation() {
        return location;
    }
    @Override
    public void setLocation(Location location) {
        if (config != null) {
            ConfigurationSection locationSection = config.getSection("location", config.getConfig());
            locationSection.set("world", Objects.requireNonNull(location.getWorld()).getName());
            locationSection.set("x", location.getX());
            locationSection.set("y", location.getY());
            locationSection.set("z", location.getZ());
            save();
        }

        location.setYaw(yaw);
        location.setPitch(pitch);

        if (this.location.getWorld() == location.getWorld()) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                packets.setLocation(entity, location, onlinePlayer);
            }
        } else {
            // Because entities cannot be teleported across worlds, the old one is removed and a new one is created
            // in the new location (another world)
            packets.removeEntity(entityId);
            plugin.getInteractionsManager().removeInteraction(getInteractionId());

            entity = packets.createEntity(entityType, location);
            entityId = entity.getEntityId();

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                sendMetadataPackets(onlinePlayer);
            }

            plugin.getInteractionsManager().addInteraction(getInteractionId(), this);
        }
        this.location = location;
    }

    @Override
    public Location center() {
        Location centered = location.clone();

        centered.setX(location.getBlockX());
        centered.setY(location.getBlockY());
        centered.setZ(location.getBlockZ());

        if (!(this instanceof BlockDisplay)) {
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
        Location loc = location.clone();
        loc.setYaw(yaw);
        loc.setPitch(pitch);
        packets.setLocation(entity, loc, player);
    }

    @Override
    public boolean isOnFire() {
        return isOnFire;
    }

    @Override
    public void setOnFire(boolean onFire) {
        this.isOnFire = onFire;
        if (config != null) {
            entitySection.set("onFire", onFire);
            save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setOnFire(onFire, onlinePlayer);
        }
    }

    @Override
    public void setOnFire(boolean onFire, Player player) {
        packets.setMetadata(entityId, player, metadata.PROPERTIES, Metadata.getProperties(onFire, isSprinting, isGlowing));
    }

    @Override
    public boolean isSprinting() {
        return isSprinting;
    }

    @Override
    public void setSprinting(boolean sprinting) {
        this.isSprinting = sprinting;
        if (config != null) {
            entitySection.set("sprinting", sprinting);
            save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setSprinting(sprinting, onlinePlayer);
        }
    }

    @Override
    public void setSprinting(boolean sprinting, Player player) {
        packets.setMetadata(entityId, player, metadata.PROPERTIES, Metadata.getProperties(isOnFire, sprinting, isGlowing));
    }

    @Override
    public boolean isGlowing() {
        return isGlowing;
    }

    @Override
    public void setGlowing(boolean isGlowing) {
        this.isGlowing = isGlowing;
        if (config != null) {
            ConfigurationSection glowSection = config.getSection("glow", entitySection);
            glowSection.set("glowing", isGlowing);
            save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setGlowing(isGlowing, onlinePlayer);
        }
    }

    @Override
    public void setGlowing(boolean isGlowing, Player player) {
        packets.setMetadata(entityId, player, metadata.PROPERTIES, Metadata.getProperties(isOnFire, isSprinting, isGlowing));
    }

    @Override
    public ChatColor getGlowColor() {
        return glowColor;
    }

    @Override
    public void setGlowColor(ChatColor color) {
        glowColor = color;
        if (config != null) {
            ConfigurationSection glowSection = config.getSection("glow", entitySection);
            glowSection.set("color", color.name());
            save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setGlowColor(color, onlinePlayer);
        }
    }

    @Override
    public void setGlowColor(ChatColor color, Player player) {
        packets.setGlowingColor(entity, color, player);
    }

    @Override
    public String getCustomName() {
        return customName;
    }

    @Override
    public void setCustomName(String customName) {
        this.customName = customName;
        if (config != null) {
            entitySection.set("custom-name", customName);
            save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setCustomName(customName, onlinePlayer);
        }
    }

    @Override
    public void setCustomName(String customName, Player player) {
        packets.setMetadata(entityId, player, metadata.CUSTOM_NAME, ComponentSerializer.deserialize(Utils.getColoredTextWithPlaceholders(player, customName)));
    }

    @Override
    public boolean isCustomNameVisible() {
        return isCustomNameVisible;
    }

    @Override
    public void setCustomNameVisible(boolean visible) {
        this.isCustomNameVisible = visible;
        if (config != null) {
            entitySection.set("custom-name-visible", visible);
            save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setCustomNameVisible(visible, onlinePlayer);
        }
    }

    @Override
    public void setCustomNameVisible(boolean visible, Player player) {
        packets.setMetadata(entityId, player, metadata.CUSTOM_NAME_VISIBLE, visible);
    }

    public int getEntityId() {
        return entityId;
    }
}