package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.displays.*;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.common.managers.ConfigManager;
import me.lucaaa.advanceddisplays.common.utils.ConfigAxisAngle4f;
import me.lucaaa.advanceddisplays.common.utils.ConfigVector3f;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
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

    public DisplaysManager(String configsFolder, boolean createFolders) {
        this.plugin = AdvancedDisplays.getPlugin();
        this.configsFolder = configsFolder;

        // Gets the displays folder and creates it if it doesn't exist.
        File displaysFolder = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + configsFolder);
        if (!displaysFolder.exists() && createFolders) displaysFolder.mkdirs();

        // If the displays folder is not empty, load the displays.
        if (displaysFolder.exists()) {
            for (File configFile : Objects.requireNonNull(displaysFolder.listFiles())) {
                if (configFile.isDirectory()) continue;
                ConfigManager configManager = new ConfigManager(this.plugin, configsFolder + File.separator + configFile.getName());
                this.loadDisplay(configManager);
            }
        }
    }

    private ConfigManager createConfigManager(String name, DisplayType type, Location location) {
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

        return displayConfigManager;
    }

    public ADTextDisplay createTextDisplay(Location location, String name, List<String> value, boolean saveToConfig) {
        if (this.displays.containsKey(name)) {
            return null;
        }

        TextDisplay newDisplayPacket = AdvancedDisplays.packetsManager.getPackets().createTextDisplay(location);
        ADTextDisplay textDisplay;

        if (saveToConfig) {
            ConfigManager configManager = this.createConfigManager(name, DisplayType.TEXT, location);
            textDisplay = new ADTextDisplay(configManager, newDisplayPacket).create(value);
            configManager.save();
        } else {
            textDisplay = new ADTextDisplay(newDisplayPacket).create(value);
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            textDisplay.sendBaseMetadataPackets(onlinePlayer);
        }

        this.displays.put(name, textDisplay);
        return textDisplay;
    }

    public ADItemDisplay createItemDisplay(Location location, String name, Material value, boolean saveToConfig) {
        if (this.displays.containsKey(name)) {
            return null;
        }

        ItemDisplay newDisplayPacket = AdvancedDisplays.packetsManager.getPackets().createItemDisplay(location);
        ADItemDisplay itemDisplay;

        if (saveToConfig) {
            ConfigManager configManager = this.createConfigManager(name, DisplayType.ITEM, location);
            itemDisplay = new ADItemDisplay(configManager, newDisplayPacket).create(value);
            configManager.save();
        } else {
            itemDisplay = new ADItemDisplay(newDisplayPacket).create(value);
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            itemDisplay.sendBaseMetadataPackets(onlinePlayer);
        }

        this.displays.put(name, itemDisplay);
        return itemDisplay;
    }

    public ADBlockDisplay createBlockDisplay(Location location, String name, BlockData value, boolean saveToConfig) {
        if (this.displays.containsKey(name)) {
            return null;
        }

        BlockDisplay newDisplayPacket = AdvancedDisplays.packetsManager.getPackets().createBlockDisplay(location);
        ADBlockDisplay blockDisplay;

        if (saveToConfig) {
            ConfigManager configManager = this.createConfigManager(name, DisplayType.BLOCK, location);
            blockDisplay = new ADBlockDisplay(configManager, newDisplayPacket).create(value);
            configManager.save();
        } else {
            blockDisplay = new ADBlockDisplay(newDisplayPacket).create(value);
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            blockDisplay.sendBaseMetadataPackets(onlinePlayer);
        }

        this.displays.put(name, blockDisplay);
        return blockDisplay;
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
