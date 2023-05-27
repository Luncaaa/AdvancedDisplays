package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.displays.ADBlockDisplay;
import me.lucaaa.advanceddisplays.displays.ADItemDisplay;
import me.lucaaa.advanceddisplays.displays.ADTextDisplay;
import me.lucaaa.advanceddisplays.utils.ConfigAxisAngle4f;
import me.lucaaa.advanceddisplays.utils.ConfigVector3f;
import me.lucaaa.advanceddisplays.utils.DisplayType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class DisplaysManager {
    private final Plugin plugin;
    public final HashMap<String, BaseDisplay> displays = new HashMap<>();

    public DisplaysManager(Plugin plugin) {
        this.plugin = plugin;

        // Gets the displays folder and creates it if it doesn't exist.
        File displaysFolder = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "displays");
        if (!displaysFolder.exists()) {
            displaysFolder.mkdirs();
        }

        // If the displays folder is not empty, load the displays.
        // TODO: Do not execute on server start
        for (File file : Objects.requireNonNull(displaysFolder.listFiles())) {
            this.loadEntity(new ConfigManager(plugin, "displays" + File.separator + file.getName()));
        }
    }

    public void loadEntity(ConfigManager config) {
        Entity entity = Bukkit.getEntity(UUID.fromString(Objects.requireNonNull(config.getConfig().getString("id"))));
        String name = config.getFile().getName().replace(".yml", "");

        if (entity == null || this.getDisplay(entity, config) == null) return;

        this.displays.put(name, this.getDisplay(entity, config));
    }

    public void createDisplay(Player p, DisplayType type, String name, Location location, String value) throws IOException {
        if (displays.containsKey(name)) {
            p.sendMessage(MessagesManager.getColoredMessage("&cA display with the name &b" + name + " &calready exists!", true));
            return;
        }

        if (type == DisplayType.BLOCK || type == DisplayType.ITEM) {
            if (Material.getMaterial(value) == null) {
                p.sendMessage(MessagesManager.getColoredMessage("&b" + value + " &cis not a valid material!", true));
                return;
            }
        }

        ConfigManager displayConfigManager = new ConfigManager(this.plugin, "displays" + File.separator + name + ".yml");
        YamlConfiguration displayConfig = displayConfigManager.getConfig();

        // Set properties in the display file.
        ConfigurationSection locationSection = displayConfig.createSection("location");
        locationSection.set("x", location.getX());
        locationSection.set("y", location.getY());
        locationSection.set("z", location.getZ());

        displayConfig.set("rotationType", Display.Billboard.CENTER.name());

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
                BlockDisplay blockDisplay = Objects.requireNonNull(location.getWorld()).spawn(location, BlockDisplay.class);
                try {
                    newDisplay = new ADBlockDisplay(displayConfigManager, blockDisplay).create(Objects.requireNonNull(Material.getMaterial(value)).createBlockData());
                } catch (IllegalArgumentException e) {
                    blockDisplay.remove();
                    p.sendMessage(MessagesManager.getColoredMessage("&cThe block &b" + value + " &cis not a valid block.", true));
                    return;
                }
            }
            case TEXT -> {
                TextDisplay textDisplay = Objects.requireNonNull(location.getWorld()).spawn(location, TextDisplay.class);
                newDisplay = new ADTextDisplay(displayConfigManager, textDisplay).create(value);
            }
            case ITEM -> {
                ItemDisplay itemDisplay = Objects.requireNonNull(location.getWorld()).spawn(location, ItemDisplay.class);
                newDisplay = new ADItemDisplay(displayConfigManager, itemDisplay).create(Objects.requireNonNull(Material.getMaterial(value)));
            }
        }

        displayConfig.set("id", newDisplay.getDisplay().getUniqueId().toString());
        displayConfigManager.save();
        displays.put(name, newDisplay);
        p.sendMessage(MessagesManager.getColoredMessage("&aThe display &e" + name + " &ahas been successfully created.", true));
    }

    public boolean removeDisplay(String name) {
        if (!displays.containsKey(name)) {
            return false;
        }

        File displayFileConfig = new ConfigManager(this.plugin, "displays" + File.separator + name +".yml").getFile();
        displayFileConfig.delete();
        displays.get(name).remove();
        displays.remove(name);
        return true;
    }

    public BaseDisplay getDisplay(Entity entity, ConfigManager config) {
        if (entity instanceof TextDisplay) {
            return new ADTextDisplay(config, (TextDisplay) entity);

        } else if (entity instanceof ItemDisplay) {
            return new ADItemDisplay(config, (ItemDisplay) entity);

        } else if (entity instanceof BlockDisplay) {
            return new ADBlockDisplay(config, (BlockDisplay) entity);

        } else return null;
    }
}
