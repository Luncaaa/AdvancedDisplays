package me.lucaaa.advanceddisplays.inventory.items;

import me.lucaaa.advanceddisplays.common.utils.HeadUtils;
import me.lucaaa.advanceddisplays.common.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GlobalItems {
    public static final ItemStack CANCEL = HeadUtils.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==", "&cCancel", List.of("&eGo back without saving"));
    public static final ItemStack DONE = HeadUtils.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMxMmNhNDYzMmRlZjVmZmFmMmViMGQ5ZDdjYzdiNTVhNTBjNGUzOTIwZDkwMzcyYWFiMTQwNzgxZjVkZmJjNCJ9fX0=", "&aDone", List.of("&eGo back and save your changes"));

    // Create without clicks
    public static ItemStack create(Material material, String title, String lore, Object value) {
        return create(material, title, List.of(lore), value, false, false, 0.0, 0.0, false);
    }

    public static ItemStack create(Material material, String title, List<String> lore, Object value, boolean setTitle) {
        return create(material, title, lore, value, false, false, 0.0, 0.0, setTitle);
    }

    public static ItemStack create(Material material, String title, String rawLore, Object value, boolean bigClicks, boolean smallClicks, double bigChange, double smallChange, boolean changeTitle) {
        return create(material, title, List.of(rawLore), value, bigClicks, smallClicks, bigChange, smallChange, changeTitle);
    }

    public static ItemStack create(Material material, String title, List<String> rawLore, Object value, boolean bigClicks, boolean smallClicks, double bigChange, double smallChange) {
        return create(material, title, rawLore, value, bigClicks, smallClicks, bigChange, smallChange, false);
    }

    public static ItemStack create(Material material, String title, List<String> rawLore, Object value, boolean bigClicks, boolean smallClicks, double bigChange, double smallChange, boolean setTitle) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

        if (setTitle) {
            meta.setDisplayName(Utils.getColoredText("&6" + title + ": &7" + value));
        } else {
            meta.setDisplayName(Utils.getColoredText("&6" + title));
        }

        ArrayList<String> lore = new ArrayList<>(rawLore.stream().map(line -> "&e" + line).toList());
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
            lore.add("&9Current value: &7" + value);
        }

        meta.setLore(lore.stream().map(Utils::getColoredText).toList());

        item.setItemMeta(meta);
        return item;
    }
}
