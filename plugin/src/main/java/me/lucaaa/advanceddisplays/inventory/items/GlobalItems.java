package me.lucaaa.advanceddisplays.inventory.items;

import me.lucaaa.advanceddisplays.common.utils.DisplayHeadType;
import me.lucaaa.advanceddisplays.common.utils.HeadUtils;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GlobalItems {
    public static final Item.ClickableItem CANCEL = getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==", "&cCancel", List.of("&eGo back without saving"));
    public static final Item.ClickableItem DONE = getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMxMmNhNDYzMmRlZjVmZmFmMmViMGQ5ZDdjYzdiNTVhNTBjNGUzOTIwZDkwMzcyYWFiMTQwNzgxZjVkZmJjNCJ9fX0=", "&aDone", List.of("&eGo back and save your changes"));

    private static Item.ClickableItem getHead(String base64, String title, List<String> lore) {
        ItemStack head = HeadUtils.getHead(
                DisplayHeadType.BASE64,
                base64,
                null,
                null
        );

        return new Item.ClickableItem(head, title, lore);
    }
}