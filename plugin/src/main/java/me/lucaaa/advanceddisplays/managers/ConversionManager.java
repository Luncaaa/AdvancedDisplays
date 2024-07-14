package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.common.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Display;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class ConversionManager {
    private static boolean conversionNeeded;

    public static void setConversionNeeded(boolean needsConversion) {
        conversionNeeded = needsConversion;

        if (!conversionNeeded) return;

        Logger.log(Level.WARNING, "The displays configuration files are from an older version and have been changed in newer versions.");
        Logger.log(Level.WARNING, "Run the command \"/ad convert\" to update the configuration files to newer versions.");
        Logger.log(Level.WARNING, "Not converting the configurations will cause the plugin to malfunction. See more information at lucaaa.gitbook.io/advanceddisplays/usage/commands-and-permissions/convert-subcommand");
    }

    public static boolean isConversionNeeded() {
        return conversionNeeded;
    }

    public static void convert(AdvancedDisplays plugin, File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection settingsSection;
        if (config.contains("settings")) {
            settingsSection = config.getConfigurationSection("settings");
        } else {
            settingsSection = config.createSection("settings");
        }
        assert settingsSection != null;

        // From version 1.0
        if (config.getString("block") != null) {
            config.set("type", DisplayType.BLOCK.name());
            settingsSection.set("block", config.getString("block"));
            config.set("block", null);

        } else if (config.getString("item") != null) {
            config.set("type", DisplayType.ITEM.name());
            settingsSection.set("item", config.getString("item"));
            settingsSection.set("itemTransformation", config.getString("itemTransformation"));
            config.set("item", null);
            config.set("itemTransformation", null);

        } else if (config.getString("text") != null) {
            config.set("type", DisplayType.TEXT.name());
            String[] oldTextSpaced = config.getString("text", "Error! No old text found.").split("\\n");
            settingsSection.createSection("texts").set("0", oldTextSpaced);
            settingsSection.set("alignment", config.getString("alignment"));
            settingsSection.set("backgroundColor", config.getString("backgroundColor") + ";255");
            settingsSection.set("lineWidth", config.getInt("lineWidth"));
            settingsSection.set("textOpacity", config.getInt("textOpacity"));
            settingsSection.set("defaultBackground", config.getBoolean("defaultBackground"));
            settingsSection.set("seeThrough", config.getBoolean("seeThrough"));
            config.set("text", null);
            config.set("alignment", null);
            config.set("backgroundColor", null);
            config.set("lineWidth", null);
            config.set("textOpacity", null);
            config.set("defaultBackground", null);
            config.set("seeThrough", null);
        }

        DisplayType type = DisplayType.valueOf(config.getString("type"));

        if (type == DisplayType.BLOCK) {
            if (!settingsSection.contains("blockData")) {
                ConfigurationSection dataSection = settingsSection.createSection("blockData");
                BlockData block = Objects.requireNonNull(Material.getMaterial(Objects.requireNonNull(settingsSection.getString("block")))).createBlockData();
                if (block.getAsString().indexOf("[") > 0) {
                    String fullData = block.getAsString().substring(block.getAsString().indexOf("[") + 1, block.getAsString().lastIndexOf("]"));
                    for (String data : fullData.split(",")) {
                        String[] dataPart = data.split("=");
                        dataSection.set(dataPart[0], dataPart[1]);
                    }
                }
                settingsSection.setComments("blockData", List.of("For more information about what these values are, visit https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/data/BlockData.html"));
            }

        } else if (type == DisplayType.TEXT) {
            if (!settingsSection.contains("animationTime")) settingsSection.set("animationTime", 20);
            if (!settingsSection.contains("refreshTime")) settingsSection.set("refreshTime", 20);

            ConfigurationSection textSection = settingsSection.createSection("texts");
            if (settingsSection.isList("text")) {
                List<String> oldTextLines = settingsSection.getStringList("text");
                for (int i = 1; (i - 1) < oldTextLines.size(); i++) {
                    textSection.set(String.valueOf(i), oldTextLines.get(i-1).split("\\n"));
                }

            } else if (settingsSection.isString("text")) {
                String[] separatedText = settingsSection.getString("text", "Error! No \"text\" section found.").split("\\n");
                textSection.set("0", separatedText);
            }
            settingsSection.set("text", null);

        } else if (type == DisplayType.ITEM) {
            if (!settingsSection.contains("enchanted")) settingsSection.set("enchanted", false);
        }

        if (!config.isString("permission")) {
            config.set("permission", "none");
        }

        if (!config.isDouble("view-distance")) {
            config.set("view-distance", 0.0);
        }

        if (!config.contains("glow")) {
            ConfigurationSection glowSection = Objects.requireNonNull(config.createSection("glow"));
            glowSection.set("glowing", false);
            glowSection.set("color", "255;170;0");
        }

        if (!config.contains("hitbox")) {
            ConfigurationSection hitboxSection = config.createSection("hitbox");
            hitboxSection.set("override", false);
            hitboxSection.set("width", 1.0f);
            hitboxSection.set("height", 1.0f);
            config.setComments("hitbox", Arrays.asList("Displays don't have hitboxes of their own, so to have click actions independent entities have to be created.", "These settings allow you to control the hitbox of the display.", "(Use F3 + B to see the hitboxes)"));
        }

        if (config.contains("id")) {
            Display display = (Display) Bukkit.getEntity(UUID.fromString(Objects.requireNonNull(config.getString("id"))));
            Objects.requireNonNull(display).remove();
            config.set("id", null);
        }

        plugin.getMainConfig().getConfig().set("text-update", null);
        plugin.getMainConfig().save();

        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}