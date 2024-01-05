package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.common.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;

public class ADBlockDisplay extends ADBaseDisplay implements DisplayMethods, me.lucaaa.advanceddisplays.api.displays.BlockDisplay {
    private ConfigurationSection settings = null;
    private BlockData block;

    public ADBlockDisplay(ConfigManager configManager, BlockDisplay display) {
        super(DisplayType.BLOCK, configManager, display);
        this.settings = this.config.getConfigurationSection("settings");

        if (this.settings != null) {
            String blockData = "minecraft:" + Objects.requireNonNull(this.settings.getString("block")).toLowerCase() + "[";
            ConfigurationSection dataSection = Objects.requireNonNull(this.settings.getConfigurationSection("blockData"));
            ArrayList<String> dataParts = new ArrayList<>();
            for (String dataKey : dataSection.getKeys(false)) {
                dataParts.add(dataKey + "=" + dataSection.get(dataKey));
            }
            blockData = blockData.concat(String.join(",", dataParts));
            this.block = Bukkit.getServer().createBlockData(blockData + "]");
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

            String fullData = block.getAsString().substring(block.getAsString().indexOf("[") + 1, block.getAsString().lastIndexOf("]"));
            ConfigurationSection dataSection = this.settings.createSection("blockData");
            for (String data : fullData.split(",")) {
                String[] dataPart = data.split("=");
                dataSection.set(dataPart[0], dataPart[1]);
            }

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
