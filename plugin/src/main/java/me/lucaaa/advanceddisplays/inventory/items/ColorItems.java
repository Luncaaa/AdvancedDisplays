package me.lucaaa.advanceddisplays.inventory.items;

import me.lucaaa.advanceddisplays.common.utils.Utils;
import me.lucaaa.advanceddisplays.data.ADChatColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public ColorItems(Color color, boolean alphaEnabled) {
        RED_1 = new ColorSelector(ColorComponent.RED, 1, color.getRed());
        RED_10 = new ColorSelector(ColorComponent.RED, 10, color.getRed());
        RED_100 = new ColorSelector(ColorComponent.RED, 100, color.getRed());
        RED_PREVIEW = new ColorPreview("&cRed preview", color, ColorComponent.RED, false);

        GREEN_1 = new ColorSelector(ColorComponent.GREEN, 1, color.getGreen());
        GREEN_10 = new ColorSelector(ColorComponent.GREEN, 10, color.getGreen());
        GREEN_100 = new ColorSelector(ColorComponent.GREEN, 100, color.getGreen());
        GREEN_PREVIEW = new ColorPreview("&aGreen preview", color, ColorComponent.GREEN, false);

        BLUE_1 = new ColorSelector(ColorComponent.BLUE, 1, color.getBlue());
        BLUE_10 = new ColorSelector(ColorComponent.BLUE, 10, color.getBlue());
        BLUE_100 = new ColorSelector(ColorComponent.BLUE, 100, color.getBlue());
        BLUE_PREVIEW = new ColorPreview("&9Blue preview", color, ColorComponent.BLUE, false);

        ALPHA_1 = new ColorSelector(ColorComponent.ALPHA, 1, color.getAlpha());
        ALPHA_10 = new ColorSelector(ColorComponent.ALPHA, 10, color.getAlpha());
        ALPHA_100 = new ColorSelector(ColorComponent.ALPHA, 100, color.getAlpha());
        ALPHA_PREVIEW = new ColorPreview("&fAlpha preview", color, ColorComponent.ALPHA, true);

        PREVIEW = new ColorPreview("Color preview", color, ColorComponent.ALL, alphaEnabled);
    }

    public static class ColorPreview extends Item<Double> {
        private final ColorItems.ColorComponent component;
        private final boolean alphaEnabled;

        public ColorPreview(String title, Color color, ColorItems.ColorComponent component, boolean alphaEnabled) {
            super(Material.LEATHER_CHESTPLATE, title, List.of(), false, null);
            this.component = component;
            this.alphaEnabled = alphaEnabled;
            // Title and armor color will be set in the setColor function.
            setColor(color);
        }

        public void setColor(Color color) {
            ADChatColor colors = new ADChatColor(color, component);

            List<String> lore = new ArrayList<>();

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

            LeatherArmorMeta leatherMeta = (LeatherArmorMeta) Objects.requireNonNull(item.getItemMeta());
            leatherMeta.setColor(ADChatColor.fromChatColor(colors.fromComponent()));
            item.setItemMeta(leatherMeta);
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