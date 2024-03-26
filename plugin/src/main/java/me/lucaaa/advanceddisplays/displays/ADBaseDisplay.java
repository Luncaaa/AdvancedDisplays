package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.actions.ActionsHandler;
import me.lucaaa.advanceddisplays.api.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.api.actions.DisplayActions;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.api.actions.ClickType;
import me.lucaaa.advanceddisplays.nms_common.PacketInterface;
import me.lucaaa.advanceddisplays.common.managers.ConfigManager;
import me.lucaaa.advanceddisplays.common.utils.ConfigAxisAngle4f;
import me.lucaaa.advanceddisplays.common.utils.ConfigVector3f;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Display;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;

import java.util.Objects;

public class ADBaseDisplay implements BaseDisplay {
    protected final PacketInterface packets = AdvancedDisplays.packetsManager.getPackets();
    protected final ConfigManager configManager;
    protected final YamlConfiguration config;
    protected final DisplayType type;
    private final ActionsHandler actionsHandler;

    protected Display display;
    protected int displayId;
    private final boolean isApi;
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

    public ADBaseDisplay(DisplayType type, ConfigManager configManager, Display display, boolean isApi) {
        this.display = display;
        this.displayId = display.getEntityId();
        this.isApi = isApi;

        this.configManager = configManager;
        this.config = configManager.getConfig();
        this.type = type;
        this.actionsHandler = new ActionsHandler(configManager.getConfig());

        ConfigurationSection locationSection = Objects.requireNonNull(this.config.getConfigurationSection("location"));
        String world = locationSection.getString("world", "world");
        double x = locationSection.getDouble("x");
        double y = locationSection.getDouble("y");
        double z = locationSection.getDouble("z");
        this.location = new Location(Bukkit.getWorld(world), x, y, z);

        this.billboard = Display.Billboard.valueOf(this.config.getString("rotationType"));

        ConfigurationSection brightnessSection = Objects.requireNonNull(this.config.getConfigurationSection("brightness"));
        this.brightness = new Display.Brightness(brightnessSection.getInt("block"), brightnessSection.getInt("sky"));

        ConfigurationSection shadowSection = Objects.requireNonNull(this.config.getConfigurationSection("shadow"));
        this.shadowRadius = shadowSection.getInt("radius");
        this.shadowStrength = shadowSection.getInt("strength");

        ConfigurationSection transformationSection = Objects.requireNonNull(this.config.getConfigurationSection("transformation"));
        this.transformation = new Transformation(
                new ConfigVector3f(Objects.requireNonNull(transformationSection.getConfigurationSection("translation")).getValues(false)).toVector3f(),
                new ConfigAxisAngle4f(Objects.requireNonNull(transformationSection.getConfigurationSection("leftRotation")).getValues(false)).toAxisAngle4f(),
                new ConfigVector3f(Objects.requireNonNull(transformationSection.getConfigurationSection("scale")).getValues(false)).toVector3f(),
                new ConfigAxisAngle4f(Objects.requireNonNull(transformationSection.getConfigurationSection("rightRotation")).getValues(false)).toAxisAngle4f()
        );

        ConfigurationSection rotationSection = Objects.requireNonNull(this.config.getConfigurationSection("rotation"));
        this.yaw = (float) rotationSection.getDouble("yaw");
        this.pitch = (float) rotationSection.getDouble("pitch");

        ConfigurationSection glowSection = Objects.requireNonNull(this.config.getConfigurationSection("glow"));
        this.isGlowing = glowSection.getBoolean("glowing");
        String[] colorParts = Objects.requireNonNull(glowSection.getString("color")).split(";");
        this.glowColor = Color.fromRGB(Integer.parseInt(colorParts[0]), Integer.parseInt(colorParts[1]), Integer.parseInt(colorParts[2]));

        Location location1 = this.location;
        if (this.type == DisplayType.BLOCK) {
            double x1 = this.transformation.getScale().x / 2;
            double z1 = this.transformation.getScale().z / 2;
            location1.add(x1, 0.0, z1);
        }
        this.hitbox = this.packets.createInteractionEntity(location1);
        ConfigurationSection hitboxSection = Objects.requireNonNull(this.config.getConfigurationSection("hitbox"));
        this.overrideHitboxSize = hitboxSection.getBoolean("override");
        this.hitboxWidth = (float) hitboxSection.getDouble("width");
        this.hitboxHeight = (float) hitboxSection.getDouble("height");
    }

    public ADBaseDisplay(DisplayType type, Display display) {
        this.display = display;
        this.displayId = display.getEntityId();
        this.isApi = true;

        this.configManager = null;
        this.config = null;
        this.type = type;
        this.actionsHandler = new ActionsHandler();

        this.location = display.getLocation();
        this.billboard = display.getBillboard();
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
            double x1 = this.transformation.getScale().x / 2;
            double z1 = this.transformation.getScale().z / 2;
            location1.add(x1, 0.0, z1);
        }
        this.hitbox = this.packets.createInteractionEntity(location1);
        this.overrideHitboxSize = false;
        this.hitboxWidth = this.transformation.getScale().x;
        this.hitboxHeight = this.transformation.getScale().z;
    }

    public void sendBaseMetadataPackets(Player player) {
        this.packets.setLocation(this.display, player);
        this.packets.setRotation(this.displayId, this.yaw, this.pitch, player);
        this.packets.setTransformation(this.displayId, this.transformation, player);
        if (!this.overrideHitboxSize) {
            this.packets.setInteractionSize(this.hitbox.getEntityId(), this.transformation.getScale().x, this.transformation.getScale().y, player);
        } else {
            this.packets.setInteractionSize(this.hitbox.getEntityId(), this.hitboxWidth, this.hitboxHeight, player);
        }
        this.packets.setBillboard(this.displayId, this.billboard, player);
        this.packets.setBrightness(this.displayId, this.brightness, player);
        this.packets.setShadow(this.displayId, this.shadowRadius, this.shadowStrength, player);
        this.packets.setGlowing(this.displayId, this.isGlowing, this.glowColor, player);
    }

    @Override
    public DisplayType getType() {
        return this.type;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }
    @Override
    public void setLocation(Location location) {
        if (this.config != null) {
            ConfigurationSection locationSection = Objects.requireNonNull(this.config.getConfigurationSection("location"));
            locationSection.set("world", Objects.requireNonNull(location.getWorld()).getName());
            locationSection.set("x", location.getX());
            locationSection.set("y", location.getY());
            locationSection.set("z", location.getZ());
            this.save();
        }

        location.setYaw(this.yaw);
        location.setPitch(this.pitch);

        if (this.location.getWorld() == location.getWorld()) {
            this.display.teleport(location);
            if (this.type == DisplayType.BLOCK) {
                double x1 = this.transformation.getScale().x / 2;
                double z1 = this.transformation.getScale().z / 2;
                location.add(x1, 0.0, z1);
            }
            this.hitbox.teleport(location);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                this.packets.setLocation(this.display, onlinePlayer);
                this.packets.setLocation(this.hitbox, onlinePlayer);
            }
        } else {
            // Because entities cannot be teleported across worlds, the old one is removed and a new one is created
            // in the new location (another world)
            this.packets.removeEntity(this.displayId);
            this.packets.removeEntity(this.hitbox.getEntityId());

            AdvancedDisplays.interactionsManager.removeInteraction(this.getInteractionId());

            this.display = switch (this.type) {
                case BLOCK -> this.packets.createBlockDisplay(location);
                case TEXT -> this.packets.createTextDisplay(location);
                case ITEM -> this.packets.createItemDisplay(location);
            };
            this.displayId = this.display.getEntityId();
            if (this.type == DisplayType.BLOCK) {
                double x1 = this.transformation.getScale().x / 2;
                double z1 = this.transformation.getScale().z / 2;
                location.add(x1, 0.0, z1);
            }
            this.hitbox = this.packets.createInteractionEntity(location);

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (this instanceof ADTextDisplay textDisplay) {
                    textDisplay.restartRunnable();
                }
                ((DisplayMethods) this).sendMetadataPackets(onlinePlayer);
            }

            AdvancedDisplays.interactionsManager.addInteraction(this.getInteractionId(), this);
        }
        this.location = location;
    }

    @Override
    public Display.Billboard getBillboard() {
        return this.billboard;
    }
    @Override
    public void setBillboard(Display.Billboard billboard) {
        this.billboard = billboard;
        if (this.config != null) {
            this.config.set("rotationType", billboard.name());
            this.save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.setBillboard(billboard, onlinePlayer);
        }
    }
    @Override
    public void setBillboard(Display.Billboard billboard, Player player) {
        this.packets.setBillboard(this.displayId, billboard, player);
    }

    @Override
    public Display.Brightness getBrightness() {
        return this.brightness;
    }
    @Override
    public void setBrightness(Display.Brightness brightness) {
        this.brightness = brightness;
        if (this.config != null) {
            ConfigurationSection brightnessSection = Objects.requireNonNull(this.config.getConfigurationSection("brightness"));
            brightnessSection.set("block", brightness.getBlockLight());
            brightnessSection.set("sky", brightness.getSkyLight());
            this.save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.setBrightness(brightness, onlinePlayer);
        }
    }
    @Override
    public void setBrightness(Display.Brightness brightness, Player player) {
        this.packets.setBrightness(this.displayId, brightness, player);
    }

    @Override
    public float getShadowRadius() {
        return this.shadowRadius;
    }
    @Override
    public float getShadowStrength() {
        return this.shadowStrength;
    }
    @Override
    public void setShadow(float shadowRadius, float shadowStrength) {
        this.shadowRadius = shadowRadius;
        this.shadowStrength = shadowStrength;
        if (this.config != null) {
            ConfigurationSection shadowSection = Objects.requireNonNull(this.config.getConfigurationSection("shadow"));
            shadowSection.set("radius", shadowRadius);
            shadowSection.set("strength", shadowStrength);
            this.save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.setShadow(shadowRadius, shadowStrength, onlinePlayer);
        }
    }
    @Override
    public void setShadow(float shadowRadius, float shadowStrength, Player player) {
        this.packets.setShadow(this.displayId, shadowRadius, shadowStrength, player);
    }

    @Override
    public Transformation getTransformation() {
        return this.transformation;
    }
    @Override
    public void setTransformation(Transformation transformation) {
        this.transformation = transformation;
        if (!this.overrideHitboxSize) {
            this.hitbox.setInteractionWidth(transformation.getScale().x);
            this.hitbox.setInteractionHeight(transformation.getScale().y);
        }

        if (this.config != null) {
            ConfigurationSection transformationSection = Objects.requireNonNull(this.config.getConfigurationSection("transformation"));
            transformationSection.createSection("translation", new ConfigVector3f(transformation.getTranslation()).serialize());
            transformationSection.createSection("leftRotation", new ConfigAxisAngle4f(transformation.getLeftRotation()).serialize());
            transformationSection.createSection("scale", new ConfigVector3f(transformation.getScale()).serialize());
            transformationSection.createSection("rightRotation", new ConfigAxisAngle4f(transformation.getRightRotation()).serialize());
            this.save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.setTransformation(transformation, onlinePlayer);
        }

    }
    @Override
    public void setTransformation(Transformation transformation, Player player) {
        this.packets.setTransformation(this.displayId, transformation, player);
        if (!this.overrideHitboxSize) {
            this.packets.setInteractionSize(this.hitbox.getEntityId(), transformation.getScale().x, transformation.getScale().y, player);
        }
    }

    @Override
    public float getYaw() {
        return this.yaw;
    }
    @Override
    public float getPitch() {
        return this.pitch;
    }
    @Override
    public void setRotation(float yaw, float pitch) {
        this.location.setYaw(yaw);
        this.yaw = yaw;
        this.location.setPitch(pitch);
        this.pitch = pitch;
        if (this.config != null) {
            ConfigurationSection rotationSection = Objects.requireNonNull(this.config.getConfigurationSection("rotation"));
            rotationSection.set("yaw", yaw);
            rotationSection.set("pitch", pitch);
            this.save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.setRotation(yaw, pitch, onlinePlayer);
        }

    }
    @Override
    public void setRotation(float yaw, float pitch, Player player) {
        this.packets.setRotation(this.displayId, yaw, pitch, player);
    }

    @Override
    public void setGlowing(boolean isGlowing) {
        this.isGlowing = isGlowing;
        if (this.config != null) {
            ConfigurationSection glowSection = Objects.requireNonNull(this.config.getConfigurationSection("glow"));
            glowSection.set("glowing", isGlowing);
            this.save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.setGlowing(isGlowing, onlinePlayer);
        }
    }

    @Override
    public void setGlowing(boolean isGlowing, Player player) {
        this.packets.setGlowing(this.displayId, isGlowing, this.glowColor, player);
    }

    @Override
    public void setGlowColor(Color color) {
        this.glowColor = color;
        if (this.config != null) {
            ConfigurationSection glowSection = Objects.requireNonNull(this.config.getConfigurationSection("glow"));
            glowSection.set("color", color.getRed() + ";" + color.getGreen() + ";" + color.getBlue());
            this.save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.setGlowColor(color, onlinePlayer);
        }
    }

    @Override
    public void setGlowColor(Color color, Player player) {
        this.packets.setGlowing(this.displayId, this.isGlowing, color, player);
    }

    @Override
    public void setHitboxSize(boolean override, float width, float height) {
        this.overrideHitboxSize = override;
        this.hitboxWidth = width;
        this.hitboxHeight = height;

        if (this.config != null) {
            ConfigurationSection hitboxSection = Objects.requireNonNull(this.config.getConfigurationSection("hitbox"));
            hitboxSection.set("override", override);
            hitboxSection.set("width", width);
            hitboxSection.set("height", height);
            this.save();
        }

        if (override) {
            this.hitbox.setInteractionWidth(transformation.getScale().x);
            this.hitbox.setInteractionHeight(transformation.getScale().y);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                this.packets.setInteractionSize(this.hitbox.getEntityId(), width, height, onlinePlayer);
            }
        }
    }

    @Override
    public float getHitboxWidth() {
        return this.hitboxWidth;
    }

    @Override
    public float getHitboxHeight() {
        return this.hitboxHeight;
    }

    @Override
    public void setClickActions(DisplayActions actions) {
        this.actionsHandler.setClickActions(actions);
    }

    public void runActions(Player player, ClickType clickType) {
        this.actionsHandler.runActions(player, clickType);
    }

    public void spawnToPlayer(Player player) {
        this.packets.spawnEntity(this.display, player);
        this.packets.spawnEntity(this.hitbox, player);
        ((DisplayMethods) this).sendMetadataPackets(player);
    }

    public void remove() {
        this.packets.removeEntity(this.displayId);
        this.packets.removeEntity(this.hitbox.getEntityId());
    }
    public int getInteractionId() {
        return this.hitbox.getEntityId();
    }
    public boolean isApi() {
        return this.isApi;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    protected void save() {
        this.configManager.save();
    }
}