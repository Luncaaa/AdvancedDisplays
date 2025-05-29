package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.managers.DisplaysManager;
import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.data.ConfigAxisAngle4f;
import me.lucaaa.advanceddisplays.data.ConfigVector3f;
import me.lucaaa.advanceddisplays.nms_common.Metadata;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.util.Transformation;

import java.util.Arrays;
import java.util.Objects;

public class ADBaseDisplay extends ADBaseEntity implements BaseDisplay {
    protected ConfigurationSection displaySection = null;
    protected ConfigurationSection settings = null;

    private Display.Billboard billboard;
    private Display.Brightness brightness;
    private float shadowRadius;
    private float shadowStrength;
    private Transformation transformation;
    private Interaction hitbox;
    private boolean overrideHitboxSize;
    private float hitboxWidth;
    private float hitboxHeight;
    private Color glowColorOverride;

    protected ADBaseDisplay(AdvancedDisplays plugin, DisplaysManager displaysManager, ConfigManager config, String name, DisplayType type, EntityType entityType) {
        super(plugin, displaysManager, config, name, type, entityType);

        displaySection = config.getSection("display", false, config.getConfig());
        settings = config.getSection("settings", false, displaySection);

        this.billboard = Display.Billboard.valueOf(config.getOrDefault("billboard", "FIXED", displaySection));

        ConfigurationSection brightnessSection = config.getSection("brightness", displaySection);
        this.brightness = new Display.Brightness(config.getOrDefault("block", 15, brightnessSection), config.getOrDefault("sky", 15, brightnessSection));

        ConfigurationSection shadowSection = config.getSection("shadow", displaySection);
        this.shadowRadius = (float) config.getOrDefault("radius", 1.0, shadowSection).doubleValue();
        this.shadowStrength = (float) config.getOrDefault("strength", 1.0, shadowSection).doubleValue();

        ConfigurationSection transformationSection = config.getSection("transformation", displaySection);
        this.transformation = new Transformation(
                new ConfigVector3f(config.getSection("translation", transformationSection).getValues(false)).toVector3f(),
                new ConfigAxisAngle4f(config.getSection("leftRotation", transformationSection).getValues(false)).toAxisAngle4f(),
                new ConfigVector3f(config.getSection("scale", transformationSection).getValues(false)).toVector3f(),
                new ConfigAxisAngle4f(config.getSection("rightRotation", transformationSection).getValues(false)).toAxisAngle4f()
        );

        Location location1 = location.clone();
        if (type == DisplayType.BLOCK) {
            double x1 = transformation.getScale().x / 2;
            double z1 = transformation.getScale().z / 2;
            location1.add(x1, 0.0, z1);
        }
        this.hitbox = (Interaction) packets.createEntity(EntityType.INTERACTION, location1);

        ConfigurationSection hitboxSection = config.getSection("hitbox", displaySection);
        this.overrideHitboxSize = config.getOrDefault("override", false, hitboxSection);
        this.hitboxWidth = (float) config.getOrDefault("width", 1.0, hitboxSection).doubleValue();
        this.hitboxHeight = (float) config.getOrDefault("height", 1.0, hitboxSection).doubleValue();

        String[] colorParts = config.getOrDefault("glowColorOverride", "255;170;0", displaySection).split(";");
        this.glowColorOverride = Color.fromRGB(Integer.parseInt(colorParts[0]), Integer.parseInt(colorParts[1]), Integer.parseInt(colorParts[2]));
    }

    protected ADBaseDisplay(AdvancedDisplays plugin, DisplaysManager displaysManager, String name, Location location, DisplayType type, EntityType entityType, boolean saveToConfig) {
        super(plugin, displaysManager, name, type, entityType, location, saveToConfig);
        Display display = (Display) entity;

        this.billboard = Display.Billboard.CENTER; // Text displays will be easier to spot.
        this.brightness = new Display.Brightness(15, 15);
        this.shadowRadius = display.getShadowRadius();
        this.shadowStrength = display.getShadowStrength();
        this.transformation = display.getTransformation();

        Location location1 = location.clone();
        if (this.type == DisplayType.BLOCK) {
            double x1 = transformation.getScale().x / 2;
            double z1 = transformation.getScale().z / 2;
            location1.add(x1, 0.0, z1);
        }
        this.hitbox = (Interaction) packets.createEntity(EntityType.INTERACTION, location1);
        this.overrideHitboxSize = false;
        this.hitboxWidth = transformation.getScale().x;
        this.hitboxHeight = transformation.getScale().z;
        this.glowColorOverride = Color.ORANGE;

        // Even though it may have been set by the createConfig() method, they are set back to "null"
        // Again when the body of this constructor starts running.
        if (config != null) {
            displaySection = config.getSection("display", false, config.getConfig());
            settings = config.getSection("settings", false, displaySection);
        }
    }

    @Override
    protected ConfigManager createConfig(Location location) {
        ConfigManager config = super.createConfig(location);
        YamlConfiguration displayConfig = config.getConfig();

        // Set properties in the display file.
        displaySection = displayConfig.createSection("display");
        settings = displaySection.createSection("settings");

        displaySection.set("billboard", org.bukkit.entity.Display.Billboard.CENTER.name());

        ConfigurationSection brightnessSection = displaySection.createSection("brightness");
        brightnessSection.set("block", 15);
        brightnessSection.set("sky", 15);

        ConfigurationSection shadowSection = displaySection.createSection("shadow");
        shadowSection.set("radius", 5.0);
        shadowSection.set("strength", 1.0);

        ConfigurationSection transformationSection = displaySection.createSection("transformation");
        transformationSection.createSection("translation", new ConfigVector3f().serialize());
        transformationSection.createSection("leftRotation", new ConfigAxisAngle4f().serialize());
        transformationSection.createSection("scale", new ConfigVector3f(1.0f, 1.0f, 1.0f).serialize());
        transformationSection.createSection("rightRotation", new ConfigAxisAngle4f().serialize());

        ConfigurationSection hitboxSection = displaySection.createSection("hitbox");
        hitboxSection.set("override", false);
        hitboxSection.set("width", 1.0f);
        hitboxSection.set("height", 1.0f);
        displaySection.setComments("hitbox", Arrays.asList("Displays don't have hitboxes of their own, so to have click actions independent entities have to be created.", "These settings allow you to control the hitbox of the display.", "(Use F3 + B to see the hitboxes)"));

        displaySection.set("glowColorOverride", "255;170;0");

        config.save();
        return config;
    }

    @Override
    public void sendMetadataPackets(Player player) {
        super.sendMetadataPackets(player);
        if (!overrideHitboxSize) {
            packets.setMetadata(hitbox.getEntityId(), player,
                    new Metadata.DataPair<>(metadata.HITBOX_WIDTH, transformation.getScale().x),
                    new Metadata.DataPair<>(metadata.HITBOX_HEIGHT, transformation.getScale().y)
            );
        } else {
            packets.setMetadata(hitbox.getEntityId(), player,
                    new Metadata.DataPair<>(metadata.HITBOX_WIDTH, hitboxWidth),
                    new Metadata.DataPair<>(metadata.HITBOX_HEIGHT, hitboxHeight)
            );
        }

        packets.setMetadata(entityId, player,
                new Metadata.DataPair<>(metadata.TRANSLATION, transformation.getTranslation()),
                new Metadata.DataPair<>(metadata.SCALE, transformation.getScale()),
                new Metadata.DataPair<>(metadata.LEFT_ROTATION, transformation.getLeftRotation()),
                new Metadata.DataPair<>(metadata.RIGHT_ROTATION, transformation.getRightRotation()),
                new Metadata.DataPair<>(metadata.BILLBOARD, Metadata.getBillboardByte(billboard)),
                new Metadata.DataPair<>(metadata.BRIGHTNESS, brightness.getBlockLight() << 4 | brightness.getSkyLight() << 20),
                new Metadata.DataPair<>(metadata.SHADOW_RADIUS, shadowRadius),
                new Metadata.DataPair<>(metadata.SHADOW_STRENGTH, shadowStrength),
                new Metadata.DataPair<>(metadata.PROPERTIES, (byte) (isGlowing ? 0x40 : 0)),
                new Metadata.DataPair<>(metadata.GLOW_COLOR, glowColorOverride.asRGB())
        );
    }

    @Override
    public int getInteractionId() {
        return hitbox.getEntityId();
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
            Location location1 = location.clone();
            if (type == DisplayType.BLOCK) {
                double x1 = transformation.getScale().x / 2;
                double z1 = transformation.getScale().z / 2;
                location1.add(x1, 0.0, z1);
            }

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                packets.setLocation(entity, location, onlinePlayer);
                packets.setLocation(hitbox, location1, onlinePlayer);
            }
        } else {
            // Because entities cannot be teleported across worlds, the old one is removed and a new one is created
            // in the new location (another world)
            packets.removeEntity(entityId);
            packets.removeEntity(hitbox.getEntityId());
            plugin.getInteractionsManager().removeInteraction(getInteractionId());

            entity = packets.createEntity(entityType, location);
            entityId = entity.getEntityId();
            Location location1 = location.clone();
            if (this.type == DisplayType.BLOCK) {
                double x1 = transformation.getScale().x / 2;
                double z1 = transformation.getScale().z / 2;
                location1.add(x1, 0.0, z1);
            }
            hitbox = (Interaction) packets.createEntity(EntityType.INTERACTION, location1);

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (this instanceof ADTextDisplay textDisplay) {
                    textDisplay.restartRunnable();
                }
                sendMetadataPackets(onlinePlayer);
            }

            plugin.getInteractionsManager().addInteraction(getInteractionId(), this);
        }
        this.location = location;
    }

    @Override
    public Display.Billboard getBillboard() {
        return billboard;
    }
    @Override
    public void setBillboard(Display.Billboard billboard) {
        this.billboard = billboard;
        if (config != null) {
            displaySection.set("billboard", billboard.name());
            save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setBillboard(billboard, onlinePlayer);
        }
    }
    @Override
    public void setBillboard(Display.Billboard billboard, Player player) {
        packets.setMetadata(entityId, player, metadata.BILLBOARD, Metadata.getBillboardByte(billboard));
    }

    @Override
    public Display.Brightness getBrightness() {
        return brightness;
    }
    @Override
    public void setBrightness(Display.Brightness brightness) {
        this.brightness = brightness;
        if (config != null) {
            ConfigurationSection brightnessSection = config.getSection("brightness", displaySection);
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
        packets.setMetadata(entityId, player, metadata.BRIGHTNESS, brightness.getBlockLight() << 4 | brightness.getSkyLight() << 20);
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
            ConfigurationSection shadowSection = config.getSection("shadow", displaySection);
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
        packets.setMetadata(entityId, player,
                new Metadata.DataPair<>(metadata.SHADOW_RADIUS, shadowRadius),
                new Metadata.DataPair<>(metadata.SHADOW_STRENGTH, shadowStrength)
        );
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
            ConfigurationSection transformationSection = config.getSection("transformation", displaySection);
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
            packets.setLocation(hitbox, location, onlinePlayer);
        }

    }
    @Override
    public void setTransformation(Transformation transformation, Player player) {
        packets.setMetadata(entityId, player,
                new Metadata.DataPair<>(metadata.TRANSLATION, transformation.getTranslation()),
                new Metadata.DataPair<>(metadata.SCALE, transformation.getScale()),
                new Metadata.DataPair<>(metadata.LEFT_ROTATION, transformation.getLeftRotation()),
                new Metadata.DataPair<>(metadata.RIGHT_ROTATION, transformation.getRightRotation())
        );

        if (!overrideHitboxSize) {
            packets.setMetadata(hitbox.getEntityId(), player,
                    new Metadata.DataPair<>(metadata.HITBOX_WIDTH, transformation.getScale().x),
                    new Metadata.DataPair<>(metadata.HITBOX_HEIGHT, transformation.getScale().y)
            );
        }
    }

    @Override
    public void setHitboxSize(boolean override, float width, float height) {
        overrideHitboxSize = override;
        hitboxWidth = (override) ? width : transformation.getScale().x;
        hitboxHeight = (override) ? height : transformation.getScale().y;

        if (config != null) {
            ConfigurationSection hitboxSection = config.getSection("hitbox", displaySection);
            hitboxSection.set("override", override);
            hitboxSection.set("width", width);
            hitboxSection.set("height", height);
            save();
        }

        hitbox.setInteractionWidth(hitboxWidth);
        hitbox.setInteractionHeight(hitboxHeight);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            packets.setMetadata(hitbox.getEntityId(), onlinePlayer,
                    new Metadata.DataPair<>(metadata.HITBOX_WIDTH, hitboxWidth),
                    new Metadata.DataPair<>(metadata.HITBOX_HEIGHT, hitboxHeight)
            );
        }
    }

    @Override
    public Color getGlowColorOverride() {
        return glowColorOverride;
    }

    @Override
    public void setGlowColorOverride(Color color) {
        glowColorOverride = color;
        if (config != null) {
            displaySection.set("glowColorOverride", color.getRed() + ";" + color.getGreen() + ";" + color.getBlue());
            save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setGlowColorOverride(color, onlinePlayer);
        }
    }

    @Override
    public void setGlowColorOverride(Color color, Player player) {
        packets.setMetadata(entityId, player,
                new Metadata.DataPair<>(metadata.PROPERTIES, (byte) (isGlowing ? 0x40 : 0)),
                new Metadata.DataPair<>(metadata.GLOW_COLOR, glowColorOverride.asRGB())
        );
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
    public void setGlowing(boolean isGlowing, Player player) {
        packets.setMetadata(entityId, player,
                new Metadata.DataPair<>(metadata.PROPERTIES, (byte) (isGlowing ? 0x40 : 0)),
                new Metadata.DataPair<>(metadata.GLOW_COLOR, glowColorOverride.asRGB())
        );
    }

    @Override
    public void spawnToPlayer(Player player) {
        packets.spawnEntity(hitbox, player);
        super.spawnToPlayer(player); // Run last for the sendMetadataPackets method.
    }

    @Override
    public void removeToPlayer(Player player) {
        super.removeToPlayer(player);
        packets.removeEntity(getInteractionId(), player);
    }

    @Override
    public void destroy() {
        super.destroy();
        plugin.getInteractionsManager().removeInteraction(getInteractionId());
        packets.removeEntity(getInteractionId());
    }
}