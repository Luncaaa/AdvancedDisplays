package me.lucaaa.advanceddisplays.inventory.items;

import me.lucaaa.advanceddisplays.common.utils.Utils;
import me.lucaaa.advanceddisplays.data.NamedEnum;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class Item<T> {
    protected final ItemStack item;
    protected final String title;
    protected final ArrayList<String> lore;
    protected final boolean changeTitle;
    protected T value;

    protected Item(Material material, String title, List<String> lore, boolean changeTitle, T initialValue) {
        this.item = new ItemStack(material);
        this.title = title;
        this.lore = new ArrayList<>(lore);
        this.changeTitle = changeTitle;
        this.value = initialValue;

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE);
        }
        item.setItemMeta(meta);
    }

    protected Item(ItemStack item, String title, List<String> lore) {
        this.item = item;
        this.title = title;
        this.lore = new ArrayList<>(lore);
        this.changeTitle = false;
        this.value = null;
        setMeta(title, lore);
    }

    protected void setMeta(String title, List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        meta.setDisplayName(Utils.getColoredText("&6" + title));
        meta.setLore(lore.stream().map(line -> Utils.getColoredText("&e" + line)).toList());

        item.setItemMeta(meta);
    }

    public void setValue(T value) {
        this.value = value;
        String title = this.title;
        if (changeTitle) {
            title = title + ": &e" + value;
        }

        if (value != null) {
            // Removes the previous old value.
            for (int i = lore.size() - 1; i >= 0; i--) {
                if (lore.get(i).startsWith("&9Current value:")) {
                    lore.remove(i);
                }
            }

            String parsedValue = value.toString();
            if (value instanceof NamedEnum namedEnum) {
                parsedValue = namedEnum.getName();
            } else if (value instanceof Double) {
                parsedValue = BigDecimal.valueOf((double) value).setScale(2, RoundingMode.HALF_UP).toString();

                // Remove the last zeroes from the decimal positions
                if (parsedValue.contains(".")) {
                    String beforeDecimal = parsedValue.split("\\.")[0];
                    String afterDecimal = parsedValue.split("\\.")[1];

                    // Remove trailing zeros
                    afterDecimal = afterDecimal.replaceAll("0+$", "");

                    parsedValue = beforeDecimal + "." + afterDecimal;

                    // If there are no decimals, remove the dot
                    if (parsedValue.endsWith(".")) {
                        parsedValue = parsedValue.replace(".", "");
                    }
                }
            }
            lore.add("&9Current value: &7" + parsedValue);
        }

        setMeta(title, lore);
    }

    public Object getValue() {
        return value;
    }

    public ItemStack getStack() {
        return item;
    }

    public static class StepItem extends Item<Double> {
        private final double bigChange;
        private final double smallChange;
        private final boolean smallEnabled;

        public StepItem(Material material, String title, List<String> lore, double initialValue, double bigChange) {
            this(material, title, lore, initialValue, bigChange, 0.0, false, false);
        }

        public StepItem(Material material, String title, List<String> lore, double initialValue, double bigChange, double smallChange) {
            this(material, title, lore, initialValue, bigChange, smallChange, true, false);
        }

        public StepItem(Material material, String title, String lore, double initialValue, double bigChange, double smallChange, boolean changeTitle) {
            this(material, title, List.of(lore), initialValue, bigChange, smallChange, true, changeTitle);
        }

        public StepItem(Material material, String title, List<String> lore, double initialValue, double bigChange, double smallChange, boolean changeTitle) {
            this(material, title, lore, initialValue, bigChange, smallChange, true, changeTitle);
        }

        private StepItem(Material material, String title, List<String> baseLore, double initialValue, double bigChange, double smallChange, boolean smallEnabled, boolean changeTitle) {
            super(material, title, baseLore, changeTitle, initialValue);
            this.bigChange = bigChange;
            this.smallChange = smallChange;
            this.smallEnabled = smallEnabled;

            lore.add("");
            lore.add("&7Use &cLEFT_CLICK &7to add " + bigChange);
            lore.add("&7Use &cRIGHT_CLICK &7to subtract " + bigChange);

            if (smallEnabled) {
                lore.add("&7Use &cSHIFT + LEFT_CLICK &7to add " + smallChange);
                lore.add("&7Use &cSHIFT + RIGHT_CLICK &7to subtract " + smallChange);
            }

            lore.add("");
            setValue(value);
        }

        public double changeValue(boolean increase, boolean small, double min) {
            return changeValue(increase, small, min, null);
        }

        public double changeValue(boolean increase, boolean small, double min, Double max) {
            double change;
            if (small && smallEnabled) {
                change = (increase) ? smallChange : -smallChange;
            } else {
                change = (increase) ? bigChange : -bigChange;
            }

            double filterMax = (max == null) ? (value + change) : Math.min(max, value + change);
            double filterMin = Math.max(min, filterMax);
            setValue(BigDecimal.valueOf(filterMin).setScale(2, RoundingMode.HALF_UP).doubleValue());
            return value;
        }

        public int setNewItemBrightness(boolean increase) {
            int change = increase ? 1 : -1;
            int newBrightness;
            if (value == 15 && change == 1) {
                newBrightness = 0;
            } else if (value == 0 && change == -1) {
                newBrightness = 15;
            } else {
                newBrightness = (int) (value + change);
            }
            EditorItems.setBrightness(item, newBrightness);
            setValue((double) newBrightness);
            return newBrightness;
        }
    }

    public static class BooleanItem extends Item<Boolean> {
        public BooleanItem(Material material, String title, String lore, boolean initialValue) {
            this(material, title, List.of(lore), initialValue, false);
        }

        public BooleanItem(Material material, String title, List<String> lore, boolean initialValue) {
            this(material, title, lore, initialValue, false);
        }

        public BooleanItem(Material material, String title, List<String> baseLore, boolean initialValue, boolean changeTitle) {
            super(material, title, baseLore, changeTitle, initialValue);

            lore.add("");
            lore.add("&7Click to change");
            lore.add("");
            setValue(value);
        }

        public boolean changeValue() {
            setValue(!value);
            return value;
        }
    }

    public static class EnumItem extends Item<Enum<?>> {
        public EnumItem(Material material, String title, String lore, Enum<?> initialValue) {
            this(material, title, List.of(lore), initialValue, false);
        }

        public EnumItem(Material material, String title, List<String> baseLore, Enum<?> initialValue, boolean changeTitle) {
            super(material, title, baseLore, changeTitle, initialValue);

            lore.add("");
            lore.add("&7Click to change");
            lore.add("");
            setValue(initialValue);
        }

        /**
         * Gets the next enum value from enum class of the given enum.
         * @return The next enum in the enum class or the first if the provided one was the last.
         * @param <T> The enum class.
         */
        public <T extends Enum<T>> T changeValue() {
            @SuppressWarnings("unchecked")
            T enumValue = (T) value;

            T[] values = enumValue.getDeclaringClass().getEnumConstants();
            int currentIndex = enumValue.ordinal();
            int newValueIndex = (currentIndex + 1 == values.length) ? 0 : currentIndex + 1;
            T newValue = values[newValueIndex];

            setValue(newValue);
            return newValue;
        }
    }

    public static class ClickableItem extends Item<String> {
        public ClickableItem(Material material, String title, String lore) {
            this(material, title, List.of(lore), null);
        }

        public ClickableItem(Material material, String title, String lore, String value) {
            this(material, title, List.of(lore), value);
        }

        public ClickableItem(Material material, String title, List<String> baseLore, String value) {
            super(material, title, baseLore, false, value);
            lore.add("");
            setValue(value);
        }

        public ClickableItem(ItemStack item, String title, List<String> lore) {
            super(item, title, lore);
        }
    }
}