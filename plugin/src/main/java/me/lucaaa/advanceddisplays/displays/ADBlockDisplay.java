package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.data.Compatibility;
import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.managers.DisplaysManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ADBlockDisplay extends ADBaseDisplay implements me.lucaaa.advanceddisplays.api.displays.BlockDisplay {
    private BlockData block;

    // Compatibility
    private String oraxenId;
    private String itemsAdderId;

    public ADBlockDisplay(AdvancedDisplays plugin, DisplaysManager displaysManager, ConfigManager configManager, String name, BlockDisplay display) {
        super(plugin, displaysManager, configManager, name, DisplayType.BLOCK, display);

        if (settings != null) {
            if (settings.isString("oraxen") && plugin.isIntegrationLoaded(Compatibility.ORAXEN)) {
                oraxenId = settings.getString("oraxen");
                block = plugin.getIntegration(Compatibility.ORAXEN).getBlockData(oraxenId);
                return;
            }

            if (settings.isString("itemsAdder") && plugin.isIntegrationLoaded(Compatibility.ITEMS_ADDER)) {
                itemsAdderId = settings.getString("itemsAdder");
                block = plugin.getIntegration(Compatibility.ITEMS_ADDER).getBlockData(itemsAdderId);
                return;
            }

            String blockData = "minecraft:" + config.getOrDefault("block", Material.BARRIER.name(), settings).toLowerCase() + "[";
            ConfigurationSection dataSection = config.getSection("blockData", settings);
            ArrayList<String> dataParts = new ArrayList<>();
            for (String dataKey : dataSection.getKeys(false)) {
                dataParts.add(dataKey + "=" + dataSection.get(dataKey));
            }
            blockData = blockData.concat(String.join(",", dataParts));
            block = Bukkit.getServer().createBlockData(blockData + "]");
        }
    }

    public ADBlockDisplay(AdvancedDisplays plugin, DisplaysManager displaysManager, String name, BlockDisplay display, boolean saveToConfig) {
        super(plugin, displaysManager, name, DisplayType.BLOCK, display, saveToConfig);
    }

    @Override
    public void sendMetadataPackets(Player player) {
        super.sendMetadataPackets(player);
        packets.setMetadata(entityId, player, metadata.VALUE, block);
    }

    public ADBlockDisplay create(BlockData block) {
        setBlock(block);
        return this;
    }

    @Override
    public BlockData getBlock() {
        return block;
    }
    @Override
    public void setBlock(BlockData block) {
        this.block = block;

        if (config != null) {
            if (oraxenId != null) settings.set("oraxen", oraxenId);
            if (itemsAdderId != null) settings.set("itemsAdder", itemsAdderId);
            settings.set("block", block.getMaterial().name());

            ConfigurationSection dataSection = settings.createSection("blockData");
            if (block.getAsString().indexOf("[") > 0) {
                String fullData = block.getAsString().substring(block.getAsString().indexOf("[") + 1, block.getAsString().lastIndexOf("]"));
                for (String data : fullData.split(",")) {
                    String[] dataPart = data.split("=");
                    dataSection.set(dataPart[0], dataPart[1]);
                }
            }
            settings.setComments("blockData", List.of("For more information about what these values are, visit https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/data/BlockData.html"));
            save();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setBlock(block, onlinePlayer);
        }
    }
    @Override
    public void setBlock(BlockData block, Player player) {
        packets.setMetadata(entityId, player, metadata.VALUE, block);
    }
}