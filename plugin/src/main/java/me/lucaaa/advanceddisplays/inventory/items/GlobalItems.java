package me.lucaaa.advanceddisplays.inventory.items;

import me.lucaaa.advanceddisplays.common.utils.DisplayHeadType;
import me.lucaaa.advanceddisplays.common.utils.HeadUtils;
import me.lucaaa.advanceddisplays.common.utils.Utils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class GlobalItems {
    public static final Item CANCEL = getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==", "&cCancel", List.of("&eGo back without saving"));
    public static final Item DONE = getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMxMmNhNDYzMmRlZjVmZmFmMmViMGQ5ZDdjYzdiNTVhNTBjNGUzOTIwZDkwMzcyYWFiMTQwNzgxZjVkZmJjNCJ9fX0=", "&aDone", List.of("&eGo back and save your changes"));

    private static Item getHead(String base64, String title, List<String> lore) {
        ItemStack head = HeadUtils.getHead(
                DisplayHeadType.BASE64,
                base64,
                null,
                null
        );

        ItemMeta meta = Objects.requireNonNull(head.getItemMeta());
        meta.setDisplayName(title);
        meta.setLore(lore.stream().map(Utils::getColoredText).toList());
        head.setItemMeta(meta);

        return new Item(head);
    }
}