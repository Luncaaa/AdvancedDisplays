package me.lucaaa.advanceddisplays.integrations;

import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

public interface Integration {
    ItemStack getItemStack(String id);

    BlockData getBlockData(String id);
}