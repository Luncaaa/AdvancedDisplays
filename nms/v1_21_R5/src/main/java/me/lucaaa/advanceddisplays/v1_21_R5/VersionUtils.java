package me.lucaaa.advanceddisplays.v1_21_R5;

import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class VersionUtils {
    @SuppressWarnings("UnstableApiUsage")
    public static void saveMetaCustomModelData(ItemMeta meta, ConfigurationSection settings) {
        CustomModelDataComponent component = meta.getCustomModelDataComponent();

        List<String> parsedColors = new ArrayList<>();
        for (Color color : component.getColors()) {
            parsedColors.add(color.getRed() + ";" + color.getGreen() + ";" + color.getBlue());
        }

        ConfigurationSection cmdSection = settings.createSection("customModelData");
        cmdSection.set("colors", parsedColors);
        cmdSection.set("strings", component.getStrings());
        cmdSection.set("flags", component.getFlags());
        cmdSection.set("floats", component.getFloats());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void loadMetaCustomModelData(ItemMeta meta, ConfigurationSection settings) {
        CustomModelDataComponent component = meta.getCustomModelDataComponent();

        ConfigurationSection cmdSection = settings.getConfigurationSection("customModelData");
        if (cmdSection == null) return;

        List<Color> colors = new ArrayList<>();
        List<String> colorsList = cmdSection.getStringList("colors");
        for (String color : colorsList) {
            String[] colorParts = color.split("\\.");
            colors.add(Color.fromRGB(Integer.parseInt(colorParts[0]), Integer.parseInt(colorParts[1]), Integer.parseInt(colorParts[2])));
        }

        component.setColors(colors);
        component.setStrings(cmdSection.getStringList("strings"));
        component.setFlags(cmdSection.getBooleanList("flags"));
        component.setFloats(cmdSection.getFloatList("floats"));
        meta.setCustomModelDataComponent(component);
    }
}