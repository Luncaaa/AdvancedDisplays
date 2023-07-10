package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.utils.DisplayType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;

import java.util.Objects;

public class ADBlockDisplay extends BaseDisplay implements DisplayMethods {
    private ConfigurationSection settings;
    private BlockData block;

    public ADBlockDisplay(ConfigManager configManager, BlockDisplay display) {
        super(DisplayType.BLOCK, configManager, display);
        this.settings = this.config.getConfigurationSection("settings");

        if (this.settings != null) {
            this.block = Objects.requireNonNull(Material.getMaterial(Objects.requireNonNull(this.settings.getString("block")))).createBlockData();
        }
    }

    @Override
    public void sendMetadataPackets(Player player) {
        this.sendBaseMetadataPackets(player);
        this.packets.setBlock(this.displayId, this.block, player);
    }

    public ADBlockDisplay create(BlockData block) {
        this.settings = this.config.createSection("settings");
        this.setBlock(block);
        return this;
    }

    public BlockData getBlock() {
        return this.block;
    }
    public void setBlock(BlockData block) {
        this.block = block;
        this.settings.set("block", block.getMaterial().name());
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.packets.setBlock(this.displayId, block, onlinePlayer);
        }
        this.save();
    }
}
