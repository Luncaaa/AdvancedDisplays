package me.lucaaa.advanceddisplays.inventory.items;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GlobalItems {
    public static Item.ClickableItem cancel(AdvancedDisplays plugin) {
        return getHead(plugin.getCachedHeads().CANCEL, "&cCancel", List.of("&eGo back without saving"));
    }
    public static Item.ClickableItem done(AdvancedDisplays plugin) {
        return getHead(plugin.getCachedHeads().DONE, "&aDone", List.of("&eGo back and save your changes"));
    }

    private static Item.ClickableItem getHead(ItemStack head, String title, List<String> lore) {
        return new Item.ClickableItem(head, title, lore, null);
    }
}