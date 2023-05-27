package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.utils.ConfigAxisAngle4f;
import me.lucaaa.advanceddisplays.utils.ConfigVector3f;
import me.lucaaa.advanceddisplays.utils.DisplayType;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class BaseDisplay {
    protected YamlConfiguration config;
    protected File file;
    protected final DisplayType type;

    private final Display display;
    private Location location;

    private Display.Billboard billboard;
    private Display.Brightness brightness;
    // glow color
    private float shadowRadius;
    private float shadowStrength;

    private Transformation transformation;

    private float yaw;
    private float pitch;

    public BaseDisplay(DisplayType type, ConfigManager configManager, Display entity) {
        this.display = entity;

        this.config = configManager.getConfig();
        this.file = configManager.getFile();
        this.type = type;

        ConfigurationSection locationSection = Objects.requireNonNull(this.config.getConfigurationSection("location"));
        double x = locationSection.getDouble("x");
        double y = locationSection.getDouble("y");
        double z = locationSection.getDouble("z");
        this.location = new Location(this.display.getWorld(), x, y, z);
        this.display.teleport(this.location);

        this.billboard = Display.Billboard.valueOf(this.config.getString("rotationType"));
        this.display.setBillboard(this.billboard);

        ConfigurationSection brightnessSection = Objects.requireNonNull(this.config.getConfigurationSection("brightness"));
        this.brightness = new Display.Brightness(brightnessSection.getInt("block"), brightnessSection.getInt("sky"));
        this.display.setBrightness(this.brightness);

        ConfigurationSection shadowSection = Objects.requireNonNull(this.config.getConfigurationSection("shadow"));
        this.shadowRadius = shadowSection.getInt("radius");
        this.shadowStrength = shadowSection.getInt("strength");
        this.display.setShadowRadius(this.shadowRadius);
        this.display.setShadowStrength(this.shadowStrength);

        ConfigurationSection transformationSection = Objects.requireNonNull(this.config.getConfigurationSection("transformation"));
        this.transformation = new Transformation(
                new ConfigVector3f(Objects.requireNonNull(transformationSection.getConfigurationSection("translation")).getValues(false)).toVector3f(),
                new ConfigAxisAngle4f(Objects.requireNonNull(transformationSection.getConfigurationSection("leftRotation")).getValues(false)).toAxisAngle4f(),
                new ConfigVector3f(Objects.requireNonNull(transformationSection.getConfigurationSection("scale")).getValues(false)).toVector3f(),
                new ConfigAxisAngle4f(Objects.requireNonNull(transformationSection.getConfigurationSection("rightRotation")).getValues(false)).toAxisAngle4f()
        );
        this.display.setTransformation(this.transformation);

        ConfigurationSection rotationSection = Objects.requireNonNull(this.config.getConfigurationSection("rotation"));
        this.yaw = (float) rotationSection.getDouble("yaw");
        this.pitch = (float) rotationSection.getDouble("pitch");
        this.display.setRotation(this.yaw, this.pitch);
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
        this.display.setBillboard(billboard);
        this.config.set("rotationType", billboard.name());
        this.save();
    }

    public Display.Brightness getBrightness() {
        return this.brightness;
    }
    public void setBrightness(Display.Brightness brightness) {
        this.brightness = brightness;
        this.display.setBrightness(brightness);
        ConfigurationSection brightnessSection = Objects.requireNonNull(this.config.getConfigurationSection("brightness"));
        brightnessSection.set("block", brightness.getBlockLight());
        brightnessSection.set("sky", brightness.getSkyLight());
        this.save();
    }

    public float getShadowRadius() {
        return this.shadowRadius;
    }
    public void setShadowRadius(float shadowRadius) {
        this.display.setShadowRadius(shadowRadius);
        ConfigurationSection shadowSection = Objects.requireNonNull(this.config.getConfigurationSection("shadow"));
        shadowSection.set("radius", shadowRadius);
        this.shadowRadius = shadowRadius;
        this.save();
    }

    public float getShadowStrength() {
        return this.shadowStrength;
    }
    public void setShadowStrength(float shadowStrength) {
        this.display.setShadowStrength(shadowStrength);
        ConfigurationSection shadowSection = Objects.requireNonNull(this.config.getConfigurationSection("shadow"));
        shadowSection.set("strength", shadowStrength);
        this.shadowStrength = shadowStrength;
        this.save();
    }

    public Transformation getTransformation() {
        return this.transformation;
    }
    public void setTransformation(Transformation transformation) {
        this.display.setTransformation(transformation);
        this.transformation = transformation;
        ConfigurationSection transformationSection = Objects.requireNonNull(this.config.getConfigurationSection("transformation"));
        transformationSection.createSection("translation", new ConfigVector3f(transformation.getTranslation()).serialize());
        transformationSection.createSection("leftRotation", new ConfigAxisAngle4f(transformation.getLeftRotation()).serialize());
        transformationSection.createSection("scale", new ConfigVector3f(transformation.getScale()).serialize());
        transformationSection.createSection("rightRotation", new ConfigAxisAngle4f(transformation.getRightRotation()).serialize());
    }

    public float getYaw() {
        return this.yaw;
    }
    public void setYaw(float yaw) {
        this.display.setRotation(yaw, this.pitch);
        ConfigurationSection shadowSection = Objects.requireNonNull(this.config.getConfigurationSection("rotation"));
        shadowSection.set("yaw", yaw);
        this.yaw = yaw;
        this.save();
    }

    private float getPitch() {
        return this.pitch;
    }
    public void setPitch(float pitch) {
        this.display.setRotation(this.yaw, pitch);
        ConfigurationSection shadowSection = Objects.requireNonNull(this.config.getConfigurationSection("rotation"));
        shadowSection.set("pitch", pitch);
        this.pitch = pitch;
        this.save();
    }

    public Display getDisplay() {
        return this.display;
    }

    public void remove() {
        this.display.remove();
    }

    protected void save() {
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}