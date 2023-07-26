package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.displays.*;
import me.lucaaa.advanceddisplays.utils.ConfigAxisAngle4f;
import me.lucaaa.advanceddisplays.utils.ConfigVector3f;
import me.lucaaa.advanceddisplays.displays.DisplayType;
import me.lucaaa.advanceddisplays.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class DisplaysManager {
    private final Plugin plugin;
    private final String configsFolder;
    public final HashMap<String, BaseDisplay> displays = new HashMap<>();

    public DisplaysManager(Plugin plugin, String configsFolder) {
        this.plugin = plugin;
        this.configsFolder = configsFolder;

        // Gets the displays folder and creates it if it doesn't exist.
        File displaysFolder = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + configsFolder);
        if (!displaysFolder.exists()) {
            displaysFolder.mkdirs();
        }

        // If the displays folder is not empty, load the displays.
        for (File configFile : Objects.requireNonNull(displaysFolder.listFiles())) {
            ConfigManager configManager = new ConfigManager(this.plugin, configsFolder + File.separator + configFile.getName());
            YamlConfiguration config = configManager.getConfig();
            if (config.getString("id") != null
            || (DisplayType.valueOf(config.getString("type")) == DisplayType.TEXT && config.getConfigurationSection("settings").get("text") instanceof String)) {
                AdvancedDisplays.needsConversion = true;
                Logger.log(Level.WARNING, "The displays configuration files are from an older version and have been changed in newer versions.");
                Logger.log(Level.WARNING, "Run the command \"/ad convert [previous version]\" in-game to update the configuration files to newer versions.");
                Logger.log(Level.WARNING, "Not converting the configurations will cause commands to malfunction. See more information at lucaaa.gitbook.io/advanceddisplays/usage/commands-and-permissions/convert-subcommand");
                break;
            }

            this.loadDisplay(configManager);
        }
    }

    public BaseDisplay createDisplay(Location location, DisplayType type, String name, String value) {
        if (displays.containsKey(name)) {
            return null;
        }

        ConfigManager displayConfigManager = new ConfigManager(this.plugin, this.configsFolder + File.separator + name + ".yml");
        YamlConfiguration displayConfig = displayConfigManager.getConfig();

        // Set properties in the display file.
        displayConfig.set("type", type.name());

        ConfigurationSection locationSection = displayConfig.createSection("location");
        locationSection.set("world", location.getWorld().getName());
        locationSection.set("x", location.getX());
        locationSection.set("y", location.getY());
        locationSection.set("z", location.getZ());

        displayConfig.set("rotationType", org.bukkit.entity.Display.Billboard.CENTER.name());

        ConfigurationSection brightnessSection = displayConfig.createSection("brightness");
        brightnessSection.set("block", 15);
        brightnessSection.set("sky", 15);

        ConfigurationSection shadowSection = displayConfig.createSection("shadow");
        shadowSection.set("radius", 5);
        shadowSection.set("strength", 1);

        ConfigurationSection transformationSection = displayConfig.createSection("transformation");
        transformationSection.createSection("translation", new ConfigVector3f().serialize());
        transformationSection.createSection("leftRotation", new ConfigAxisAngle4f().serialize());
        transformationSection.createSection("scale", new ConfigVector3f(1.0f, 1.0f, 1.0f).serialize());
        transformationSection.createSection("rightRotation", new ConfigAxisAngle4f().serialize());

        ConfigurationSection rotationSection = displayConfig.createSection("rotation");
        rotationSection.set("yaw", 0.0);
        rotationSection.set("pitch", 0.0);

        BaseDisplay newDisplay = null;
        switch (type) {
            case BLOCK -> {
                BlockDisplay newDisplayPacket = AdvancedDisplays.packetsManager.getPackets().createBlockDisplay(location);
                newDisplay = new ADBlockDisplay(displayConfigManager, newDisplayPacket).create(Objects.requireNonNull(Material.getMaterial(value)).createBlockData());
            }
            case TEXT -> {
                TextDisplay newDisplayPacket = AdvancedDisplays.packetsManager.getPackets().createTextDisplay(location);
                newDisplay = new ADTextDisplay(displayConfigManager, newDisplayPacket).create(List.of(value));
            }
            case ITEM -> {
                ItemDisplay newDisplayPacket = AdvancedDisplays.packetsManager.getPackets().createItemDisplay(location);
                newDisplay = new ADItemDisplay(displayConfigManager, newDisplayPacket).create(Objects.requireNonNull(Material.getMaterial(value)));
            }
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            newDisplay.sendBaseMetadataPackets(onlinePlayer);
        }

        displayConfigManager.save();
        this.displays.put(name, newDisplay);
        return newDisplay;
    }

    public boolean removeDisplay(String name) {
        if (!this.displays.containsKey(name)) {
            return false;
        }

        File displayFileConfig = new ConfigManager(this.plugin, this.configsFolder + File.separator + name +".yml").getFile();
        displayFileConfig.delete();
        BaseDisplay display = this.displays.get(name);
        if (display instanceof ADTextDisplay) ((ADTextDisplay) display).stopRunnable();
        AdvancedDisplays.packetsManager.getPackets().removeDisplay(display.getDisplayId());
        this.displays.remove(name);
        return true;
    }

    public void removeAllEntities() {
        for (BaseDisplay display : this.displays.values()) {
            AdvancedDisplays.packetsManager.getPackets().removeDisplay(display.getDisplayId());
            if (display instanceof ADTextDisplay) {
                ((ADTextDisplay) display).stopRunnable();
            }
        }
    }

    public BaseDisplay getDisplayFromMap(String name) {
        return this.displays.get(name);
    }

    public void spawnDisplays(Player player) {
        for (BaseDisplay display : this.displays.values()) {
            AdvancedDisplays.packetsManager.getPackets().spawnDisplay(display.getDisplay(), player);
            ((DisplayMethods) display).sendMetadataPackets(player);
        }
    }

    public void reloadDisplay(String name) {
        if (this.displays.get(name) instanceof ADTextDisplay) ((ADTextDisplay) this.displays.get(name)).stopRunnable();
        this.displays.remove(name);
        this.loadDisplay(new ConfigManager(this.plugin, this.configsFolder + File.separator + name + ".yml"));
    }

    public void loadDisplay(ConfigManager configManager) {
        DisplayType displayType = DisplayType.valueOf(configManager.getConfig().getString("type"));
        ConfigurationSection locationSection = Objects.requireNonNull(configManager.getConfig().getConfigurationSection("location"));
        String world = locationSection.getString("world", "world");
        double x = locationSection.getDouble("x");
        double y = locationSection.getDouble("y");
        double z = locationSection.getDouble("z");
        Location location = new Location(Bukkit.getWorld(world), x, y, z);

        BaseDisplay newDisplay = null;
        switch (displayType) {
            case BLOCK -> {
                BlockDisplay newDisplayPacket = AdvancedDisplays.packetsManager.getPackets().createBlockDisplay(location);
                newDisplay = new ADBlockDisplay(configManager, newDisplayPacket);
            }
            case TEXT -> {
                TextDisplay newDisplayPacket = AdvancedDisplays.packetsManager.getPackets().createTextDisplay(location);
                newDisplay = new ADTextDisplay(configManager, newDisplayPacket);
            }
            case ITEM -> {
                ItemDisplay newDisplayPacket = AdvancedDisplays.packetsManager.getPackets().createItemDisplay(location);
                newDisplay = new ADItemDisplay(configManager, newDisplayPacket);
            }
        }

        this.displays.put(configManager.getFile().getName().replace(".yml", ""), newDisplay);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            ((DisplayMethods) newDisplay).sendMetadataPackets(onlinePlayer);
        }
    }
}
