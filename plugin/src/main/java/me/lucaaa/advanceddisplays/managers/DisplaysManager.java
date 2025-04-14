package me.lucaaa.advanceddisplays.managers;

import com.google.common.io.Files;
import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.data.AttachedDisplay;
import me.lucaaa.advanceddisplays.displays.*;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.nms_common.PacketInterface;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.File;
import java.util.*;

public class DisplaysManager {
    private final AdvancedDisplays plugin;
    private final PacketInterface packets;
    private final String configsFolder;
    private final Map<String, ADBaseDisplay> displays = new HashMap<>();
    private final Map<Player, AttachedDisplay> attachDisplays = new HashMap<>();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public DisplaysManager(AdvancedDisplays plugin, String configsFolder, boolean createFolders) {
        this.plugin = plugin;
        this.packets = plugin.getPacketsManager().getPackets();
        this.configsFolder = configsFolder;

        // Gets the displays folder and creates it if it doesn't exist.
        File displaysFolder = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + configsFolder);
        if (!displaysFolder.exists() && createFolders) displaysFolder.mkdirs();

        // If the displays folder is not empty, load the displays.
        if (displaysFolder.exists()) {
            for (File configFile : Objects.requireNonNull(displaysFolder.listFiles())) {
                if (configFile.isDirectory()) continue;
                ConfigManager configManager = new ConfigManager(plugin, configsFolder + File.separator + configFile.getName(), false);

                if (!configManager.getConfig().isConfigurationSection("view-conditions")) {
                    ConversionManager.setConversionNeeded(plugin, true);
                    break;
                }

                loadDisplay(configManager);
            }
        }
    }

    public ADTextDisplay createAttachedDisplay(PlayerInteractEvent event, AttachedDisplay display) {
        if (event.getClickedBlock() == null) return null;

        Player player = event.getPlayer();
        BlockFace clickedFace = event.getBlockFace();

        float yaw;
        if (clickedFace == BlockFace.UP || clickedFace == BlockFace.DOWN) {
            // Get the yaw depending on where the player is facing.
            yaw = AttachedDisplay.getYaw(player.getFacing().getOppositeFace());
        } else {
            // Get the yaw depending on the clicked face.
            yaw = AttachedDisplay.getYaw(clickedFace);
        }

        float pitch = 0.0f;
        if (clickedFace == BlockFace.UP) {
            pitch = -90.0f;
        } else if (clickedFace == BlockFace.DOWN) {
            pitch = 90.0f;
        }

        Location location;
        if (clickedFace == BlockFace.UP || clickedFace == BlockFace.DOWN) {
            double addY = (clickedFace == BlockFace.UP) ? 1.001 : -0.001;
            boolean add = clickedFace == BlockFace.UP;
            Location loc = event.getClickedBlock().getLocation().clone().add(0.0, addY, 0.0);
            float pos = AttachedDisplay.getPos(player.getFacing().getOppositeFace(), display.side());
            location = AttachedDisplay.addSides(player.getFacing(), loc, pos, add);
        } else {
            float pos = AttachedDisplay.getPos(clickedFace, display.side());
            location = AttachedDisplay.addSides(clickedFace, event.getClickedBlock().getLocation(), pos, false);
        }

        ADTextDisplay newDisplay = createTextDisplay(location, display.name(), display.content(), display.saveToConfig());

        if (newDisplay != null) {
            newDisplay.setBillboard(Display.Billboard.FIXED);
            newDisplay.setSeeThrough(false);
            newDisplay.setRotation(yaw, pitch);
        }

        return newDisplay;
    }

    public ADTextDisplay createTextDisplay(Location location, String name, String value, boolean saveToConfig) {
        if (displays.containsKey(name)) {
            return null;
        }

        TextDisplay newDisplayPacket = packets.createTextDisplay(location);
        ADTextDisplay display = new ADTextDisplay(plugin, this, name, newDisplayPacket, saveToConfig).create(value);
        createGeneral(name, display);
        return display;
    }

    public ADTextDisplay createTextDisplay(Location location, String name, Component value, boolean saveToConfig) {
        if (displays.containsKey(name)) {
            return null;
        }

        TextDisplay newDisplayPacket = packets.createTextDisplay(location);
        ADTextDisplay display = new ADTextDisplay(plugin, this, name, newDisplayPacket, saveToConfig).create(value);
        createGeneral(name, display);
        return display;
    }

    public ADItemDisplay createItemDisplay(Location location, String name, Material value, boolean saveToConfig) {
        if (displays.containsKey(name)) {
            return null;
        }

        ItemDisplay newDisplayPacket = packets.createItemDisplay(location);
        ADItemDisplay display = new ADItemDisplay(plugin, this, name, newDisplayPacket, saveToConfig).create(value);
        createGeneral(name, display);
        return display;
    }

    public ADBlockDisplay createBlockDisplay(Location location, String name, BlockData value, boolean saveToConfig) {
        if (displays.containsKey(name)) {
            return null;
        }

        BlockDisplay newDisplayPacket = packets.createBlockDisplay(location);
        ADBlockDisplay display = new ADBlockDisplay(plugin, this, name, newDisplayPacket, saveToConfig).create(value);
        createGeneral(name, display);
        return display;
    }

    private void createGeneral(String name, ADBaseDisplay display) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            display.sendBaseMetadataPackets(onlinePlayer);
        }

        plugin.getInteractionsManager().addInteraction(display.getInteractionId(), display);
        displays.put(name, display);
    }

    public boolean removeDisplay(String name) {
        if (!displays.containsKey(name)) {
            return false;
        }

        removeDisplay(displays.get(name), true);
        return true;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void removeDisplay(ADBaseDisplay display, boolean deleteFile) {
        if (display.getConfigManager() != null && deleteFile) {
            display.getConfigManager().getFile().delete();
        }

        if (display instanceof ADTextDisplay) ((ADTextDisplay) display).stopRunnable();
        display.destroy();
        display.stopTicking();
        plugin.getInteractionsManager().removeInteraction(display.getInteractionId());
        plugin.getInventoryManager().handleRemoval(display);
        displays.remove(display.getName());
        display.setRemoved();
    }

    public void removeAll(boolean onReload) {
        for (ADBaseDisplay display : displays.values()) {
            removeDisplay(display, !onReload);
        }

        attachDisplays.clear();
    }

    public ADBaseDisplay getDisplayFromMap(String name) {
        return displays.get(name);
    }

    public void spawnDisplays(Player player) {
        for (ADBaseDisplay display : displays.values()) {
            if (display.getLocation().getWorld() != player.getLocation().getWorld()) continue;
            if (display.getVisibilityManager().isVisibleByPlayer(player)) display.spawnToPlayer(player);
        }
    }

    public void loadDisplay(ConfigManager configManager) {
        DisplayType displayType = DisplayType.valueOf(configManager.getConfig().getString("type"));
        ConfigurationSection locationSection = Objects.requireNonNull(configManager.getConfig().getConfigurationSection("location"));
        String world = locationSection.getString("world", "world");
        double x = locationSection.getDouble("x");
        double y = locationSection.getDouble("y");
        double z = locationSection.getDouble("z");
        Location location = new Location(Bukkit.getWorld(world), x, y, z);
        String name = Files.getNameWithoutExtension(configManager.getFile().getName());

        ADBaseDisplay newDisplay = null;
        switch (displayType) {
            case BLOCK -> {
                BlockDisplay newDisplayPacket = packets.createBlockDisplay(location);
                newDisplay = new ADBlockDisplay(plugin, this, configManager, name, newDisplayPacket);
            }
            case TEXT -> {
                TextDisplay newDisplayPacket = packets.createTextDisplay(location);
                newDisplay = new ADTextDisplay(plugin, this, configManager, name, newDisplayPacket);
            }
            case ITEM -> {
                ItemDisplay newDisplayPacket = packets.createItemDisplay(location);
                newDisplay = new ADItemDisplay(plugin, this, configManager, name, newDisplayPacket);
            }
        }

        displays.put(configManager.getFile().getName().replace(".yml", ""), newDisplay);
        plugin.getInteractionsManager().addInteraction(newDisplay.getInteractionId(), newDisplay);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            ((DisplayMethods) newDisplay).sendMetadataPackets(onlinePlayer);
        }
    }

    public BaseDisplay getDisplayFromLoc(Location location, double radius, boolean closest) {
        double closestDistance = radius;
        BaseDisplay closestDisplay = null;

        for (BaseDisplay display : displays.values()) {
            double distanceSquared = display.getLocation().distanceSquared(location);
            boolean isInRadius = distanceSquared <= Math.pow(radius, 2);

            if (closest && isInRadius) {
                if (closestDistance > distanceSquared) {
                    closestDistance = distanceSquared;
                    closestDisplay = display;
                }

            } else if (isInRadius) {
                return display;
            }
        }

        return closestDisplay;
    }

    public void addAttachingPlayer(Player player, AttachedDisplay display) {
        attachDisplays.put(player, display);
    }

    public boolean isPlayerAttaching(Player player) {
        return attachDisplays.containsKey(player);
    }

    public AttachedDisplay getAttachingDisplay(Player player) {
        return attachDisplays.remove(player);
    }

    public void removeAttachingDisplay(Player player) {
        attachDisplays.remove(player);
    }

    public Map<String, ADBaseDisplay> getDisplays() {
        return displays;
    }

    public boolean existsDisplay(String name) {
        return displays.containsKey(name);
    }

    public String getConfigsFolder() {
        return configsFolder;
    }
}