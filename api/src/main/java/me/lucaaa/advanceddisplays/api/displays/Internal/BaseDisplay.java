package me.lucaaa.advanceddisplays.api.displays.Internal;

import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.common.PacketInterface;
import me.lucaaa.advanceddisplays.common.managers.ConfigManager;
import me.lucaaa.advanceddisplays.common.managers.PacketsManager;
import me.lucaaa.advanceddisplays.common.utils.ConfigAxisAngle4f;
import me.lucaaa.advanceddisplays.common.utils.ConfigVector3f;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;

import java.io.File;
import java.util.Objects;

public class BaseDisplay implements me.lucaaa.advanceddisplays.api.displays.api.BaseDisplay {
    protected final PacketInterface packets = PacketsManager.getPackets();
    protected final ConfigManager configManager;
    protected final YamlConfiguration config;
    protected final File file;
    protected final DisplayType type;

    protected final Display display;
    protected final int displayId;
    private Location location;

    private Display.Billboard billboard;
    private Display.Brightness brightness;
    private float shadowRadius;
    private float shadowStrength;
    private Transformation transformation;

    private float yaw;
    private float pitch;

    public BaseDisplay(DisplayType type, ConfigManager configManager, Display display) {
        this.display = display;
        this.displayId = display.getEntityId();

        this.configManager = configManager;
        this.config = configManager.getConfig();
        this.file = configManager.getFile();
        this.type = type;

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
    }

    public BaseDisplay(DisplayType type, Display display) {
        this.display = display;
        this.displayId = display.getEntityId();
        this.type = type;

        this.configManager = null;
        this.config = null;
        this.file = null;
    }

    public void sendBaseMetadataPackets(Player player) {
        this.packets.setLocation(this.display, player);
        this.packets.setRotation(this.displayId, this.yaw, this.pitch, player);
        this.packets.setTransformation(this.displayId, this.transformation, player);
        this.packets.setBillboard(this.displayId, this.billboard, player);
        this.packets.setBrightness(this.displayId, this.brightness, player);
        this.packets.setShadow(this.displayId, this.shadowRadius, this.shadowStrength, player);
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
        location.setYaw(this.yaw);
        location.setPitch(this.pitch);
        this.location = location;

        if (this.config != null) {
            ConfigurationSection locationSection = Objects.requireNonNull(this.config.getConfigurationSection("location"));
            locationSection.set("world", Objects.requireNonNull(location.getWorld()).getName());
            locationSection.set("x", location.getX());
            locationSection.set("y", location.getY());
            locationSection.set("z", location.getZ());
            this.save();
        }

        if (this.location.getWorld() == location.getWorld()) {
            this.display.teleport(location);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                this.packets.setLocation(this.display, onlinePlayer);
            }
        } else {
            // TODO

            // Entities cannot be teleported across worlds with NMS,
            // so the display will be removed & respawned in the new world.
            /*! this.packets.removeDisplay(this.displayId);
            AdvancedDisplays.displaysManager.reloadDisplay(this.file.getName().replace(".yml", ""));*/

            // todo: improve
            // Maybe try creating new entities (replacing old displayId and display) and send metadata packets.
        }
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
        this.yaw = yaw;
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

    public int getDisplayId() {
        return this.displayId;
    }
    public Display getDisplay() {
        return this.display;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    protected void save() {
        this.configManager.save();
    }
}