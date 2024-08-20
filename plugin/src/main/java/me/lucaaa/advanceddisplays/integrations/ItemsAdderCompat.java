package me.lucaaa.advanceddisplays.integrations;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import me.lucaaa.advanceddisplays.common.utils.Logger;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

public class ItemsAdderCompat implements Integration {
    public ItemsAdderCompat() {
        Logger.log(Level.INFO, "ItemsAdder detected. Hooking up implementation...");
    }

    @Override
    public ItemStack getItemStack(String id) {
        return CustomStack.getInstance(id).getItemStack();
    }

    @Override
    public BlockData getBlockData(String id) {
        return CustomBlock.getInstance(id).getBaseBlockData();
    }
}