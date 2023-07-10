package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.common.PacketInterface;
import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.utils.ConfigAxisAngle4f;
import me.lucaaa.advanceddisplays.utils.ConfigVector3f;
import me.lucaaa.advanceddisplays.utils.DisplayType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class BaseDisplay {
    protected PacketInterface packets = AdvancedDisplays.packetsManager.getPackets();
    protected YamlConfiguration config;
    protected File file;
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

    public void sendBaseMetadataPackets(Player player) {
        this.packets.setLocation(this.display, player);
        this.packets.setRotation(this.displayId, this.yaw, this.pitch, player);
        this.packets.setTransformation(this.displayId, this.transformation, player);
        this.packets.setBillboard(this.displayId, this.billboard, player);
        this.packets.setBrightness(this.displayId, this.brightness, player);
        this.packets.setShadow(this.displayId, this.shadowRadius, this.shadowStrength, player);
    }

    public DisplayType getType() {
        return this.type;
    }

    public Location getLocation() {
        return this.location;
    }
    public void setLocation(Location location) {
        this.location = location;
        this.display.teleport(location);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.packets.setLocation(this.display, onlinePlayer);
        }
        ConfigurationSection locationSection = Objects.requireNonNull(this.config.getConfigurationSection("location"));
        locationSection.set("x", location.getX());
        locationSection.set("y", location.getY());
        locationSection.set("z", location.getZ());
        this.save();
    }

    public Display.Billboard getBillboard() {
        return this.billboard;
    }
    public void setBillboard(Display.Billboard billboard) {
        this.billboard = billboard;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.packets.setBillboard(this.displayId, billboard, onlinePlayer);
        }
        this.config.set("rotationType", billboard.name());
        this.save();
    }

    public Display.Brightness getBrightness() {
        return this.brightness;
    }
    public void setBrightness(Display.Brightness brightness) {
        this.brightness = brightness;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.packets.setBrightness(this.displayId, brightness, onlinePlayer);
        }
        ConfigurationSection brightnessSection = Objects.requireNonNull(this.config.getConfigurationSection("brightness"));
        brightnessSection.set("block", brightness.getBlockLight());
        brightnessSection.set("sky", brightness.getSkyLight());
        this.save();
    }

    public float getShadowRadius() {
        return this.shadowRadius;
    }
    public float getShadowStrength() {
        return this.shadowStrength;
    }
    public void setShadow(float shadowRadius, float shadowStrength) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.packets.setShadow(this.displayId, shadowRadius, shadowStrength, onlinePlayer);
        }
        ConfigurationSection shadowSection = Objects.requireNonNull(this.config.getConfigurationSection("shadow"));
        shadowSection.set("radius", shadowRadius);
        shadowSection.set("strength", shadowStrength);
        this.shadowRadius = shadowRadius;
        this.shadowStrength = shadowStrength;
        this.save();
    }

    public Transformation getTransformation() {
        return this.transformation;
    }
    public void setTransformation(Transformation transformation) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.packets.setTransformation(this.displayId, transformation, onlinePlayer);
        }
        this.transformation = transformation;
        ConfigurationSection transformationSection = Objects.requireNonNull(this.config.getConfigurationSection("transformation"));
        transformationSection.createSection("translation", new ConfigVector3f(transformation.getTranslation()).serialize());
        transformationSection.createSection("leftRotation", new ConfigAxisAngle4f(transformation.getLeftRotation()).serialize());
        transformationSection.createSection("scale", new ConfigVector3f(transformation.getScale()).serialize());
        transformationSection.createSection("rightRotation", new ConfigAxisAngle4f(transformation.getRightRotation()).serialize());
        this.save();
    }

    public float getYaw() {
        return this.yaw;
    }
    public float getPitch() {
        return this.pitch;
    }
    public void setRotation(float yaw, float pitch) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.packets.setRotation(this.displayId, yaw, pitch, onlinePlayer);
        }
        ConfigurationSection rotationSection = Objects.requireNonNull(this.config.getConfigurationSection("rotation"));
        rotationSection.set("yaw", yaw);
        rotationSection.set("pitch", pitch);
        this.yaw = yaw;
        this.pitch = pitch;
        this.save();
    }

    public int getDisplayId() {
        return this.displayId;
    }
    public Display getDisplay() {
        return this.display;
    }

    protected void save() {
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}