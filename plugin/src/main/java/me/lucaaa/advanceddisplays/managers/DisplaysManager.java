package me.lucaaa.advanceddisplays.managers;

import com.google.common.io.Files;
import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.BaseEntity;
import me.lucaaa.advanceddisplays.data.AttachedDisplay;
import me.lucaaa.advanceddisplays.displays.*;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
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
import java.util.logging.Level;

public class DisplaysManager {
    private final AdvancedDisplays plugin;
    private final String pluginName;
    private final String configsFolder;
    private final boolean isApi;
    private final Map<String, ADBaseEntity> displays = new HashMap<>();
    private final Map<Player, AttachedDisplay> attachDisplays = new HashMap<>();

    private int failedLoads = 0;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public DisplaysManager(AdvancedDisplays plugin, String pluginName, boolean createFolders, boolean isApi) {
        this.plugin = plugin;
        this.pluginName = pluginName;
        this.configsFolder = (isApi) ? "displays" + File.separator + pluginName : "displays";
        this.isApi = isApi;

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

        String message = "Loaded " + displays.size() + " display(s)";
        if (isApi) {
            message += " for plugin \"" + pluginName + "\"";
        }
        plugin.log(Level.INFO, message);
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

        ADTextDisplay newDisplay = (ADTextDisplay) createDisplay(DisplayType.TEXT, location, display.name(), display.content(), display.saveToConfig());

        if (newDisplay != null) {
            newDisplay.setBillboard(Display.Billboard.FIXED);
            newDisplay.setSeeThrough(false);
            newDisplay.setRotation(yaw, pitch);
        }

        return newDisplay;
    }

    public ADBaseEntity createDisplay(DisplayType type, Location location, String name, Object value, boolean saveToConfig) {
        if (displays.containsKey(name)) {
            return null;
        }

        ADBaseEntity display = switch (type) {
            case BLOCK -> new ADBlockDisplay(plugin, this, name, location, saveToConfig).create((BlockData) value);
            case ENTITY -> new ADEntityDisplay(plugin, this, name, location, (EntityType) value, saveToConfig).create();
            case ITEM -> new ADItemDisplay(plugin, this, name, location, saveToConfig).create((Material) value);
            case TEXT -> {
                ADTextDisplay textDisplay = new ADTextDisplay(plugin, this, name, location, saveToConfig);
                if (value instanceof Component) {
                    yield textDisplay.create((Component) value);
                } else {
                    yield textDisplay.create((String) value);
                }
            }
        };

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            display.sendMetadataPackets(onlinePlayer);
        }

        plugin.getInteractionsManager().addInteraction(display.getInteractionId(), display);
        displays.put(name, display);
        return display;
    }

    public boolean removeDisplay(String name) {
        if (!displays.containsKey(name)) {
            return false;
        }

        removeDisplay(displays.get(name), true, true);
        return true;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void removeDisplay(ADBaseEntity display, boolean deleteFile, boolean removeFromList) {
        if (display.getConfigManager() != null && deleteFile) {
            display.getConfigManager().getFile().delete();
        }

        if (display instanceof ADTextDisplay) ((ADTextDisplay) display).stopRunnable();
        display.destroy();
        display.stopTicking();
        plugin.getInventoryManager().handleRemoval(display);
        if (removeFromList) displays.remove(display.getName());
        display.setRemoved();
    }

    public void removeAll(boolean onReload) {
        for (ADBaseEntity display : displays.values()) {
            removeDisplay(display, !onReload, false); // false to prevent ConcurrentModificationException
        }

        attachDisplays.clear();
    }

    public ADBaseEntity getDisplayFromMap(String name) {
        return displays.get(name);
    }

    public void spawnDisplays(Player player) {
        for (ADBaseEntity display : displays.values()) {
            if (display.getLocation().getWorld() != player.getLocation().getWorld()) continue;
            if (display.getVisibilityManager().isVisibleByPlayer(player)) display.spawnToPlayer(player);
        }
    }

    public void loadDisplay(ConfigManager configManager) {
        String name = Files.getNameWithoutExtension(configManager.getFile().getName());
        if (!hasValidLocation(name, configManager)) {
            failedLoads++;
            return;
        }

        String configDisplayType = configManager.getConfig().getString("type");
        DisplayType displayType;
        if (configDisplayType == null) {
            plugin.log(Level.WARNING, getMessage(name, "does not have a display type set!"));
            failedLoads++;
            return;
        }

        try {
            displayType = DisplayType.valueOf(configDisplayType);
        } catch (IllegalArgumentException e) {
            plugin.log(Level.WARNING, getMessage(name, "has an invalid display type set: " + configDisplayType));
            failedLoads++;
            return;
        }

        ADBaseEntity newDisplay = switch (displayType) {
            case BLOCK -> new ADBlockDisplay(plugin, this, configManager, name);
            case TEXT -> new ADTextDisplay(plugin, this, configManager, name);
            case ITEM -> new ADItemDisplay(plugin, this, configManager, name);
            case ENTITY -> {
                String configType = configManager.getSection("entity").getString("type");
                if (configType == null) {
                    plugin.log(Level.WARNING, getMessage(name, "does not have an entity type set!"));
                    yield null;
                }

                try {
                    yield new ADEntityDisplay(plugin, this, configManager, name, EntityType.valueOf(configType));
                } catch (IllegalArgumentException e) {
                    plugin.log(Level.WARNING, getMessage(name, "has an invalid entity type set: " + configType));
                    yield null;
                }
            }
        };

        if (newDisplay == null) {
            failedLoads++;
            return;
        }

        displays.put(configManager.getFile().getName().replace(".yml", ""), newDisplay);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            newDisplay.sendMetadataPackets(onlinePlayer);
        }
        plugin.getInteractionsManager().addInteraction(newDisplay.getInteractionId(), newDisplay);
    }

    public BaseEntity getDisplayFromLoc(Location location, double radius, boolean closest) {
        double closestDistance = Math.pow(radius, 2);
        BaseEntity closestDisplay = null;

        for (BaseEntity display : displays.values()) {
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

    public Map<String, ADBaseEntity> getDisplays() {
        return displays;
    }

    public boolean existsDisplay(String name) {
        return displays.containsKey(name);
    }

    public String getConfigsFolder() {
        return configsFolder;
    }

    public boolean isApi() {
        return isApi;
    }

    private boolean hasValidLocation(String name, ConfigManager configManager) {
        List<String> errors = new ArrayList<>();

        YamlConfiguration config = configManager.getConfig();
        ConfigurationSection locationSection = config.getConfigurationSection("location");
        if (locationSection == null) {
            errors.add("Missing \"location\" section. An empty one has been created for you.");
            locationSection = config.createSection("location");
            configManager.save();
        }

        String world = locationSection.getString("world");
        if (world == null) {
            errors.add("Missing \"world\" field.");
        } else if (plugin.getServer().getWorld(world) == null) {
            errors.add("Invalid \"world\" field - World not found: " + world);
        }

        for (String field : List.of("x", "y", "z")) {
            if (!locationSection.isDouble(field)) {
                errors.add("Missing \"" + field + "\" field or invalid type - must be a double.");
            }
        }

        if (!errors.isEmpty()) {
            plugin.log(Level.WARNING, "=".repeat(25));
            plugin.log(Level.SEVERE, getMessage(name, "has an invalid location:"));
            errors.forEach(error -> plugin.log(Level.WARNING, error));
            plugin.log(Level.WARNING, "=".repeat(25));
        }

        return errors.isEmpty();
    }

    private String getMessage(String name, String message) {
        return "The display \"" + name + "\" for plugin \"" + pluginName + "\" " + message;
    }

    public int getFailedLoads() {
        return failedLoads;
    }
}