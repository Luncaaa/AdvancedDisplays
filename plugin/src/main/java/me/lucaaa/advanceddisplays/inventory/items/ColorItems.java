package me.lucaaa.advanceddisplays.inventory.items;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ColorItems {
    public final Item RED_1;
    public final Item RED_10;
    public final Item RED_100;
    public final Item RED_PREVIEW;

    public final Item GREEN_1;
    public final Item GREEN_10;
    public final Item GREEN_100;
    public final Item GREEN_PREVIEW;

    public final Item BLUE_1;
    public final Item BLUE_10;
    public final Item BLUE_100;
    public final Item BLUE_PREVIEW;

    public final Item ALPHA_1;
    public final Item ALPHA_10;
    public final Item ALPHA_100;
    public final Item ALPHA_PREVIEW;

    public final Item PREVIEW;

    public ColorItems(Color color, boolean alphaEnabled) {
        RED_1 = new Item("&cRed ±1", "Adds or subtracts 1 red from the color", ColorComponent.RED);
        RED_10 = new Item("&cRed ±10", "Adds or subtracts 10 red from the color", ColorComponent.RED);
        RED_100 = new Item("&cRed ±100", "Adds or subtracts 100 red from the color", ColorComponent.RED);
        RED_PREVIEW = new Item("&cRed preview", color.getRed(), ColorComponent.RED);

        GREEN_1 = new Item("&aGreen ±1", "Adds or subtracts 1 green from the color", ColorComponent.GREEN);
        GREEN_10 = new Item("&aGreen ±10", "Adds or subtracts 10 green from the color", ColorComponent.GREEN);
        GREEN_100 = new Item("&aGreen ±100", "Adds or subtracts 100 green from the color", ColorComponent.GREEN);
        GREEN_PREVIEW = new Item("&aGreen preview", color.getGreen(), ColorComponent.GREEN);

        BLUE_1 = new Item("&9Blue ±1", "Adds or subtracts 1 blue from the color", ColorComponent.BLUE);
        BLUE_10 = new Item("&9Blue ±10", "Adds or subtracts 10 blue from the color", ColorComponent.BLUE);
        BLUE_100 = new Item("&9Blue ±100", "Adds or subtracts 100 blue from the color", ColorComponent.BLUE);
        BLUE_PREVIEW = new Item("&9Blue preview", color.getBlue(), ColorComponent.BLUE);

        ALPHA_1 = new Item("&fAlpha ±1", "Adds or subtracts 1 alpha from the color", ColorComponent.ALPHA);
        ALPHA_10 = new Item("&fAlpha ±10", "Adds or subtracts 10 alpha from the color", ColorComponent.ALPHA);
        ALPHA_100 = new Item("&fAlpha ±100", "Adds or subtracts 100 alpha from the color", ColorComponent.ALPHA);
        ALPHA_PREVIEW = new Item("&fAlpha preview", color.getAlpha(), ColorComponent.ALPHA);

        PREVIEW = new Item(new ItemStack(Material.LEATHER_CHESTPLATE)).setArmorColor(color).setPreviewLore(color, alphaEnabled, "Color Preview");
    }

    public enum ColorComponent {
        RED,
        GREEN,
        BLUE,
        ALPHA
    }
}