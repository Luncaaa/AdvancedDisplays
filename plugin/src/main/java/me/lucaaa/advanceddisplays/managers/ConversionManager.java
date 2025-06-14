package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class ConversionManager {
    private static boolean conversionNeeded;

    public static void setConversionNeeded(AdvancedDisplays plugin, boolean needsConversion) {
        conversionNeeded = needsConversion;

        if (!conversionNeeded) return;

        plugin.log(Level.WARNING, "The displays or the main configuration files are from an older version and have been changed in newer versions.");
        plugin.log(Level.WARNING, "Run the command \"/ad convert\" to update the configuration files to newer versions, but make a backup first.");
        plugin.log(Level.WARNING, "Not converting the configurations will cause the plugin to malfunction. See more information at lucaaa.gitbook.io/advanceddisplays/usage/commands-and-permissions/convert-subcommand");
    }

    public static boolean isConversionNeeded() {
        return conversionNeeded;
    }

    public static void convert(AdvancedDisplays plugin, File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection entitySection;
        if (!config.isConfigurationSection("entity")) {
            entitySection = config.createSection("entity");
        } else {
            entitySection = config.getConfigurationSection("entity");
        }
        assert entitySection != null;

        ConfigurationSection displaySection;
        if (config.isConfigurationSection("display")) {
            displaySection = config.getConfigurationSection("display");
        } else {
            displaySection = config.createSection("display");
        }
        assert  displaySection != null;

        ConfigurationSection settingsSection;
        if (displaySection.isConfigurationSection("settings")) {
            settingsSection = displaySection.getConfigurationSection("settings");
        } else if (config.isConfigurationSection("settings")) {
            displaySection.set("settings", config.getConfigurationSection("settings"));
            settingsSection = displaySection.getConfigurationSection("settings");
            config.set("settings", null);
        } else {
            settingsSection = displaySection.createSection("settings");
        }
        assert settingsSection != null;

        if (!entitySection.isBoolean("onFire")) entitySection.set("onFire", false);
        if (!entitySection.isBoolean("sprinting")) entitySection.set("sprinting", false);
        if (!entitySection.isString("custom-name")) entitySection.set("custom-name", "Custom name");
        if (!entitySection.isBoolean("custom-name-visible")) entitySection.set("custom-name-visible", false);

        if (config.isConfigurationSection("glow")) {
            ConfigurationSection oldGlowSection = Objects.requireNonNull(config.getConfigurationSection("glow"));
            ConfigurationSection glowSection = entitySection.createSection("glow");
            glowSection.set("glowing", oldGlowSection.getBoolean("glowing"));
            glowSection.set("color", "GOLD");
            displaySection.set("glowColorOverride", oldGlowSection.getString("color"));
            config.set("glow", null);

        } else if (!entitySection.isConfigurationSection("hitbox")) {
            ConfigurationSection glowSection = entitySection.createSection("glow");
            glowSection.set("glowing", false);
            glowSection.set("color", "GOLD");
            displaySection.set("glowColorOverride", "255;170;0");
        }

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
            entitySection.set("type", EntityType.BLOCK_DISPLAY.name());
            if (!settingsSection.isConfigurationSection("blockData")) {
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
            entitySection.set("type", EntityType.TEXT_DISPLAY.name());
            if (!settingsSection.isInt("animationTime")) settingsSection.set("animationTime", 20);
            if (!settingsSection.isInt("refreshTime")) settingsSection.set("refreshTime", 20);

            if (!settingsSection.isConfigurationSection("texts")) {
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
            }

        } else if (type == DisplayType.ITEM) {
            entitySection.set("type", EntityType.ITEM_DISPLAY.name());
            if (!settingsSection.isBoolean("enchanted")) settingsSection.set("enchanted", false);
            if (!settingsSection.isInt("customModelData")) settingsSection.set("customModelData", 0);
        }

        if (config.isConfigurationSection("hitbox")) {
            displaySection.set("hitbox", config.getConfigurationSection("hitbox"));
            config.set("hitbox", null);

        } else if (!displaySection.isConfigurationSection("hitbox")) {
            ConfigurationSection hitboxSection = displaySection.createSection("hitbox");
            hitboxSection.set("override", false);
            hitboxSection.set("width", 1.0f);
            hitboxSection.set("height", 1.0f);
            displaySection.setComments("hitbox", Arrays.asList("Displays don't have hitboxes of their own, so to have click actions independent entities have to be created.", "These settings allow you to control the hitbox of the display.", "(Use F3 + B to see the hitboxes)"));
        }

        if (config.isConfigurationSection("transformation")) {
            displaySection.set("transformation", config.getConfigurationSection("transformation"));
            config.set("transformation", null);
        }

        if (config.isConfigurationSection("shadow")) {
            displaySection.set("shadow", config.getConfigurationSection("shadow"));
            config.set("shadow", null);
        }

        if (config.isConfigurationSection("brightness")) {
            displaySection.set("brightness", config.getConfigurationSection("brightness"));
            config.set("brightness", null);
        }

        if (config.isString("rotationType")) {
            displaySection.set("billboard", config.getString("rotationType"));
            config.set("rotationType", null);
        }

        String hasPermission = config.getString("permission", "none");
        String lacksPermission = config.getString("hide-permission", "none");
        double distance = config.getDouble("view-distance");

        if (!config.isConfigurationSection("view-conditions")) {
            ConfigurationSection viewConditionsSection = config.createSection("view-conditions");
            viewConditionsSection.set("distance", distance);
            viewConditionsSection.set("has-permission", hasPermission);
            viewConditionsSection.set("lacks-permission", lacksPermission);
        }

        config.set("permission", null);
        config.set("hide-permission", null);
        config.set("view-distance", null);

        if (config.contains("id")) {
            Display display = (Display) Bukkit.getEntity(UUID.fromString(Objects.requireNonNull(config.getString("id"))));
            Objects.requireNonNull(display).remove();
            config.set("id", null);
        }

        ConfigManager mainConfig = plugin.getMainConfig();
        YamlConfiguration yaml = mainConfig.getConfig();
        yaml.set("text-update", null);

        if (!yaml.isBoolean("updateChecker")) {
            yaml.set("updateChecker", true);
        }

        if (!yaml.isList("disabledItems")) {
            yaml.set("disabledItems", List.of());
            yaml.setComments("disabledItems", List.of(
                    " List of disabled settings in the editor menu. Visit the link below for a list of settings that can be disabled.",
                    "https://javadoc.jitpack.io/com/github/Luncaaa/AdvancedDisplays/main-SNAPSHOT/javadoc/me/lucaaa/advanceddisplays/api/displays/enums/EditorItem.html"
            ));
        }
        mainConfig.save();

        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}