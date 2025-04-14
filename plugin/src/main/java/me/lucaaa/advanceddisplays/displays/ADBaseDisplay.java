package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.actions.ActionsHandler;
import me.lucaaa.advanceddisplays.actions.actionTypes.ActionType;
import me.lucaaa.advanceddisplays.api.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.api.actions.DisplayActions;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.api.displays.enums.EditorItem;
import me.lucaaa.advanceddisplays.api.displays.visibility.VisibilityManager;
import me.lucaaa.advanceddisplays.data.Ticking;
import me.lucaaa.advanceddisplays.managers.DisplaysManager;
import me.lucaaa.advanceddisplays.nms_common.PacketInterface;
import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.data.ConfigAxisAngle4f;
import me.lucaaa.advanceddisplays.data.ConfigVector3f;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Display;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.util.Transformation;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ADBaseDisplay extends Ticking implements BaseDisplay {
    protected final AdvancedDisplays plugin;
    private final DisplaysManager displaysManager;
    protected final PacketInterface packets;
    protected final ConfigManager config;
    protected final DisplayType type;
    private final ActionsHandler actionsHandler;
    private final ADVisibilityManager visibilityManager;

    private final String name;
    protected Display display;
    protected int displayId;
    private final boolean isApi;
    private boolean isRemoved = false;
    private Location location;

    private Display.Billboard billboard;
    private Display.Brightness brightness;
    private float shadowRadius;
    private float shadowStrength;
    private Transformation transformation;
    private float yaw;
    private float pitch;
    private boolean isGlowing;
    private Color glowColor;
    private Interaction hitbox;
    private boolean overrideHitboxSize;
    private float hitboxWidth;
    private float hitboxHeight;

    public ADBaseDisplay(AdvancedDisplays plugin, DisplaysManager displaysManager, String name, DisplayType type, ConfigManager config, Display display) {
        super(plugin);
        startTicking();

        this.plugin = plugin;
        this.displaysManager = displaysManager;
        this.packets = plugin.getPacketsManager().getPackets();
        this.name = name;
        this.display = display;
        this.displayId = display.getEntityId();
        this.isApi = plugin.getDisplaysManager() != displaysManager;

        this.config = config;
        this.type = type;
        this.actionsHandler = new ActionsHandler(plugin, this, config.getConfig());
        this.visibilityManager = new ADVisibilityManager(plugin, this);

        ConfigurationSection locationSection = config.getSection("location");
        String world = locationSection.getString("world", Bukkit.getWorlds().get(0).getName());
        double x = locationSection.getDouble("x");
        double y = locationSection.getDouble("y");
        double z = locationSection.getDouble("z");
        this.location = new Location(Bukkit.getWorld(world), x, y, z);

        this.billboard = Display.Billboard.valueOf(config.getOrDefault("rotationType", "FIXED"));

        ConfigurationSection brightnessSection = config.getSection("brightness");
        this.brightness = new Display.Brightness(brightnessSection.getInt("block"), brightnessSection.getInt("sky"));

        ConfigurationSection shadowSection = config.getSection("shadow");
        this.shadowRadius = (float) shadowSection.getDouble("radius");
        this.shadowStrength = (float) shadowSection.getDouble("strength");

        ConfigurationSection transformationSection = config.getSection("transformation");
        this.transformation = new Transformation(
                new ConfigVector3f(Objects.requireNonNull(transformationSection.getConfigurationSection("translation")).getValues(false)).toVector3f(),
                new ConfigAxisAngle4f(Objects.requireNonNull(transformationSection.getConfigurationSection("leftRotation")).getValues(false)).toAxisAngle4f(),
                new ConfigVector3f(Objects.requireNonNull(transformationSection.getConfigurationSection("scale")).getValues(false)).toVector3f(),
                new ConfigAxisAngle4f(Objects.requireNonNull(transformationSection.getConfigurationSection("rightRotation")).getValues(false)).toAxisAngle4f()
        );

        ConfigurationSection rotationSection = config.getSection("rotation");
        this.yaw = (float) rotationSection.getDouble("yaw");
        this.pitch = (float) rotationSection.getDouble("pitch");

        ConfigurationSection glowSection = config.getSection("glow");
        this.isGlowing = glowSection.getBoolean("glowing");
        String[] colorParts = Objects.requireNonNull(glowSection.getString("color")).split(";");
        this.glowColor = Color.fromRGB(Integer.parseInt(colorParts[0]), Integer.parseInt(colorParts[1]), Integer.parseInt(colorParts[2]));

        Location location1 = location.clone();
        if (type == DisplayType.BLOCK) {
            double x1 = transformation.getScale().x / 2;
            double z1 = transformation.getScale().z / 2;
            location1.add(x1, 0.0, z1);
        }
        this.hitbox = packets.createInteractionEntity(location1);
        ConfigurationSection hitboxSection = config.getSection("hitbox");
        this.overrideHitboxSize = hitboxSection.getBoolean("override");
        this.hitboxWidth = (float) hitboxSection.getDouble("width");
        this.hitboxHeight = (float) hitboxSection.getDouble("height");
    }

    public ADBaseDisplay(AdvancedDisplays plugin, DisplaysManager displaysManager, String name, DisplayType type, Display display, boolean saveToConfig) {
        super(plugin);
        startTicking();

        this.plugin = plugin;
        this.displaysManager = displaysManager;
        this.packets = plugin.getPacketsManager().getPackets();
        this.name = name;
        this.display = display;
        this.displayId = display.getEntityId();
        this.isApi = plugin.getDisplaysManager() != displaysManager;

        this.type = type;
        this.config = (saveToConfig) ? createConfig(display.getLocation()) : null;
        this.actionsHandler = new ActionsHandler(plugin);
        this.visibilityManager = new ADVisibilityManager(plugin, this);

        this.location = display.getLocation();
        this.billboard = Display.Billboard.CENTER; // Text displays will be easier to spot.
        this.brightness = new Display.Brightness(15, 15);
        this.shadowRadius = display.getShadowRadius();
        this.shadowStrength = display.getShadowStrength();
        this.transformation = display.getTransformation();
        this.yaw = display.getLocation().getYaw();
        this.pitch = display.getLocation().getPitch();
        this.isGlowing = display.isGlowing();
        this.glowColor = Color.ORANGE;

        Location location1 = this.location;
        if (this.type == DisplayType.BLOCK) {
            double x1 = transformation.getScale().x / 2;
            double z1 = transformation.getScale().z / 2;
            location1.add(x1, 0.0, z1);
        }
        this.hitbox = packets.createInteractionEntity(location1);
        this.overrideHitboxSize = false;
        this.hitboxWidth = transformation.getScale().x;
        this.hitboxHeight = transformation.getScale().z;
    }

    private ConfigManager createConfig(Location location) {
        ConfigManager displayConfigManager = new ConfigManager(plugin, displaysManager.getConfigsFolder() + File.separator + name + ".yml", false);
        YamlConfiguration displayConfig = displayConfigManager.getConfig();

        // Set properties in the display file.
        displayConfig.set("type", type.name());
        ConfigurationSection viewConditionsSection = displayConfig.createSection("view-conditions");
        viewConditionsSection.createSection("distance").set("distance", 0.0);
        viewConditionsSection.createSection("has-permission").set("permission", "none");
        viewConditionsSection.createSection("lacks-permission").set("permission", "none");

        ConfigurationSection locationSection = displayConfig.createSection("location");
        locationSection.set("world", Objects.requireNonNull(location.getWorld()).getName());
        locationSection.set("x", location.getX());
        locationSection.set("y", location.getY());
        locationSection.set("z", location.getZ());

        displayConfig.set("rotationType", org.bukkit.entity.Display.Billboard.CENTER.name());

        ConfigurationSection brightnessSection = displayConfig.createSection("brightness");
        brightnessSection.set("block", 15);
        brightnessSection.set("sky", 15);

        ConfigurationSection shadowSection = displayConfig.createSection("shadow");
        shadowSection.set("radius", 5.0);
        shadowSection.set("strength", 1.0);

        ConfigurationSection transformationSection = displayConfig.createSection("transformation");
        transformationSection.createSection("translation", new ConfigVector3f().serialize());
        transformationSection.createSection("leftRotation", new ConfigAxisAngle4f().serialize());
        transformationSection.createSection("scale", new ConfigVector3f(1.0f, 1.0f, 1.0f).serialize());
        transformationSection.createSection("rightRotation", new ConfigAxisAngle4f().serialize());

        ConfigurationSection rotationSection = displayConfig.createSection("rotation");
        rotationSection.set("yaw", 0.0);
        rotationSection.set("pitch", 0.0);

        ConfigurationSection glowSection = displayConfig.createSection("glow");
        glowSection.set("glowing", false);
        glowSection.set("color", "255;170;0");

        ConfigurationSection hitboxSection = displayConfig.createSection("hitbox");
        hitboxSection.set("override", false);
        hitboxSection.set("width", 1.0f);
        hitboxSection.set("height", 1.0f);
        displayConfig.setComments("hitbox", Arrays.asList("Displays don't have hitboxes of their own, so to have click actions independent entities have to be created.", "These settings allow you to control the hitbox of the display.", "(Use F3 + B to see the hitboxes)"));

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

    public void sendBaseMetadataPackets(Player player) {
        packets.setLocation(display, player);
        packets.setRotation(displayId, yaw, pitch, player);
        packets.setTransformation(displayId, transformation, player);
        if (!overrideHitboxSize) {
            packets.setInteractionSize(hitbox.getEntityId(), transformation.getScale().x, transformation.getScale().y, player);
        } else {
            packets.setInteractionSize(hitbox.getEntityId(), hitboxWidth, hitboxHeight, player);
        }
        packets.setBillboard(displayId, billboard, player);
        packets.setBrightness(displayId, brightness, player);
        packets.setShadow(displayId, shadowRadius, shadowStrength, player);
        packets.setGlowing(displayId, isGlowing, glowColor, player);
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
        plugin.getInventoryManager().addEditingPlayer(player, disabledSettings, this);
        player.sendMessage(plugin.getMessagesManager().getColoredMessage("&aYou are now editing the display &e" + display.getName() + "&a. Run &e/ad finish &ato get your old inventory back."));
    }

    @Override
    public void closeEditor(Player player) {
        if (plugin.getInventoryManager().isPlayerNotEditing(player)) return;

        plugin.getInventoryManager().getEditingPlayer(player).finishEditing();
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
            display.teleport(location);
            Location location1 = location.clone();
            if (type == DisplayType.BLOCK) {
                double x1 = transformation.getScale().x / 2;
                double z1 = transformation.getScale().z / 2;
                location1.add(x1, 0.0, z1);
            }
            hitbox.teleport(location1);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                packets.setLocation(display, onlinePlayer);
                packets.setLocation(hitbox, onlinePlayer);
            }
        } else {
            // Because entities cannot be teleported across worlds, the old one is removed and a new one is created
            // in the new location (another world)
            packets.removeEntity(displayId);
            packets.removeEntity(hitbox.getEntityId());

            plugin.getInteractionsManager().removeInteraction(getInteractionId());

            display = switch (type) {
                case BLOCK -> packets.createBlockDisplay(location);
                case TEXT -> packets.createTextDisplay(location);
                case ITEM -> packets.createItemDisplay(location);
            };
            displayId = display.getEntityId();
            if (type == DisplayType.BLOCK) {
                double x1 = transformation.getScale().x / 2;
                double z1 = transformation.getScale().z / 2;
                location.add(x1, 0.0, z1);
            }
            hitbox = packets.createInteractionEntity(location);

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (this instanceof ADTextDisplay textDisplay) {
                    textDisplay.restartRunnable();
                }
                ((DisplayMethods) this).sendMetadataPackets(onlinePlayer);
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

        if (type != DisplayType.BLOCK) {
            centered.add(0.5, 0.0, 0.5);
        }

        setLocation(centered);
        return centered;
    }

    @Override
    public Display.Billboard getBillboard() {
        return billboard;
    }
    @Override
    public void setBillboard(Display.Billboard billboard) {
        this.billboard = billboard;
        if (config != null) {
            config.set("rotationType", billboard.name());
            save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setBillboard(billboard, onlinePlayer);
        }
    }
    @Override
    public void setBillboard(Display.Billboard billboard, Player player) {
        packets.setBillboard(displayId, billboard, player);
    }

    @Override
    public Display.Brightness getBrightness() {
        return brightness;
    }
    @Override
    public void setBrightness(Display.Brightness brightness) {
        this.brightness = brightness;
        if (config != null) {
            ConfigurationSection brightnessSection = config.getSection("brightness");
            brightnessSection.set("block", brightness.getBlockLight());
            brightnessSection.set("sky", brightness.getSkyLight());
            save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setBrightness(brightness, onlinePlayer);
        }
    }
    @Override
    public void setBrightness(Display.Brightness brightness, Player player) {
        packets.setBrightness(displayId, brightness, player);
    }

    @Override
    public float getShadowRadius() {
        return shadowRadius;
    }
    @Override
    public float getShadowStrength() {
        return shadowStrength;
    }
    @Override
    public void setShadow(float shadowRadius, float shadowStrength) {
        this.shadowRadius = shadowRadius;
        this.shadowStrength = shadowStrength;
        if (config != null) {
            ConfigurationSection shadowSection = config.getSection("shadow");
            shadowSection.set("radius", shadowRadius);
            shadowSection.set("strength", shadowStrength);
            save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setShadow(shadowRadius, shadowStrength, onlinePlayer);
        }
    }
    @Override
    public void setShadow(float shadowRadius, float shadowStrength, Player player) {
        packets.setShadow(displayId, shadowRadius, shadowStrength, player);
    }

    @Override
    public Transformation getTransformation() {
        return transformation;
    }
    @Override
    public void setTransformation(Transformation transformation) {
        this.transformation = transformation;
        if (!overrideHitboxSize) {
            hitbox.setInteractionWidth(transformation.getScale().x);
            hitbox.setInteractionHeight(transformation.getScale().y);
        }

        if (config != null) {
            ConfigurationSection transformationSection = config.getSection("transformation");
            transformationSection.createSection("translation", new ConfigVector3f(transformation.getTranslation()).serialize());
            transformationSection.createSection("leftRotation", new ConfigAxisAngle4f(transformation.getLeftRotation()).serialize());
            transformationSection.createSection("scale", new ConfigVector3f(transformation.getScale()).serialize());
            transformationSection.createSection("rightRotation", new ConfigAxisAngle4f(transformation.getRightRotation()).serialize());
            save();
        }

        // Centers the block in the hitbox when size changes
        Location location1 = location.clone();
        if (type == DisplayType.BLOCK) {
            double x1 = transformation.getScale().x / 2;
            double z1 = transformation.getScale().z / 2;
            location1.add(x1, 0.0, z1);
        }
        hitbox.teleport(location1);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setTransformation(transformation, onlinePlayer);
            packets.setLocation(hitbox, onlinePlayer);
        }

    }
    @Override
    public void setTransformation(Transformation transformation, Player player) {
        packets.setTransformation(displayId, transformation, player);
        if (!overrideHitboxSize) {
            packets.setInteractionSize(hitbox.getEntityId(), transformation.getScale().x, transformation.getScale().y, player);
        }
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
        packets.setRotation(displayId, yaw, pitch, player);
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
        packets.setGlowing(displayId, isGlowing, glowColor, player);
    }

    @Override
    public Color getGlowColor() {
        return glowColor;
    }

    @Override
    public void setGlowColor(Color color) {
        glowColor = color;
        if (config != null) {
            ConfigurationSection glowSection = config.getSection("glow");
            glowSection.set("color", color.getRed() + ";" + color.getGreen() + ";" + color.getBlue());
            save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setGlowColor(color, onlinePlayer);
        }
    }

    @Override
    public void setGlowColor(Color color, Player player) {
        packets.setGlowing(displayId, isGlowing, color, player);
    }

    @Override
    public void setHitboxSize(boolean override, float width, float height) {
        overrideHitboxSize = override;
        hitboxWidth = (override) ? width : transformation.getScale().x;
        hitboxHeight = (override) ? height : transformation.getScale().y;

        if (config != null) {
            ConfigurationSection hitboxSection = config.getSection("hitbox");
            hitboxSection.set("override", override);
            hitboxSection.set("width", width);
            hitboxSection.set("height", height);
            save();
        }

        hitbox.setInteractionWidth(hitboxWidth);
        hitbox.setInteractionHeight(hitboxHeight);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            packets.setInteractionSize(hitbox.getEntityId(), hitboxWidth, hitboxHeight, onlinePlayer);
        }
    }

    @Override
    public float getHitboxWidth() {
        return hitboxWidth;
    }

    @Override
    public float getHitboxHeight() {
        return hitboxHeight;
    }

    @Override
    public boolean isHitboxSizeOverriden() {
        return overrideHitboxSize;
    }

    @Override
    public void setClickActions(DisplayActions actions) {
        actionsHandler.setClickActions(actions);
    }

    public void runActions(Player player, ClickType clickType) {
        actionsHandler.runActions(player, clickType, this);
    }

    public void spawnToPlayer(Player player) {
        packets.spawnEntity(display, player);
        packets.spawnEntity(hitbox, player);
        ((DisplayMethods) this).sendMetadataPackets(player);
    }

    public void removeToPlayer(Player player) {
        packets.removeEntity(displayId, player);
        packets.removeEntity(getInteractionId(), player);
    }

    public void destroy() {
        packets.removeEntity(displayId);
        packets.removeEntity(hitbox.getEntityId());
    }

    @Override
    public void remove() {
        displaysManager.removeDisplay(this, true);
    }

    public void setRemoved() {
        isRemoved = true;
    }

    @Override
    public boolean isRemoved() {
        return isRemoved;
    }

    public int getInteractionId() {
        return hitbox.getEntityId();
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