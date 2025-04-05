package me.lucaaa.advanceddisplays.integrations;

import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.compatibilities.CompatibilitiesManager;
import io.th0rgal.oraxen.compatibilities.CompatibilityProvider;
import me.lucaaa.advanceddisplays.AdvancedDisplays;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

public class OraxenCompat extends CompatibilityProvider<AdvancedDisplays> implements Integration {
    public OraxenCompat(AdvancedDisplays plugin) {
        plugin.log(Level.INFO, "Oraxen detected. Hooking up implementation...");

        CompatibilitiesManager.addCompatibility(plugin.getName(), this.getClass());
    }

    @Override
    public ItemStack getItemStack(String id) {
        return OraxenItems.getItemById(id).build();
    }

    @Override
    public BlockData getBlockData(String id) {
        return OraxenBlocks.getOraxenBlockData(id);
    }
}