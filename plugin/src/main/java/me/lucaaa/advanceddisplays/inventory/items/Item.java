package me.lucaaa.advanceddisplays.inventory.items;

import me.lucaaa.advanceddisplays.common.utils.Utils;
import me.lucaaa.advanceddisplays.data.NamedEnum;
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
import java.util.List;
import java.util.Objects;

public class Item {
    private final ItemStack item;
    private Object value;
    private final double bigChange;
    private final double smallChange;

    private final ColorItems.ColorComponent colorComponent;

    public Item(Material material, String title, String lore, Object value) {
        this(material, title, List.of(lore), value, false, false, 0.0, 0.0, false);
    }

    public Item(Material material, String title, List<String> lore, Object value, boolean setTitle) {
        this(material, title, lore, value, false, false, 0.0, 0.0, setTitle);
    }

    public Item(Material material, String title, String rawLore, Object value, boolean bigClicks, boolean smallClicks, double bigChange, double smallChange, boolean changeTitle) {
        this(material, title, List.of(rawLore), value, bigClicks, smallClicks, bigChange, smallChange, changeTitle);
    }

    public Item(Material material, String title, List<String> rawLore, Object value, boolean bigClicks, boolean smallClicks, double bigChange, double smallChange) {
        this(material, title, rawLore, value, bigClicks, smallClicks, bigChange, smallChange, false);
    }

    public Item(Material material, String title, List<String> rawLore, Object value, boolean bigClicks, boolean smallClicks, double bigChange, double smallChange, boolean setTitle) {
        ArrayList<String> lore = new ArrayList<>(rawLore.stream().map(line -> "&e" + line).toList());

        if (!material.isItem()) {
            material = Material.BARRIER;
            lore.add("&cDisplayed material does not have an item!");
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

        Object displayValue = value;
        if (value instanceof NamedEnum named) {
            displayValue = named.getName();
        }

        if (setTitle) {
            meta.setDisplayName(Utils.getColoredText("&6" + title + ": &7" + displayValue));
        } else {
            meta.setDisplayName(Utils.getColoredText("&6" + title));
        }

        if (bigClicks) {
            lore.add("");
            lore.add("&7Use &cLEFT_CLICK &7to add " + bigChange);
            lore.add("&7Use &cRIGHT_CLICK &7to subtract " + bigChange);
        }

        if (smallClicks) {
            if (!bigClicks) lore.add("");
            lore.add("&7Use &cSHIFT + LEFT_CLICK &7to add " + smallChange);
            lore.add("&7Use &cSHIFT + RIGHT_CLICK &7to subtract " + smallChange);
        }

        if (value != null) {
            lore.add("");
            lore.add("&9Current value: &7" + displayValue);
        }

        meta.setLore(lore.stream().map(Utils::getColoredText).toList());
        item.setItemMeta(meta);

        this.item = item;
        if (value instanceof Float f) {
            this.value = (double) f;
        } else {
            this.value = value;
        }
        this.bigChange = bigChange;
        this.smallChange = smallChange;

        colorComponent = null;
    }

    public Item(String title, String rawLore, ColorItems.ColorComponent component) {
        Material material = switch (component) {
            case RED -> Material.RED_CONCRETE;
            case GREEN -> Material.LIME_CONCRETE;
            case BLUE -> Material.BLUE_CONCRETE;
            case ALPHA -> Material.WHITE_CONCRETE;
        };

        ItemStack item = new ItemStack(material);

        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

        meta.setDisplayName(Utils.getColoredText("&6" + title));

        List<String> lore = new ArrayList<>();
        lore.add("&e" + rawLore);
        lore.add("");
        lore.add("&7Use &cLEFT_CLICK &7to add");
        lore.add("&7Use &cRIGHT_CLICK &7to subtract");

        meta.setLore(lore.stream().map(Utils::getColoredText).toList());
        item.setItemMeta(meta);

        this.item = item;
        this.value = null;
        this.bigChange = 0.0;
        this.smallChange = 0.0;

        this.colorComponent = component;
    }

    public Item(String title, int value, ColorItems.ColorComponent component) {
        item = new ItemStack(Material.LEATHER_CHESTPLATE);

        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

        meta.setDisplayName(Utils.getColoredText(title));

        ArrayList<String> lore = new ArrayList<>();
        Color valueColor = switch (component) {
            case RED -> Color.fromRGB(value, 0, 0);
            case GREEN -> Color.fromRGB(0, value, 0);
            case BLUE -> Color.fromRGB(0, 0, value);
            case ALPHA -> Color.fromRGB(value, value, value);
        };
        lore.add(Utils.getColoredText("&9Current value: " + ChatColor.of(new java.awt.Color(valueColor.asRGB())) + component + " (" + value + ")"));
        meta.setLore(lore);
        this.item.setItemMeta(meta);

        this.value = value;
        this.bigChange = 0.0;
        this.smallChange = 0.0;

        this.colorComponent = component;

        setArmorColor(valueColor);
    }

    public Item(ItemStack item) {
        this.item = new ItemStack(item);
        this.value = null;
        this.bigChange = 0.0;
        this.smallChange = 0.0;

        this.colorComponent = null;
    }

    public Item setArmorColor(Color color) {
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        if (!(meta instanceof LeatherArmorMeta armorMeta)) return this;

        if (colorComponent != null) {
            int component = 0;
            switch (colorComponent) {
                case RED -> {
                    component = color.getRed();
                    color = Color.fromRGB(component, 0, 0);
                }
                case GREEN -> {
                    component = color.getGreen();
                    color = Color.fromRGB(0, component, 0);
                }
                case BLUE -> {
                    component = color.getBlue();
                    color = Color.fromRGB(0, 0, component);
                }
                case ALPHA -> {
                    component = color.getAlpha();
                    color = Color.fromRGB(component, component, component);
                }
            }

            changeCurrentValue(ChatColor.of(new java.awt.Color(color.asRGB())) + colorComponent.name() + " (" + component + ")", false, armorMeta);
        }

        armorMeta.setColor(color);
        item.setItemMeta(armorMeta);
        return this;
    }

    public Item setPreviewLore(Color color, boolean alphaEnabled, String title) {
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

        String displayName = (title == null) ? "Preview Color" : title;
        meta.setDisplayName(ChatColor.of(new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())) + displayName);

        ArrayList<String> lore = new ArrayList<>();
        lore.add(Utils.getColoredText("&9Current values:"));
        lore.add(ChatColor.of("#FF0000") + "Red: " + ChatColor.of(new java.awt.Color(color.getRed(), 0, 0)) + color.getRed());
        lore.add(ChatColor.of("#00FF00") + "Green: " + ChatColor.of(new java.awt.Color(0, color.getGreen(), 0)) + color.getGreen());
        lore.add(ChatColor.of("#0000FF") + "Blue: " + ChatColor.of(new java.awt.Color(0, 0, color.getBlue())) + color.getBlue());
        int gray = (int) (color.getAlpha() * 255.0 / 255);
        if (alphaEnabled) lore.add(ChatColor.WHITE + "Alpha: " + ChatColor.of(new java.awt.Color(gray, gray, gray)) + color.getAlpha());

        meta.setLore(lore);
        item.setItemMeta(meta);
        return this;
    }

    public Item setItemBrightness(int brightness) {
        BlockDataMeta meta = (BlockDataMeta) Objects.requireNonNull(item.getItemMeta());
        Levelled data = (Levelled) Bukkit.createBlockData(Material.LIGHT);
        data.setLevel(brightness);
        meta.setBlockData(data);
        item.setItemMeta(meta);
        return this;
    }

    public int setNewItemBrightness(boolean increase) {
        if (!(value instanceof Integer)) return 0;

        int change = increase ? 1 : -1;
        int newBrightness;
        if ((int) value == 15 && change == 1) {
            newBrightness = 0;
        } else if ((int) value == 0 && change == -1) {
            newBrightness = 15;
        } else {
            newBrightness = (int) value + change;
        }
        setItemBrightness(newBrightness);
        changeCurrentValue(newBrightness, false);
        return newBrightness;
    }

    public void changeCurrentValue(Object value, boolean changeTitle) {
        changeCurrentValue(value, changeTitle, null);
    }

    public void changeCurrentValue(Object value, boolean changeTitle, ItemMeta itemMeta) {
        ItemMeta meta = (itemMeta == null) ? Objects.requireNonNull(item.getItemMeta()) : itemMeta;

        Object display = value;
        if (value instanceof NamedEnum named) {
            display = named.getName();
        }

        if (changeTitle) {
            meta.setDisplayName(Utils.getColoredText(meta.getDisplayName().split(":")[0] + ": &7" + display));
        }

        List<String> lore = new ArrayList<>(Objects.requireNonNull(meta.getLore()));
        lore.remove(lore.size() - 1);
        lore.add(Utils.getColoredText("&9Current value: &7" + display));

        meta.setLore(lore);
        if (itemMeta == null) item.setItemMeta(meta);
        if (value instanceof Float f) {
            this.value = (double) f;
        } else {
            this.value = value;
        }
    }

    public double changeDoubleValue(boolean changeSmall, double min, Double max, boolean increase, boolean changeTitle) {
        if (!(value instanceof Number)) return 0.0;

        double change;
        if (changeSmall) {
            change = (increase) ? smallChange : -smallChange;
        } else {
            change = (increase) ? bigChange : -bigChange;
        }

        // If the number is more than the max, return the max.
        double currentValue = (value instanceof Integer integer) ? integer : (double) value;
        double selectMax = (max == null) ? currentValue + change : Math.min(max, currentValue + change);
        double toReturn = Math.max(min, selectMax);

        // Round it to 2 decimal places
        double rounded = new BigDecimal(toReturn).setScale(2, RoundingMode.HALF_UP).doubleValue();
        changeCurrentValue(rounded, changeTitle);
        return rounded;
    }

    public boolean changeBooleanValue() {
        if (!(value instanceof Boolean)) return true;

        boolean newValue = !(boolean) value;

        changeCurrentValue(newValue, false);
        return newValue;
    }

    /**
     * Gets the next enum value from enum class of the given enum.
     * @param changeTitle Whether the title should be changed.
     * @return The next enum in the enum class or the first if the provided one was the last.
     * @param <T> The enum class.
     */
    public <T extends Enum<T>> T changeEnumValue(boolean changeTitle) {
        if (!(value instanceof Enum<?>)) {
            throw new IllegalArgumentException("Unexpected error - value is not an enum. Report to developers.");
        }

        @SuppressWarnings("unchecked")
        T enumValue = (T) value;

        T[] values = enumValue.getDeclaringClass().getEnumConstants();
        int currentIndex = enumValue.ordinal();
        int newValueIndex = (currentIndex + 1 == values.length) ? 0 : currentIndex + 1;
        T newValue = values[newValueIndex];

        changeCurrentValue(newValue, changeTitle);
        return newValue;
    }

    public Object getValue() {
        return value;
    }

    public ItemStack getItemStack() {
        return item;
    }
}