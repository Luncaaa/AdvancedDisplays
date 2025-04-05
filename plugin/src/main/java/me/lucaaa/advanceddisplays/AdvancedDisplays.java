package me.lucaaa.advanceddisplays;

import me.lucaaa.advanceddisplays.data.Compatibility;
import me.lucaaa.advanceddisplays.data.Version;
import me.lucaaa.advanceddisplays.integrations.Integration;
import me.lucaaa.advanceddisplays.integrations.ItemsAdderCompat;
import me.lucaaa.advanceddisplays.integrations.OraxenCompat;
import me.lucaaa.advanceddisplays.managers.*;
import me.lucaaa.advanceddisplays.events.*;
import me.lucaaa.advanceddisplays.api.*;
import me.lucaaa.advanceddisplays.displays.ADBaseDisplay;
import me.lucaaa.advanceddisplays.commands.MainCommand;
import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.common.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

// TODO
// 1. Animated item and block displays?
public class AdvancedDisplays extends JavaPlugin implements Logger {
    // Config files.
    private ConfigManager mainConfig;
    private ConfigManager savesConfig;

    // Other.
    private Version nmsVersion;

    // Integrations.
    private final Map<Compatibility, Integration> integrations = new HashMap<>();

    // Managers.
    private TickManager tickManager;
    private PacketsManager packetsManager;
    private InteractionsManager interactionsManager;
    private DisplaysManager displaysManager;
    private MessagesManager messagesManager;
    private InventoryManager inventoryManager;

    // API
    private final ADAPIProviderImplementation apiDisplays = new ADAPIProviderImplementation(this);

    // Reload the config files.
    public void reloadConfigs() {
        // Config files
        mainConfig = new ConfigManager(this, "config.yml", true);
        savesConfig = new ConfigManager(this, "saved-inventories.yml", true);

        // Managers
        HashMap<Integer, ADBaseDisplay> savedApiDisplays = new HashMap<>(); // If the plugin is reloaded, this will save the click actions for API displays.
        if (displaysManager != null) displaysManager.removeAll(true); // If the plugin has been reloaded, remove the displays to prevent duplicate displays.
        if (interactionsManager != null) savedApiDisplays = interactionsManager.getApiDisplays();
        if (packetsManager != null) packetsManager.removeAll(); // If the plugin has been reloaded, remove and add all players again.
        if (inventoryManager != null) inventoryManager.clearAll(); // If the plugin has been reloaded, clear the map.
        if (tickManager != null) tickManager.stop();
        tickManager = new TickManager(this);
        packetsManager = new PacketsManager(this);
        interactionsManager = new InteractionsManager(savedApiDisplays);
        displaysManager = new DisplaysManager(this, "displays", true);
        messagesManager = new MessagesManager(mainConfig);
        inventoryManager = new InventoryManager(this, mainConfig, savesConfig);
    }

    @Override
    public void onEnable() {
        String version = getServer().getBukkitVersion().split("-")[0];
        nmsVersion = Version.getNMSVersion(version);
        if (nmsVersion == Version.UNKNOWN) {
            log(Level.SEVERE, "Unknown NMS version! Version: " + version);
            log(Level.SEVERE, "The plugin may not be updated to support the server's version.");
            log(Level.SEVERE, "If you're using a version lower than 1.19.4, upgrade to 1.19.4 or higher to use this plugin.");
            log(Level.SEVERE, "The plugin will be disabled...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        ADAPIProvider.setImplementation(apiDisplays);

        // Set up integrations.
        if (Bukkit.getPluginManager().isPluginEnabled("Oraxen")) {
            integrations.put(Compatibility.ORAXEN, new OraxenCompat(this));
        }

        if (Bukkit.getPluginManager().isPluginEnabled("ItemsAdder")) {
            integrations.put(Compatibility.ITEMS_ADDER, new ItemsAdderCompat(this));
        }

        // Set up files and managers.
        reloadConfigs();

        // Look for updates.
        if (mainConfig.getConfig().getBoolean("updateChecker", true)) {
            new UpdateManager(this).getVersion(v -> UpdateManager.sendStatus(this, v, getDescription().getVersion()));
        }

        // Register events.
        getServer().getPluginManager().registerEvents(new PlayerEventsListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryEventsListener(this), this);

        // Registers the main command and adds tab completions.
        Objects.requireNonNull(getCommand("ad")).setExecutor(new MainCommand(this));

        Bukkit.getConsoleSender().sendMessage(messagesManager.getColoredMessage("&aThe plugin has been successfully enabled! &7Version: " + getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        if (inventoryManager != null) inventoryManager.clearAll();
        if (tickManager != null) tickManager.stop();
    }

    public ConfigManager getMainConfig() {
        return mainConfig;
    }

    public ConfigManager getSavesConfig() {
        return savesConfig;
    }

    public Version getNmsVersion() {
        return nmsVersion;
    }

    public boolean isIntegrationLoaded(Compatibility compatibility) {
        return integrations.containsKey(compatibility);
    }

    public Integration getIntegration(Compatibility compatibility) {
        return integrations.get(compatibility);
    }

    public PacketsManager getPacketsManager() {
        return packetsManager;
    }

    public InteractionsManager getInteractionsManager() {
        return interactionsManager;
    }

    public DisplaysManager getDisplaysManager() {
        return displaysManager;
    }

    public ADAPIProviderImplementation getApiDisplays() {
        return apiDisplays;
    }
    
    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public TickManager getTickManager() {
        return tickManager;
    }

    @Override
    public void log(Level level, String message) {
        getLogger().log(level, message);
    }

    @Override
    public void logError(Level level, String message, Throwable error) {
        getLogger().log(level, message, error);
    }
}