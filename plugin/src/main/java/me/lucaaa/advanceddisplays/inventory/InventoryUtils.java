package me.lucaaa.advanceddisplays.inventory;

import me.lucaaa.advanceddisplays.common.utils.Utils;
import me.lucaaa.advanceddisplays.data.NamedEnum;
import me.lucaaa.advanceddisplays.inventory.items.ColorItems;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.data.Levelled;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Objects;

public class InventoryUtils {
    public static ItemStack setItemBrightness(ItemStack item, int brightness) {
        BlockDataMeta meta = (BlockDataMeta) Objects.requireNonNull(item.getItemMeta());
        Levelled data = (Levelled) Bukkit.createBlockData(Material.LIGHT);
        data.setLevel(brightness);
        meta.setBlockData(data);
        item.setItemMeta(meta);
        return item;
    }

    public static int setNewItemBrightness(ItemStack item, int oldBrightness, boolean increase) {
        int change = increase ? 1 : -1;
        int newBrightness;
        if (oldBrightness == 15 && change == 1) {
            newBrightness = 0;
        } else if (oldBrightness == 0 && change == -1) {
            newBrightness = 15;
        } else {
            newBrightness = oldBrightness + change;
        }
        setItemBrightness(item, newBrightness);
        changeCurrentValue(item, newBrightness);
        return newBrightness;
    }

    public static double changeDoubleValue(ItemStack item, double oldValue, boolean changeDecimals, double min, boolean increase) {
        return changeDoubleValue(item, oldValue, 1.0, 0.1, changeDecimals, min, null, increase, false);
    }

    public static double changeDoubleValue(ItemStack item, double oldValue, boolean changeDecimals, double min, boolean increase, boolean changeTitle) {
        return changeDoubleValue(item, oldValue, 1.0, 0.1, changeDecimals, min, null, increase, changeTitle);
    }

    public static double changeDoubleValue(ItemStack item, double oldValue, boolean changeDecimals, double min, double max, boolean increase, boolean changeTitle) {
        return changeDoubleValue(item, oldValue, 1.0, 0.1, changeDecimals, min, max, increase, changeTitle);
    }

    public static double changeDoubleValue(ItemStack item, double oldValue, double bigChange, double smallChange, boolean changeSmall, double min, Double max, boolean increase) {
        return changeDoubleValue(item, oldValue, bigChange, smallChange, changeSmall, min, max, increase, false);
    }

    public static double changeDoubleValue(ItemStack item, double oldValue, double bigChange, double smallChange, boolean changeSmall, double min, Double max, boolean increase, boolean changeTitle) {
        double change;
        if (changeSmall) {
            change = (increase) ? smallChange : -smallChange;
        } else {
            change = (increase) ? bigChange : -bigChange;
        }

        // If the number is more than the max, return the max.
        double selectMax = (max == null) ? oldValue + change : Math.min(max, oldValue + change);
        double toReturn = Math.max(min, selectMax);

        // Round it to 2 decimal places
        double rounded = new BigDecimal(toReturn).setScale(2, RoundingMode.HALF_UP).doubleValue();
        changeCurrentValue(item, rounded, changeTitle);
        return rounded;
    }

    public static boolean changeBooleanValue(ItemStack item, boolean oldValue) {
        boolean newValue = !oldValue;

        changeCurrentValue(item, newValue);
        return newValue;
    }

    public static <T extends Enum<T>> T changeEnumValue(ItemStack item, T initialValue) {
        return changeEnumValue(item, initialValue, false);
    }

    /**
     * Gets the next enum value from enum class of the given enum.
     * @param item The inventory item whose value we want to change.
     * @param initialValue The enum we want to get the next enum from.
     * @param changeTitle Whether the title should be changed.
     * @return The next enum in the enum class or the first if the provided one was the last.
     * @param <T> The enum class.
     */
    public static <T extends Enum<T>> T changeEnumValue(ItemStack item, T initialValue, boolean changeTitle) {
        T[] values = initialValue.getDeclaringClass().getEnumConstants();
        int currentIndex = initialValue.ordinal();
        int newValueIndex = (currentIndex + 1 == values.length) ? 0 : currentIndex + 1;
        T newValue = values[newValueIndex];

        String title = newValue.name();
        if (newValue instanceof NamedEnum named) {
            title = named.getName();
        }

        changeCurrentValue(item, title, changeTitle);
        return newValue;
    }

    public static void changeCurrentValue(ItemStack item, Object value) {
        changeCurrentValue(item, value, false);
    }

    public static void changeCurrentValue(ItemStack item, Object value, boolean changeTitle) {
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

        if (changeTitle) {
            meta.setDisplayName(Utils.getColoredText(meta.getDisplayName().split(":")[0] + ": &7" + value));
        }

        ArrayList<String> lore = new ArrayList<>(Objects.requireNonNull(meta.getLore()));
        lore.remove(lore.size() - 1);
        lore.add(Utils.getColoredText("&9Current value: &7" + value));

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public static ItemStack changeArmorColor(ItemStack armor, Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta) Objects.requireNonNull(armor.getItemMeta());
        meta.setColor(color);
        armor.setItemMeta(meta);
        return armor;
    }

    public static int getUpdatedColor(ItemStack armor, int colorValue, int amount, boolean add, ColorItems.ColorComponent component) {
        int value = (add) ? colorValue + amount : colorValue - amount;
        if (value > 255) {
            value = 255;
        } else if (value < 0) {
            value = 0;
        }

        Color color = switch (component) {
            case RED -> Color.fromRGB(value, 0, 0);
            case GREEN -> Color.fromRGB(0, value, 0);
            case BLUE -> Color.fromRGB(0, 0, value);
            case ALPHA -> Color.fromRGB(value, value, value);
        };

        changeArmorColor(armor, color);
        changeCurrentValue(armor, ChatColor.of(new java.awt.Color(color.asRGB())) + component.name() + " (" + value + ")");
        return value;
    }
}