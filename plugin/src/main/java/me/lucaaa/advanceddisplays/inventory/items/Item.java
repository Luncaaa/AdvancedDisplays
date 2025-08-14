package me.lucaaa.advanceddisplays.inventory.items;

import me.lucaaa.advanceddisplays.data.Utils;
import me.lucaaa.advanceddisplays.data.NamedEnum;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Item<T> {
    protected ItemStack item;
    protected final String title;
    protected final ArrayList<String> lore;
    protected final boolean changeTitle;
    protected T value;

    protected Item(Material material, String title, List<String> lore, T initialValue, boolean changeTitle) {
        this(new ItemStack(material), title, lore, initialValue, changeTitle);
    }

    protected Item(ItemStack item, String title, List<String> lore, T value, boolean changeTitle) {
        this.item = item;
        this.title = title;
        this.lore = new ArrayList<>(lore);
        this.changeTitle = changeTitle;
        this.value = value;

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Utils.hideFlags(meta);
        }

        item.setItemMeta(meta);
        setMeta(title, lore);
    }

    protected void setMeta(String title, List<String> lore) {
        ItemStack clone = item.clone();
        ItemMeta meta = clone.getItemMeta();
        if (meta == null) return;

        meta.setDisplayName(Utils.getColoredText("&6" + title));
        meta.setLore(lore.stream().map(line -> Utils.getColoredText("&e" + line)).toList());

        clone.setItemMeta(meta);
        this.item = clone;
    }

    public void applyMeta(ItemMeta meta) {
        ItemStack clone = item.clone();
        ItemMeta cloneMeta = clone.getItemMeta();
        if (cloneMeta == null) return;
        meta.setDisplayName(cloneMeta.getDisplayName());
        meta.setLore(cloneMeta.getLore());
        clone.setItemMeta(meta);
        this.item = clone;
    }

    public void setLore(List<String> lore) {
        this.lore.clear();
        this.lore.addAll(lore);
        setMeta(title, lore);
    }

    public void disable(List<String> reason) {
        lore.add("");
        lore.add(net.md_5.bungee.api.ChatColor.RED + "" + net.md_5.bungee.api.ChatColor.BOLD + "Setting disabled!");
        lore.add(net.md_5.bungee.api.ChatColor.GRAY + "You won't be able to change it");
        lore.add(net.md_5.bungee.api.ChatColor.DARK_GRAY + "Reason:");
        if (reason.isEmpty()) {
            lore.add(net.md_5.bungee.api.ChatColor.DARK_GRAY + "" + net.md_5.bungee.api.ChatColor.ITALIC + "Disabled by plugin");
        } else {
            for (String line : reason) {
                lore.add(net.md_5.bungee.api.ChatColor.DARK_GRAY + "" + net.md_5.bungee.api.ChatColor.ITALIC + line);
            }
        }

        lore.add("");
        setValue(value); // To set the lore
    }

    public ItemStack getStack() {
        return item;
    }

    public void setValue(T value) {
        this.value = value;
        String title = this.title;

        List<String> newLore = new ArrayList<>(lore);
        if (value != null) {
            String parsedValue = value.toString();
            if (value instanceof NamedEnum namedEnum) {
                parsedValue = namedEnum.getName();

            } else if (value instanceof ChatColor color) {
                parsedValue = color + color.name(); // "color" alone is the legacy symbol + the color code

            } else if (value instanceof Keyed keyed) {
                parsedValue = keyed.getKey().getKey();

            } else if (value instanceof Double) {
                parsedValue = String.valueOf(Utils.round((double) value));

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

            newLore.add("&9Current value: &7" + parsedValue);

            if (changeTitle) {
                title = title + ": &e" + parsedValue;
            }
        }

        setMeta(title, newLore);
    }

    public Object getValue() {
        return value;
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
            super(material, title, baseLore, initialValue, changeTitle);
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

        public double changeValue(boolean increase, boolean small, Double min) {
            return changeValue(increase, small, min, null);
        }

        public double changeValue(boolean increase, boolean small, Double min, Double max) {
            double change;
            if (small && smallEnabled) {
                change = (increase) ? smallChange : -smallChange;
            } else {
                change = (increase) ? bigChange : -bigChange;
            }

            double filterMax = (max == null) ? (value + change) : Math.min(max, value + change);
            double filterMin = (min == null) ? filterMax : Math.max(min, filterMax);
            setValue(Utils.round(filterMin));
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
            this(new ItemStack(material), title, baseLore, initialValue, changeTitle);
        }

        public BooleanItem(ItemStack item, String title, List<String> baseLore, boolean initialValue, boolean changeTitle) {
            super(item, title, baseLore, initialValue, changeTitle);

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

    public static class EnumItem<T extends Enum<T>> extends Item<T> {
        public EnumItem(Material material, String title, String lore, T initialValue) {
            this(material, title, List.of(lore), initialValue, false);
        }

        public EnumItem(Material material, String title, List<String> baseLore, T initialValue, boolean changeTitle) {
            this(new ItemStack(material), title, baseLore, initialValue, changeTitle);
        }

        public EnumItem(ItemStack item, String title, List<String> baseLore, T initialValue, boolean changeTitle) {
            super(item, title, baseLore, initialValue, changeTitle);

            lore.add("");
            lore.add("&7Click to change");
            lore.add("");
            setValue(initialValue);
        }

        /**
         * Gets the next enum value from enum class of the given enum.
         * @return The next enum in the enum class or the first if the provided one was the last.
         */
        public T changeValue() {
            T[] values = value.getDeclaringClass().getEnumConstants();
            int currentIndex = value.ordinal();
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
            super(material, title, baseLore, value, false);
            lore.add("");
            setValue(value);
        }

        public ClickableItem(ItemStack item, String title, List<String> baseLore, String value) {
            super(item, title, baseLore, value, false);
            lore.add("");
            setValue(value);
        }
    }

    public static class RegistryItem extends Item<Keyed> {
        private final boolean canBeNone;
        private boolean isNone;

        public RegistryItem(Material material, String title, Keyed initialValue) {
            this(material, title, List.of(), initialValue, false, false);
        }

        public RegistryItem(Material material, String title, List<String> baseLore, Keyed initialValue, boolean canBeNone, boolean startNone) {
            super(material, title, baseLore, initialValue, false);
            this.canBeNone = canBeNone;
            this.isNone = canBeNone && startNone;

            lore.add("");
            lore.add("&7Click to change");
            lore.add("");
            // Manually set "NONE" if it can be none.
            if (isNone) {
                setValueNone();
            } else {
                setValue(value);
            }
        }

        private void setValueNone() {
            List<String> newLore = new ArrayList<>(lore);
            newLore.add("&9Current value: &7NONE");
            setMeta(title, newLore);
        }

        // No "stream" method available for Registry class in 1.19.4 apparently...
        @SuppressWarnings("unchecked")
        public Keyed changeValue() {
            Class<? extends Keyed> clazz = value.getClass();
            // Get the actual class for the registry (for example, org.bukkit.Art from org.bukkit.craftbukkit.CraftArt)
            for (Class<?> interfaceClass : value.getClass().getInterfaces()) {
                if (Keyed.class.isAssignableFrom(interfaceClass) && interfaceClass != Keyed.class) {
                    clazz = (Class<? extends Keyed>) interfaceClass;
                }
            }

            Registry<? extends Keyed> registry = Objects.requireNonNull(Bukkit.getRegistry(clazz));
            Iterator<? extends Keyed> iterator = registry.iterator();

            Keyed first = null;
            boolean foundCurrent = false;

            while (iterator.hasNext() && (!isNone || first == null)) {
                Keyed registryValue = iterator.next();

                // Store the first value in case the current one is the last one.
                if (first == null) {
                    first = registryValue;
                }

                // If the current value was found in the previous iteration, it means that the value
                // from this iteration is the next value in the "list".
                if (foundCurrent) {
                    setValue(registryValue);
                    return registryValue;
                }

                // If the value from this iteration is the same as the current value, set the variable to
                // true so that the value from the next iteration is set.
                if (registryValue.equals(value)) {
                    foundCurrent = true;
                }
            }

            // If the current value is none, the new one will be the first one from the iterator.
            if (isNone) {
                isNone = false;
                setValue(first);
                return first;

            // If this item can be none of the keys and the current one is the last entry, set the new value
            // to none. The next time the user clicks on the button, it'll be set to the first value.
            } else if (canBeNone) {
                isNone = true;
                value = first;
                setValueNone();
                return null;
            }

            // If the current value was not found, or it was the last one in the "list", set the current
            // value to the first one from  the iterator.
            setValue(first);
            return first;
        }
    }
}