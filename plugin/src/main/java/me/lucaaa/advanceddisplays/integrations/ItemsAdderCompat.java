package me.lucaaa.advanceddisplays.integrations;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderCompat implements Integration {
    @Override
    public ItemStack getItemStack(String id) {
        return CustomStack.getInstance(id).getItemStack();
    }

    @Override
    public BlockData getBlockData(String id) {
        return CustomBlock.getInstance(id).getBaseBlockData();
    }
}