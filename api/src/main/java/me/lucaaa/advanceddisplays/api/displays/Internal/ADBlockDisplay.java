package me.lucaaa.advanceddisplays.api.displays.Internal;

import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.common.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;

import java.util.Objects;

public class ADBlockDisplay extends BaseDisplay implements DisplayMethods, me.lucaaa.advanceddisplays.api.displays.api.BlockDisplay {
    private ConfigurationSection settings = null;
    private BlockData block;

    public ADBlockDisplay(ConfigManager configManager, BlockDisplay display) {
        super(DisplayType.BLOCK, configManager, display);
        this.settings = this.config.getConfigurationSection("settings");

        if (this.settings != null) {
            this.block = Objects.requireNonNull(Material.getMaterial(Objects.requireNonNull(this.settings.getString("block")))).createBlockData();
        }
    }
    public ADBlockDisplay(BlockDisplay display) {
        super(DisplayType.BLOCK, display);
    }

    @Override
    public void sendMetadataPackets(Player player) {
        this.sendBaseMetadataPackets(player);
        this.packets.setBlock(this.displayId, this.block, player);
    }

    public ADBlockDisplay create(BlockData block) {
        if (this.config != null) this.settings = this.config.createSection("settings");
        this.setBlock(block);
        return this;
    }

    @Override
    public BlockData getBlock() {
        return this.block;
    }
    @Override
    public void setBlock(BlockData block) {
        this.block = block;
        if (this.config != null) {
            this.settings.set("block", block.getMaterial().name());
            this.save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.setBlock(block, onlinePlayer);
        }
    }
    @Override
    public void setBlock(BlockData block, Player player) {
        this.packets.setBlock(this.displayId, block, player);
    }
}
