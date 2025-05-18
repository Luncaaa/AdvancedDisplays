package me.lucaaa.advanceddisplays.data;

import me.lucaaa.advanceddisplays.inventory.items.ColorItems;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;

public class ADChatColor {
    private final ColorItems.ColorComponent component;
    public final ChatColor color;
    public final ChatColor red;
    public final ChatColor green;
    public final ChatColor blue;
    public final ChatColor alpha;

    public ADChatColor(Color color, ColorItems.ColorComponent component) {
        this.component = component;

        this.color = ChatColor.of(new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
        this.red = ChatColor.of(new java.awt.Color(color.getRed(), 0, 0));
        this.green = ChatColor.of(new java.awt.Color(0, color.getGreen(), 0));
        this.blue = ChatColor.of(new java.awt.Color(0, 0, color.getBlue()));
        int gray = (int) (color.getAlpha() * 255.0 / 255);
        this.alpha = ChatColor.of(new java.awt.Color(gray, gray, gray));
    }

    public ChatColor fromComponent() {
        return switch (component) {
            case RED -> red;
            case GREEN -> green;
            case BLUE -> blue;
            case ALPHA -> alpha;
            case ALL -> color;
        };
    }

    public static Color fromChatColor(ChatColor color) {
        java.awt.Color color1 = color.getColor();
        return Color.fromARGB(color1.getAlpha(), color1.getRed(), color1.getGreen(), color1.getBlue());
    }
}