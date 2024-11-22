package me.lucaaa.advanceddisplays;

import io.th0rgal.oraxen.compatibilities.CompatibilitiesManager;
import me.lucaaa.advanceddisplays.data.Compatibility;
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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

// TODO
// 1. Add more text methods (add, remove, set order, per player...)
// 2. Animated item and block displays?
public class AdvancedDisplays extends JavaPlugin {
    // Config files.
    private ConfigManager mainConfig;
    private ConfigManager savesConfig;

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
        // Creates the config file.
        if (!new File(getDataFolder().getAbsolutePath() + File.separator + "config.yml").exists())
            saveResource("config.yml", false);
        if (!new File(getDataFolder().getAbsolutePath() + File.separator + "saved-inventories.yml").exists())
            saveResource("saved-inventories.yml", false);

        mainConfig = new ConfigManager(this, "config.yml");
        savesConfig = new ConfigManager(this, "saved-inventories.yml");

        // Managers
        HashMap<Integer, ADBaseDisplay> savedApiDisplays = new HashMap<>(); // If the plugin is reloaded, this will save the click actions for API displays.
        if (displaysManager != null) displaysManager.removeAll(); // If the plugin has been reloaded, remove the displays to prevent duplicate displays.
        if (interactionsManager != null) savedApiDisplays = interactionsManager.getApiDisplays();
        if (packetsManager != null) packetsManager.removeAll(); // If the plugin has been reloaded, remove and add all players again.
        if (inventoryManager != null) inventoryManager.clearAll(); // If the plugin has been reloaded, clear the map.
        if (tickManager != null) tickManager.stop();
        tickManager = new TickManager(this);
        packetsManager = new PacketsManager(this, Bukkit.getServer().getBukkitVersion().split("-")[0]);
        interactionsManager = new InteractionsManager(savedApiDisplays);
        displaysManager = new DisplaysManager(this, "displays", true, false);
        messagesManager = new MessagesManager(mainConfig);
        inventoryManager = new InventoryManager(this, mainConfig, savesConfig);
    }

    @Override
    public void onEnable() {
        String version = getServer().getBukkitVersion().split("-")[0];
        String[] parts = version.split("\\.");
        int majorNumber = Integer.parseInt(parts[1]);
        int minorNumber = (parts.length == 2) ? 0 : Integer.parseInt(parts[2]);
        if (majorNumber < 19 || (majorNumber == 19 && minorNumber < 4)) {
            Logger.log(Level.SEVERE, "The plugin will not work on this version as displays were not added until 1.19.4");
            Logger.log(Level.SEVERE, "Please update your server to 1.19.4 or higher to use this plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        ADAPIProvider.setImplementation(apiDisplays);

        // Set up integrations.
        if (Bukkit.getPluginManager().isPluginEnabled("Oraxen")) {
            CompatibilitiesManager.addCompatibility("AdvancedDisplays", OraxenCompat.class);
            integrations.put(Compatibility.ORAXEN, new OraxenCompat());
        }

        if (Bukkit.getPluginManager().isPluginEnabled("ItemsAdder")) {
            integrations.put(Compatibility.ITEMS_ADDER, new ItemsAdderCompat());
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

        Bukkit.getConsoleSender().sendMessage(messagesManager.getColoredMessage("&aThe plugin has been successfully enabled! &7Version: " + getDescription().getVersion(), true));
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
}