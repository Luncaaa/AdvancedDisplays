package me.lucaaa.advanceddisplays.api.displays;

import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public interface BlockDisplay extends BaseDisplay {
    /**
     * Gets the displayed block.
     * @return The displayed block.
     */
    BlockData getBlock();
    /**
     * Sets the displayed block for everyone.
     * @param block The new displayed block.
     */
    void setBlock(BlockData block);
    /**
     * Sets the displayed block for a specific player.
     * @param block The new displayed block.
     * @param player The player who will see the new displayed block.
     */
    void setBlock(BlockData block, Player player);
}
