package me.lucaaa.advanceddisplays;

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
import java.util.Objects;
import java.util.logging.Level;

// TODO
// 1. Fix movehere method between worlds.
// 2. Add more text methods (add, remove, set order, per player...)
// 3. Animated item and block displays?

// TODO
// Move displays manager to plugin module - won't be used in the API, as the create() methods will return new TextDisplay(...).create(...)
// Create API methods

// TODO:
// 1. Setup subcommand
// 2. Better developer API.
// 3. More actions on click.

public class AdvancedDisplays extends JavaPlugin {
    // Config files.
    private ConfigManager mainConfig;
    private ConfigManager savesConfig;

    // Managers.
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
        if (displaysManager != null) displaysManager.removeAllEntities(); // If the plugin has been reloaded, remove the displays to prevent duplicate displays.
        if (interactionsManager != null) savedApiDisplays = interactionsManager.getApiDisplays();
        if (packetsManager != null) packetsManager.removeAll(); // If the plugin has been reloaded, remove and add all players again.
        if (inventoryManager != null) inventoryManager.clearAll(); // If the plugin has been reloaded, clear the map.
        packetsManager = new PacketsManager(this, Bukkit.getServer().getBukkitVersion().split("-")[0]);
        interactionsManager = new InteractionsManager(savedApiDisplays);
        displaysManager = new DisplaysManager(this, "displays", true, false);
        messagesManager = new MessagesManager(this.mainConfig);
        inventoryManager = new InventoryManager(this, savesConfig);
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

        // Set up files and managers.
        reloadConfigs();

        // Look for updates.
        new UpdateManager(this).getVersion(v -> {
            String[] spigotVerDivided = v.split("\\.");
            double spigotVerMajor = Double.parseDouble(spigotVerDivided[0] + "." + spigotVerDivided[1]);
            double spigotVerMinor = (spigotVerDivided.length > 2) ? Integer.parseInt(spigotVerDivided[2]) : 0;

            String[] pluginVerDivided = getDescription().getVersion().split("\\.");
            double pluginVerMajor = Double.parseDouble(pluginVerDivided[0] + "." + pluginVerDivided[1]);
            double pluginVerMinor = (pluginVerDivided.length > 2) ? Integer.parseInt(pluginVerDivided[2]) : 0;

            if (spigotVerMajor == pluginVerMajor && spigotVerMinor == pluginVerMinor) {
                Bukkit.getConsoleSender().sendMessage(messagesManager.getColoredMessage("&aThe plugin is up to date! &7(v" + getDescription().getVersion() + ")", true));

            } else if (spigotVerMajor > pluginVerMajor || (spigotVerMajor == pluginVerMajor && spigotVerMinor > pluginVerMinor)) {
                Bukkit.getConsoleSender().sendMessage(messagesManager.getColoredMessage("&6There's a new update available on Spigot! &c" + getDescription().getVersion() + " &7-> &a" + v, true));
                Bukkit.getConsoleSender().sendMessage(messagesManager.getColoredMessage("&6Download it at &7https://www.spigotmc.org/resources/advanceddisplays.110865/", true));

            } else {
                Bukkit.getConsoleSender().sendMessage(messagesManager.getColoredMessage("&6Your plugin version is newer than the Spigot version! &a" + getDescription().getVersion() + " &7-> &c" + v, true));
                Bukkit.getConsoleSender().sendMessage(messagesManager.getColoredMessage("&6There may be bugs and/or untested features!", true));
            }
        });

        // Register events.
        getServer().getPluginManager().registerEvents(new PlayerEventsListener(this), this);
        getServer().getPluginManager().registerEvents(new InternalEntityClickListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryEventsListener(this), this);

        // Registers the main command and adds tab completions.
        MainCommand commandHandler = new MainCommand(this);
        Objects.requireNonNull(this.getCommand("ad")).setExecutor(commandHandler);
        Objects.requireNonNull(this.getCommand("ad")).setTabCompleter(commandHandler);

        Bukkit.getConsoleSender().sendMessage(messagesManager.getColoredMessage("&aThe plugin has been successfully enabled! &7Version: " + this.getDescription().getVersion(), true));
    }

    @Override
    public void onDisable() {
        inventoryManager.clearAll();
    }

    public ConfigManager getMainConfig() {
        return this.mainConfig;
    }

    public ConfigManager getSavesConfig() {
        return this.savesConfig;
    }

    public PacketsManager getPacketsManager() {
        return packetsManager;
    }

    public InteractionsManager getInteractionsManager() {
        return this.interactionsManager;
    }

    public DisplaysManager getDisplaysManager() {
        return this.displaysManager;
    }

    public ADAPIProviderImplementation getApiDisplays() {
        return this.apiDisplays;
    }
    
    public MessagesManager getMessagesManager() {
        return this.messagesManager;
    }

    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }
}