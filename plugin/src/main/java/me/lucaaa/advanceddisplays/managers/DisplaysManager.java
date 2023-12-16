package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.displays.*;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.common.managers.ConfigManager;
import me.lucaaa.advanceddisplays.common.managers.ConversionManager;
import me.lucaaa.advanceddisplays.common.utils.ConfigAxisAngle4f;
import me.lucaaa.advanceddisplays.common.utils.ConfigVector3f;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;

public class DisplaysManager {
    private final Plugin plugin;
    private final String configsFolder;
    public final HashMap<String, ADBaseDisplay> displays = new HashMap<>();

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
            if (configFile.isDirectory()) continue;
            ConfigManager configManager = new ConfigManager(this.plugin, configsFolder + File.separator + configFile.getName());
            YamlConfiguration config = configManager.getConfig();
            if (config.getString("id") != null
            || (DisplayType.valueOf(config.getString("type")) == DisplayType.ITEM && Objects.requireNonNull(config.getConfigurationSection("settings")).get("enchanted") == null)) {
                ConversionManager.setConversionNeeded(true);
                break;
            }

            this.loadDisplay(configManager);
        }
    }

    public ADBaseDisplay createDisplay(Location location, DisplayType type, String name, String value) {
        if (displays.containsKey(name)) {
            return null;
        }

        ConfigManager displayConfigManager = new ConfigManager(this.plugin, this.configsFolder + File.separator + name + ".yml");
        YamlConfiguration displayConfig = displayConfigManager.getConfig();

        // Set properties in the display file.
        displayConfig.set("type", type.name());

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

        ADBaseDisplay newDisplay = null;
        switch (type) {
            case BLOCK -> {
                BlockDisplay newDisplayPacket = AdvancedDisplays.packetsManager.getPackets().createBlockDisplay(location);
                newDisplay = new ADBlockDisplay(displayConfigManager, newDisplayPacket).create(Objects.requireNonNull(Material.getMaterial(value)).createBlockData());
            }
            case TEXT -> {
                TextDisplay newDisplayPacket = AdvancedDisplays.packetsManager.getPackets().createTextDisplay(location);
                newDisplay = new ADTextDisplay(displayConfigManager, newDisplayPacket, this.plugin).create(List.of(value));
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

        ADBaseDisplay display = this.displays.get(name);
        if (display.getConfigManager() != null) {
            display.getConfigManager().getFile().delete();
        }

        if (display instanceof ADTextDisplay) ((ADTextDisplay) display).stopRunnable();
        AdvancedDisplays.packetsManager.getPackets().removeDisplay(display.getDisplayId());
        this.displays.remove(name);
        return true;
    }

    public void removeAllEntities() {
        for (ADBaseDisplay display : this.displays.values()) {
            AdvancedDisplays.packetsManager.getPackets().removeDisplay(display.getDisplayId());
            if (display instanceof ADTextDisplay) {
                ((ADTextDisplay) display).stopRunnable();
            }
        }
    }

    public ADBaseDisplay getDisplayFromMap(String name) {
        return this.displays.get(name);
    }

    public void spawnDisplays(Player player) {
        for (ADBaseDisplay display : this.displays.values()) {
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

        ADBaseDisplay newDisplay = null;
        switch (displayType) {
            case BLOCK -> {
                BlockDisplay newDisplayPacket = AdvancedDisplays.packetsManager.getPackets().createBlockDisplay(location);
                newDisplay = new ADBlockDisplay(configManager, newDisplayPacket);
            }
            case TEXT -> {
                TextDisplay newDisplayPacket = AdvancedDisplays.packetsManager.getPackets().createTextDisplay(location);
                newDisplay = new ADTextDisplay(configManager, newDisplayPacket, this.plugin);
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
