package me.lucaaa.advanceddisplays.managers;

import com.google.common.io.Files;
import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.actions.actionTypes.ActionType;
import me.lucaaa.advanceddisplays.data.AttachedDisplay;
import me.lucaaa.advanceddisplays.displays.*;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.data.ConfigAxisAngle4f;
import me.lucaaa.advanceddisplays.data.ConfigVector3f;
import me.lucaaa.advanceddisplays.nms_common.PacketInterface;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.File;
import java.util.*;
import java.util.List;

public class DisplaysManager {
    private final AdvancedDisplays plugin;
    private final PacketInterface packets;
    private final String configsFolder;
    private final Map<String, ADBaseDisplay> displays = new HashMap<>();
    private final boolean isApi;
    private final Map<Player, AttachedDisplay> attachDisplays = new HashMap<>();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public DisplaysManager(AdvancedDisplays plugin, String configsFolder, boolean createFolders, boolean isApi) {
        this.plugin = plugin;
        this.packets = plugin.getPacketsManager().getPackets();
        this.configsFolder = configsFolder;
        this.isApi = isApi;

        // Gets the displays folder and creates it if it doesn't exist.
        File displaysFolder = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + configsFolder);
        if (!displaysFolder.exists() && createFolders) displaysFolder.mkdirs();

        // If the displays folder is not empty, load the displays.
        if (displaysFolder.exists()) {
            for (File configFile : Objects.requireNonNull(displaysFolder.listFiles())) {
                if (configFile.isDirectory()) continue;
                ConfigManager configManager = new ConfigManager(plugin, configsFolder + File.separator + configFile.getName());

                if (!plugin.getMainConfig().getConfig().isBoolean("updateChecker")) {
                    ConversionManager.setConversionNeeded(true);
                    break;
                }

                loadDisplay(configManager);
            }
        }
    }

    private ConfigManager createConfigManager(String name, DisplayType type, Location location) {
        ConfigManager displayConfigManager = new ConfigManager(plugin, configsFolder + File.separator + name + ".yml");
        YamlConfiguration displayConfig = displayConfigManager.getConfig();

        // Set properties in the display file.
        displayConfig.set("type", type.name());
        displayConfig.set("permission", "none");
        displayConfig.set("hide-permission", "none");
        displayConfig.set("view-distance", 0.0);

        ConfigurationSection locationSection = displayConfig.createSection("location");
        locationSection.set("world", Objects.requireNonNull(location.getWorld()).getName());
        locationSection.set("x", location.getX());
        locationSection.set("y", location.getY());
        locationSection.set("z", location.getZ());

        displayConfig.set("rotationType", org.bukkit.entity.Display.Billboard.CENTER.name());

        ConfigurationSection brightnessSection = displayConfig.createSection("brightness");
        brightnessSection.set("block", 15);
        brightnessSection.set("sky", 15);

        ConfigurationSection shadowSection = displayConfig.createSection("shadow");
        shadowSection.set("radius", 5.0);
        shadowSection.set("strength", 1.0);

        ConfigurationSection transformationSection = displayConfig.createSection("transformation");
        transformationSection.createSection("translation", new ConfigVector3f().serialize());
        transformationSection.createSection("leftRotation", new ConfigAxisAngle4f().serialize());
        transformationSection.createSection("scale", new ConfigVector3f(1.0f, 1.0f, 1.0f).serialize());
        transformationSection.createSection("rightRotation", new ConfigAxisAngle4f().serialize());

        ConfigurationSection rotationSection = displayConfig.createSection("rotation");
        rotationSection.set("yaw", 0.0);
        rotationSection.set("pitch", 0.0);

        ConfigurationSection glowSection = displayConfig.createSection("glow");
        glowSection.set("glowing", false);
        glowSection.set("color", "255;170;0");

        ConfigurationSection hitboxSection = displayConfig.createSection("hitbox");
        hitboxSection.set("override", false);
        hitboxSection.set("width", 1.0f);
        hitboxSection.set("height", 1.0f);
        displayConfig.setComments("hitbox", Arrays.asList("Displays don't have hitboxes of their own, so to have click actions independent entities have to be created.", "These settings allow you to control the hitbox of the display.", "(Use F3 + B to see the hitboxes)"));

        ConfigurationSection actionsSection = displayConfig.createSection("actions");
        ConfigurationSection anySection = actionsSection.createSection("ANY");
        ConfigurationSection actionSetting = anySection.createSection("messagePlayer");
        actionSetting.set("type", ActionType.MESSAGE.getConfigName());
        actionSetting.set("message", "You clicked me, %player_name%!");
        actionSetting.set("delay", 20);
        actionSetting.set("global", false);
        actionSetting.set("global-placeholders", true);
        actionSetting.setInlineComments("delay", List.of("In ticks"));

        return displayConfigManager;
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

    public ADTextDisplay createTextDisplay(Location location, String name, Component value, boolean saveToConfig) {
        if (displays.containsKey(name)) {
            return null;
        }

        TextDisplay newDisplayPacket = packets.createTextDisplay(location);
        ADTextDisplay textDisplay;

        if (saveToConfig) {
            ConfigManager configManager = createConfigManager(name, DisplayType.TEXT, location);
            textDisplay = new ADTextDisplay(plugin, configManager, name, newDisplayPacket, isApi).create(value);
            configManager.save();
        } else {
            textDisplay = new ADTextDisplay(plugin, name, newDisplayPacket).create(value);
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            textDisplay.sendBaseMetadataPackets(onlinePlayer);
        }

        plugin.getInteractionsManager().addInteraction(textDisplay.getInteractionId(), textDisplay);
        displays.put(name, textDisplay);
        return textDisplay;
    }

    public ADItemDisplay createItemDisplay(Location location, String name, Material value, boolean saveToConfig) {
        if (displays.containsKey(name)) {
            return null;
        }

        ItemDisplay newDisplayPacket = packets.createItemDisplay(location);
        ADItemDisplay itemDisplay;

        if (saveToConfig) {
            ConfigManager configManager = createConfigManager(name, DisplayType.ITEM, location);
            itemDisplay = new ADItemDisplay(plugin, configManager, name, newDisplayPacket, isApi).create(value);
            configManager.save();
        } else {
            itemDisplay = new ADItemDisplay(plugin, name, newDisplayPacket).create(value);
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            itemDisplay.sendBaseMetadataPackets(onlinePlayer);
        }

        plugin.getInteractionsManager().addInteraction(itemDisplay.getInteractionId(), itemDisplay);
        displays.put(name, itemDisplay);
        return itemDisplay;
    }

    public ADBlockDisplay createBlockDisplay(Location location, String name, BlockData value, boolean saveToConfig) {
        if (displays.containsKey(name)) {
            return null;
        }

        BlockDisplay newDisplayPacket = packets.createBlockDisplay(location);
        ADBlockDisplay blockDisplay;

        if (saveToConfig) {
            ConfigManager configManager = createConfigManager(name, DisplayType.BLOCK, location);
            blockDisplay = new ADBlockDisplay(plugin, configManager, name, newDisplayPacket, isApi).create(value);
            configManager.save();
        } else {
            blockDisplay = new ADBlockDisplay(plugin, name, newDisplayPacket).create(value);
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            blockDisplay.sendBaseMetadataPackets(onlinePlayer);
        }

        plugin.getInteractionsManager().addInteraction(blockDisplay.getInteractionId(), blockDisplay);
        displays.put(name, blockDisplay);
        return blockDisplay;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean removeDisplay(String name) {
        if (!displays.containsKey(name)) {
            return false;
        }

        ADBaseDisplay display = displays.get(name);
        if (display.getConfigManager() != null) {
            display.getConfigManager().getFile().delete();
        }

        if (display instanceof ADTextDisplay) ((ADTextDisplay) display).stopRunnable();
        display.remove();
        display.stopTicking();
        displays.remove(name);
        plugin.getInteractionsManager().removeInteraction(display.getInteractionId());
        plugin.getInventoryManager().handleRemoval(display);
        return true;
    }

    public void removeAll() {
        for (ADBaseDisplay display : displays.values()) {
            display.remove();
            if (display instanceof ADTextDisplay) {
                ((ADTextDisplay) display).stopRunnable();
            }
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
                newDisplay = new ADBlockDisplay(plugin, configManager, name, newDisplayPacket, isApi);
            }
            case TEXT -> {
                TextDisplay newDisplayPacket = packets.createTextDisplay(location);
                newDisplay = new ADTextDisplay(plugin, configManager, name, newDisplayPacket, isApi);
            }
            case ITEM -> {
                ItemDisplay newDisplayPacket = packets.createItemDisplay(location);
                newDisplay = new ADItemDisplay(plugin, configManager, name, newDisplayPacket, isApi);
            }
        }

        displays.put(configManager.getFile().getName().replace(".yml", ""), newDisplay);
        plugin.getInteractionsManager().addInteraction(newDisplay.getInteractionId(), newDisplay);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            ((DisplayMethods) newDisplay).sendMetadataPackets(onlinePlayer);
        }
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
}