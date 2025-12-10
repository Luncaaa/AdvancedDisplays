package me.lucaaa.advanceddisplays;

import me.lucaaa.advanceddisplays.data.*;
import me.lucaaa.advanceddisplays.displays.ADBaseEntity;
import me.lucaaa.advanceddisplays.integrations.Integration;
import me.lucaaa.advanceddisplays.integrations.ItemsAdderCompat;
import me.lucaaa.advanceddisplays.integrations.OraxenCompat;
import me.lucaaa.advanceddisplays.managers.*;
import me.lucaaa.advanceddisplays.events.*;
import me.lucaaa.advanceddisplays.api.*;
import me.lucaaa.advanceddisplays.commands.MainCommand;
import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.nms_common.Logger;
import me.lucaaa.advanceddisplays.nms_common.Metadata;
import me.lucaaa.advanceddisplays.nms_common.Version;
import me.lucaaa.advanceddisplays.common.TasksManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

// TODO (not in order)
// 1. Animated item and block displays
// 2. Multi-line displays (possibility to combine text, item, block and entity in one display)
// 3. Better actions system and in-game editor
// 4. Finish entity displays (metadata system)
public class AdvancedDisplays extends JavaPlugin implements Logger {
    // Config files.
    private ConfigManager mainConfig;
    private ConfigManager savesConfig;

    // Other.
    private Version nmsVersion;
    private boolean isRunning = false;
    public Metadata metadata;
    private BukkitAudiences audiences;

    // Integrations.
    private final Map<Compatibility, Integration> integrations = new HashMap<>();

    // Managers.
    private VersionManager versionManager;
    private TickManager tickManager;
    private HeadCacheManager headCacheManager;
    private InteractionsManager interactionsManager;
    private DisplaysManager displaysManager;
    private MessagesManager messagesManager;
    private PlayersManager playersManager;
    private InventoryManager inventoryManager;

    // API
    private final ADAPIProviderImplementation apiDisplays = new ADAPIProviderImplementation(this);

    // Reload the config files.
    public void reloadConfigs() {
        // Config files
        mainConfig = new ConfigManager(this, "config.yml", true);
        savesConfig = new ConfigManager(this, "saved-inventories.yml", true);

        // Managers
        HashMap<Integer, ADBaseEntity> savedApiDisplays = new HashMap<>(); // If the plugin is reloaded, this will save the click actions for API displays.
        if (isRunning) {
            headCacheManager.shutdown();
            displaysManager.removeAll(true); // If the plugin has been reloaded, remove the displays to prevent duplicate displays.
            savedApiDisplays = interactionsManager.getApiDisplays();
            versionManager.getPacketsManager().removeAll(); // If the plugin has been reloaded, remove and add all players again.
            playersManager.removeAll();
            inventoryManager.clearAll(); // If the plugin has been reloaded, clear the map.
            tickManager.stop();
        }

        versionManager = new VersionManager(this);
        tickManager = new TickManager(this);
        headCacheManager = new HeadCacheManager(this);
        interactionsManager = new InteractionsManager(savedApiDisplays);
        displaysManager = new DisplaysManager(this, getName(), true, false);
        messagesManager = new MessagesManager(this, mainConfig);
        playersManager = new PlayersManager(this);
        inventoryManager = new InventoryManager(this, mainConfig);
    }

    @Override
    public void onEnable() {
        String version = getServer().getBukkitVersion().split("-")[0];
        nmsVersion = Version.getNMSVersion(version);
        if (nmsVersion == Version.UNKNOWN) {
            log(Level.SEVERE, "----------[AdvancedDisplays initialization error]----------");
            log(Level.SEVERE, "Unknown NMS version! Server version: " + version);
            log(Level.SEVERE, "The plugin may not be updated to support the server's version.");
            log(Level.SEVERE, "Check dev builds latest versions support: https://github.com/Luncaaa/AdvancedDisplays/releases/tag/dev");
            log(Level.SEVERE, "If you're using a version lower than 1.19.4, upgrade to 1.19.4 or higher to use this plugin.");
            log(Level.SEVERE, "The plugin will be disabled...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        metadata = new Metadata(nmsVersion);
        audiences = BukkitAudiences.create(this);

        ADAPIProvider.setImplementation(apiDisplays);

        // Set up integrations.
        if (getServer().getPluginManager().isPluginEnabled("Oraxen")) {
            integrations.put(Compatibility.ORAXEN, new OraxenCompat(this));
        }

        if (getServer().getPluginManager().isPluginEnabled("ItemsAdder")) {
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

        isRunning = true;
        getServer().getConsoleSender().sendMessage(messagesManager.getColoredMessage("&aThe plugin has been successfully enabled! &7Version: " + getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        if (inventoryManager != null) inventoryManager.clearAll();
        if (playersManager != null) playersManager.removeAll();
        if (headCacheManager != null) headCacheManager.shutdown();
        if (tickManager != null) tickManager.stop();
        if (audiences != null) audiences.close();
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

    public Audience getAudience(Player player) {
        return audiences.player(player);
    }

    public boolean isIntegrationLoaded(Compatibility compatibility) {
        return integrations.containsKey(compatibility);
    }

    public Integration getIntegration(Compatibility compatibility) {
        return integrations.get(compatibility);
    }

    public VersionManager.PacketsManager getPacketsManager() {
        return versionManager.getPacketsManager();
    }

    public TasksManager getTasksManager() {
        return versionManager.getTasksManager();
    }

    public TickManager getTickManager() {
        return tickManager;
    }

    public HeadCacheManager getHeadCacheManager() {
        return headCacheManager;
    }

    public InteractionsManager getInteractionsManager() {
        return interactionsManager;
    }

    public ADAPIProviderImplementation getApiDisplays() {
        return apiDisplays;
    }
    
    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public PlayersManager getPlayersManager() {
        return playersManager;
    }

    public DisplaysManager getDisplaysManager() {
        return displaysManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
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