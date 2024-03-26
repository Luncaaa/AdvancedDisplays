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
        if (needsConversion) {
            Logger.log(Level.WARNING, "The displays configuration files are from an older version and have been changed in newer versions.");
            Logger.log(Level.WARNING, "Run the command \"/ad convert [previous version]\" to update the configuration files to newer versions.");
            Logger.log(Level.WARNING, "Not converting the configurations will cause the plugin to malfunction. See more information at lucaaa.gitbook.io/advanceddisplays/usage/commands-and-permissions/convert-subcommand");
        }
        conversionNeeded = needsConversion;
    }

    public static boolean isConversionNeeded() {
        return conversionNeeded;
    }

    public static void convert(String previousVersion, File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection settingsSection;
        if (config.contains("settings")) {
            settingsSection = config.getConfigurationSection("settings");
        } else {
            settingsSection = config.createSection("settings");
        }
        assert settingsSection != null;

        switch (previousVersion) {
            case "1.0" -> {
                if (config.getString("block") != null) {
                    config.set("type", DisplayType.BLOCK.name());
                    settingsSection.set("block", config.getString("block"));

                    ConfigurationSection dataSection = settingsSection.createSection("blockData");
                    BlockData block = Objects.requireNonNull(Material.getMaterial(Objects.requireNonNull(config.getString("block")))).createBlockData();
                    if (block.getAsString().indexOf("[") > 0) {
                        String fullData = block.getAsString().substring(block.getAsString().indexOf("[") + 1, block.getAsString().lastIndexOf("]"));
                        for (String data : fullData.split(",")) {
                            String[] dataPart = data.split("=");
                            dataSection.set(dataPart[0], dataPart[1]);
                        }
                    }
                    settingsSection.setComments("blockData", List.of("For more information about what these values are, visit https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/data/BlockData.html"));

                    config.set("block", null);

                } else if (config.getString("item") != null) {
                    config.set("type", DisplayType.ITEM.name());
                    settingsSection.set("item", config.getString("item"));
                    settingsSection.set("enchanted", false);
                    settingsSection.set("itemTransformation", config.getString("itemTransformation"));
                    config.set("item", null);
                    config.set("itemTransformation", null);

                } else if (config.getString("text") != null) {
                    config.set("type", DisplayType.TEXT.name());
                    settingsSection.set("animationTime", 20);
                    settingsSection.set("refreshTime", 20);
                    settingsSection.set("text", List.of(Objects.requireNonNull(config.getString("text"))));
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

                ConfigurationSection glowSection = Objects.requireNonNull(config.createSection("glow"));
                glowSection.set("glowing", false);
                glowSection.set("color", "255;170;0");

                ConfigurationSection hitboxSection = config.createSection("hitbox");
                hitboxSection.set("override", false);
                hitboxSection.set("width", 1.0f);
                hitboxSection.set("height", 1.0f);
                config.setComments("hitbox", Arrays.asList("Displays don't have hitboxes of their own, so to have click actions independent entities have to be created.", "These settings allow you to control the hitbox of the display.", "(Use F3 + B to see the hitboxes)"));

                Display display = (Display) Bukkit.getEntity(UUID.fromString(Objects.requireNonNull(config.getString("id"))));
                Objects.requireNonNull(display).remove();
                config.set("id", null);
            }

            case "1.1" -> {
                DisplayType type = DisplayType.valueOf(config.getString("type"));
                if (type == DisplayType.BLOCK) {
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

                } else if (type == DisplayType.TEXT) {
                    settingsSection.set("animationTime", 20);
                    settingsSection.set("refreshTime", 20);
                    settingsSection.set("text", List.of(Objects.requireNonNull(settingsSection.get("text"))));

                } else if (type == DisplayType.ITEM) {
                    settingsSection.set("enchanted", false);
                }

                ConfigurationSection glowSection = Objects.requireNonNull(config.createSection("glow"));
                glowSection.set("glowing", false);
                glowSection.set("color", "255;170;0");

                ConfigurationSection hitboxSection = config.createSection("hitbox");
                hitboxSection.set("override", false);
                hitboxSection.set("width", 1.0f);
                hitboxSection.set("height", 1.0f);
                config.setComments("hitbox", Arrays.asList("Displays don't have hitboxes of their own, so to have click actions independent entities have to be created.", "These settings allow you to control the hitbox of the display.", "(Use F3 + B to see the hitboxes)"));
            }

            case "1.2" -> {
                DisplayType type = DisplayType.valueOf(config.getString("type"));
                if (type == DisplayType.BLOCK) {
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

                } else if (type == DisplayType.TEXT) {
                    settingsSection.set("animationTime", 20);
                    settingsSection.set("refreshTime", 20);

                } else if (type == DisplayType.ITEM) {
                    settingsSection.set("enchanted", false);
                }

                ConfigurationSection glowSection = Objects.requireNonNull(config.createSection("glow"));
                glowSection.set("glowing", false);
                glowSection.set("color", "255;170;0");

                ConfigurationSection hitboxSection = config.createSection("hitbox");
                hitboxSection.set("override", false);
                hitboxSection.set("width", 1.0f);
                hitboxSection.set("height", 1.0f);
                config.setComments("hitbox", Arrays.asList("Displays don't have hitboxes of their own, so to have click actions independent entities have to be created.", "These settings allow you to control the hitbox of the display.", "(Use F3 + B to see the hitboxes)"));

                AdvancedDisplays.mainConfig.getConfig().set("text-update", null);
                AdvancedDisplays.mainConfig.save();
            }

            case "1.2.1", "1.2.2", "1.2.3" -> {
                ConfigurationSection glowSection = Objects.requireNonNull(config.createSection("glow"));
                glowSection.set("glowing", false);
                glowSection.set("color", "255;170;0");

                ConfigurationSection hitboxSection = config.createSection("hitbox");
                hitboxSection.set("override", false);
                hitboxSection.set("width", 1.0f);
                hitboxSection.set("height", 1.0f);
                config.setComments("hitbox", Arrays.asList("Displays don't have hitboxes of their own, so to have click actions independent entities have to be created.", "These settings allow you to control the hitbox of the display.", "(Use F3 + B to see the hitboxes)"));

                DisplayType type = DisplayType.valueOf(config.getString("type"));
                if (type == DisplayType.BLOCK) {
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

                } else if (type == DisplayType.ITEM) {
                    settingsSection.set("enchanted", false);
                }
            }

            case "1.3" -> {
                ConfigurationSection hitboxSection = config.createSection("hitbox");
                hitboxSection.set("override", false);
                hitboxSection.set("width", 1.0f);
                hitboxSection.set("height", 1.0f);
                config.setComments("hitbox", Arrays.asList("Displays don't have hitboxes of their own, so to have click actions independent entities have to be created.", "These settings allow you to control the hitbox of the display.", "(Use F3 + B to see the hitboxes)"));
            }
        }

        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}