package me.lucaaa.advanceddisplays.inventory.items;

import me.lucaaa.advanceddisplays.data.Utils;
import me.lucaaa.advanceddisplays.data.ADChatColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.ArrayList;
import java.util.List;

public class ColorItems {
    public final ColorSelector RED_1;
    public final ColorSelector RED_10;
    public final ColorSelector RED_100;
    public final ColorPreview RED_PREVIEW;

    public final ColorSelector GREEN_1;
    public final ColorSelector GREEN_10;
    public final ColorSelector GREEN_100;
    public final ColorPreview GREEN_PREVIEW;

    public final ColorSelector BLUE_1;
    public final ColorSelector BLUE_10;
    public final ColorSelector BLUE_100;
    public final ColorPreview BLUE_PREVIEW;

    public final ColorSelector ALPHA_1;
    public final ColorSelector ALPHA_10;
    public final ColorSelector ALPHA_100;
    public final ColorPreview ALPHA_PREVIEW;

    public final ColorPreview PREVIEW;

    public ColorItems(Material previewMaterial, Color color, boolean alphaEnabled) {
        RED_1 = new ColorSelector(ColorComponent.RED, 1, color.getRed());
        RED_10 = new ColorSelector(ColorComponent.RED, 10, color.getRed());
        RED_100 = new ColorSelector(ColorComponent.RED, 100, color.getRed());
        RED_PREVIEW = new ColorPreview(previewMaterial, "&cRed preview", color, ColorComponent.RED, false);

        GREEN_1 = new ColorSelector(ColorComponent.GREEN, 1, color.getGreen());
        GREEN_10 = new ColorSelector(ColorComponent.GREEN, 10, color.getGreen());
        GREEN_100 = new ColorSelector(ColorComponent.GREEN, 100, color.getGreen());
        GREEN_PREVIEW = new ColorPreview(previewMaterial, "&aGreen preview", color, ColorComponent.GREEN, false);

        BLUE_1 = new ColorSelector(ColorComponent.BLUE, 1, color.getBlue());
        BLUE_10 = new ColorSelector(ColorComponent.BLUE, 10, color.getBlue());
        BLUE_100 = new ColorSelector(ColorComponent.BLUE, 100, color.getBlue());
        BLUE_PREVIEW = new ColorPreview(previewMaterial, "&9Blue preview", color, ColorComponent.BLUE, false);

        ALPHA_1 = new ColorSelector(ColorComponent.ALPHA, 1, color.getAlpha());
        ALPHA_10 = new ColorSelector(ColorComponent.ALPHA, 10, color.getAlpha());
        ALPHA_100 = new ColorSelector(ColorComponent.ALPHA, 100, color.getAlpha());
        ALPHA_PREVIEW = new ColorPreview(previewMaterial, "&fAlpha preview", color, ColorComponent.ALPHA, true);

        PREVIEW = new ColorPreview(previewMaterial, "Color preview", color, ColorComponent.ALL, alphaEnabled);
    }

    public static class ColorPreview extends Item<Double> {
        private final ColorItems.ColorComponent component;
        private final boolean alphaEnabled;

        public ColorPreview(String title, Color color, ColorItems.ColorComponent component, boolean alphaEnabled) {
            this(Material.LEATHER_CHESTPLATE, title, List.of(), color, component, alphaEnabled);
        }

        public ColorPreview(Material material, String title, Color color, ColorItems.ColorComponent component, boolean alphaEnabled) {
            this(material, title, List.of(), color, component, alphaEnabled);
        }

        public ColorPreview(Material material, String title, List<String> lore, Color color, ColorItems.ColorComponent component, boolean alphaEnabled) {
            super(material, title, lore, null, false);
            this.component = component;
            this.alphaEnabled = alphaEnabled;
            // Title and armor color will be set in the setColor function.
            setColor(color);
        }

        public void setColor(Color color) {
            ADChatColor colors = new ADChatColor(color, component);

            List<String> lore = new ArrayList<>(this.lore);
            lore.add("");

            if (component == ColorItems.ColorComponent.ALL) {
                lore.add(Utils.getColoredText("&9Current values:"));
                lore.add(ChatColor.of("#FF0000") + "Red: " + colors.red + color.getRed());
                lore.add(ChatColor.of("#00FF00") + "Green: " + colors.green + color.getGreen());
                lore.add(ChatColor.of("#0000FF") + "Blue: " + colors.blue + color.getBlue());
                if (alphaEnabled) lore.add(ChatColor.WHITE + "Alpha: " + colors.alpha + color.getAlpha());
                lore.add("");
                lore.add("&9Preview: " + colors.fromComponent() + "Color preview");
            } else {
                int componentNumber = switch (component) {
                    case RED -> color.getRed();
                    case GREEN -> color.getGreen();
                    case BLUE -> color.getBlue();
                    case ALPHA -> color.getAlpha();
                    default -> throw new IllegalStateException("Unexpected value: " + component);
                };
                lore.add("&9Current value: " + colors.fromComponent() + component.name() + component.code + " (" + componentNumber + ")");
            }

            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                Color parsedColor = ADChatColor.fromChatColor(colors.fromComponent());
                if (meta instanceof LeatherArmorMeta leatherMeta) {
                    leatherMeta.setColor(parsedColor);
                } else if (meta instanceof PotionMeta potionMeta) {
                    potionMeta.setColor(parsedColor);
                }

                item.setItemMeta(meta);
            }

            setMeta(title, lore);
        }
    }

    public static class ColorSelector extends Item.StepItem {
        private final int amount;

        public ColorSelector(ColorItems.ColorComponent component, int amount, int color) {
            super(
                    component.material,
                    component.name + " ±" + amount,
                    List.of("Adds or subtracts " + amount + " " + component.name + " from the color"),
                    color,
                    amount
            );

            this.amount = amount;
        }

        public void updateColor(double newAmount) {
            setValue(newAmount);
        }

        public int getAmount() {
            return amount;
        }
    }

    public enum ColorComponent {
        RED(Material.RED_CONCRETE, "Red", "&c"),
        GREEN(Material.LIME_CONCRETE, "Green", "&a"),
        BLUE(Material.BLUE_CONCRETE, "Blue", "&9"),
        ALPHA(Material.WHITE_CONCRETE, "Alpha", "&f"),
        ALL(Material.BARRIER, "All", "&4"); // The barrier shouldn't appear anywhere

        private final Material material;
        private final String name;
        private final String code;

        ColorComponent(Material material, String name, String code) {
            this.material = material;
            this.name = name;
            this.code = code;
        }
    }
}