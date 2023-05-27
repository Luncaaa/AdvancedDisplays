package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.utils.DisplayType;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;

import java.util.Objects;

public class ADBlockDisplay extends BaseDisplay {
    private final BlockDisplay display;

    private BlockData block;

    public ADBlockDisplay(ConfigManager configManager, BlockDisplay display) {
        super(DisplayType.BLOCK, configManager, display);
        this.display = display;

        if (this.config.getString("block") != null) {
            this.block = Objects.requireNonNull(Material.getMaterial(Objects.requireNonNull(this.config.getString("block")))).createBlockData();
            this.display.setBlock(this.block);
        }
    }

    public ADBlockDisplay create(BlockData block) {
        this.setBlock(block);
        return this;
    }

    public BlockData getBlock() {
        return this.block;
    }
    public void setBlock(BlockData block) {
        this.block = block;
        this.config.set("block", block.getMaterial().name());
        this.display.setBlock(block);
        this.save();
    }
}
