package me.lucaaa.advanceddisplays.inventory.items;

import me.lucaaa.advanceddisplays.common.utils.Utils;
import me.lucaaa.advanceddisplays.inventory.InventoryUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ColorItems {
    public final ItemStack RED_1;
    public final ItemStack RED_10;
    public final ItemStack RED_100;
    public final ItemStack RED_PREVIEW;

    public final ItemStack GREEN_1;
    public final ItemStack GREEN_10;
    public final ItemStack GREEN_100;
    public final ItemStack GREEN_PREVIEW;

    public final ItemStack BLUE_1;
    public final ItemStack BLUE_10;
    public final ItemStack BLUE_100;
    public final ItemStack BLUE_PREVIEW;

    public final ItemStack ALPHA_1;
    public final ItemStack ALPHA_10;
    public final ItemStack ALPHA_100;
    public final ItemStack ALPHA_PREVIEW;

    public final ItemStack PREVIEW;

    public ColorItems(Color color, boolean alphaEnabled) {
        RED_1 = create(Material.RED_CONCRETE, "&cRed ±1", List.of("Adds or subtracts 1 red from the color"));
        RED_10 = create(Material.RED_CONCRETE, "&cRed ±10", List.of("Adds or subtracts 10 red from the color"));
        RED_100 = create(Material.RED_CONCRETE, "&cRed ±100", List.of("Adds or subtracts 100 red from the color"));
        RED_PREVIEW = InventoryUtils.changeArmorColor(create("&cRed preview", color.getRed(), ColorComponent.RED), Color.fromRGB(color.getRed(), 0, 0));

        GREEN_1 = create(Material.LIME_CONCRETE, "&aGreen ±1", List.of("Adds or subtracts 1 green from the color"));
        GREEN_10 = create(Material.LIME_CONCRETE, "&aGreen ±10", List.of("Adds or subtracts 10 green from the color"));
        GREEN_100 = create(Material.LIME_CONCRETE, "&aGreen ±100", List.of("Adds or subtracts 100 green from the color"));
        GREEN_PREVIEW = InventoryUtils.changeArmorColor(create("&aGreen preview", color.getGreen(), ColorComponent.GREEN), Color.fromRGB(0, color.getGreen(), 0));

        BLUE_1 = create(Material.BLUE_CONCRETE, "&9Blue ±1", List.of("Adds or subtracts 1 blue from the color"));
        BLUE_10 = create(Material.BLUE_CONCRETE, "&9Blue ±10", List.of("Adds or subtracts 10 blue from the color"));
        BLUE_100 = create(Material.BLUE_CONCRETE, "&9Blue ±100", List.of("Adds or subtracts 100 blue from the color"));
        BLUE_PREVIEW = InventoryUtils.changeArmorColor(create("&9Blue preview", color.getBlue(), ColorComponent.BLUE), Color.fromRGB(0, 0, color.getBlue()));

        ALPHA_1 = create(Material.WHITE_CONCRETE, "&fAlpha ±1", List.of("Adds or subtracts 1 alpha from the color"));
        ALPHA_10 = create(Material.WHITE_CONCRETE, "&fAlpha ±10", List.of("Adds or subtracts 10 alpha from the color"));
        ALPHA_100 = create(Material.WHITE_CONCRETE, "&fAlpha ±100", List.of("Adds or subtracts 100 alpha from the color"));
        ALPHA_PREVIEW = InventoryUtils.changeArmorColor(create("&fAlpha preview", color.getAlpha(), ColorComponent.ALPHA), Color.fromRGB(color.getAlpha(), color.getAlpha(), color.getAlpha()));

        PREVIEW = InventoryUtils.changeArmorColor(createPreview(color, alphaEnabled, "Color Preview"), color);
    }

    public static ItemStack createPreview(Color color, boolean alphaEnabled, String title) {
        ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
        item.setItemMeta(setPreviewLore(Objects.requireNonNull(item.getItemMeta()), color, alphaEnabled, title));
        return item;
    }

    private ItemStack create(String title, int value, ColorComponent component) {
        ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);

        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

        meta.setDisplayName(Utils.getColoredText(title));

        ArrayList<String> lore = new ArrayList<>();
        ChatColor valueColor = switch (component) {
            case RED -> ChatColor.of(new java.awt.Color(value, 0, 0));
            case GREEN -> ChatColor.of(new java.awt.Color(0, value, 0));
            case BLUE -> ChatColor.of(new java.awt.Color(0, 0, value));
            case ALPHA -> {
                int gray = (int) (value * 255.0 / 255);
                yield ChatColor.of(new java.awt.Color(gray, gray, gray));
            }
        };
        lore.add(Utils.getColoredText("&9Current value: " + valueColor + component + " (" + value + ")"));
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack create(Material material, String title, List<String> rawLore) {
        ItemStack item = new ItemStack(material);

        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

        meta.setDisplayName(Utils.getColoredText("&6" + title));

        ArrayList<String> lore = new ArrayList<>(rawLore.stream().map(line -> "&e" + line).toList());
        lore.add("");
        lore.add("&7Use &cLEFT_CLICK &7to add");
        lore.add("&7Use &cRIGHT_CLICK &7to subtract");

        meta.setLore(lore.stream().map(Utils::getColoredText).toList());

        item.setItemMeta(meta);
        return item;
    }

    public enum ColorComponent {
        RED,
        GREEN,
        BLUE,
        ALPHA
    }

    public static ItemMeta setPreviewLore(ItemMeta meta, Color color, boolean alphaEnabled, String title) {
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
        return meta;
    }
}