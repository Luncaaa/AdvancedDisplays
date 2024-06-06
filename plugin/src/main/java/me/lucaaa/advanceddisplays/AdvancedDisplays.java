package me.lucaaa.advanceddisplays;

import me.lucaaa.advanceddisplays.api.ADAPIProvider;
import me.lucaaa.advanceddisplays.api.ADAPIProviderImplementation;
import me.lucaaa.advanceddisplays.api.APIDisplays;
import me.lucaaa.advanceddisplays.displays.ADBaseDisplay;
import me.lucaaa.advanceddisplays.events.InternalEntityClickListener;
import me.lucaaa.advanceddisplays.managers.*;
import me.lucaaa.advanceddisplays.commands.MainCommand;
import me.lucaaa.advanceddisplays.common.managers.ConfigManager;
import me.lucaaa.advanceddisplays.events.PlayerEventsListener;
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
    // Config file.
    private ConfigManager mainConfig;

    // Managers.
    private PacketsManager packetsManager;
    private InteractionsManager interactionsManager;
    private DisplaysManager displaysManager;
    private APIDisplays apiDisplays;
    private MessagesManager messagesManager;

    // Reload the config files.
    public void reloadConfigs() {
        // Creates the config file.
        if (!new File(getDataFolder().getAbsolutePath() + File.separator + "config.yml").exists())
            saveResource("config.yml", false);

        mainConfig = new ConfigManager(this, "config.yml");

        // Managers
        HashMap<Integer, ADBaseDisplay> savedApiDisplays = new HashMap<>(); // If the plugin is reloaded, this will save the click actions for API displays.
        if (displaysManager != null) displaysManager.removeAllEntities(); // If the plugin has been reloaded, remove the displays to prevent duplicate displays.
        if (interactionsManager != null) savedApiDisplays = interactionsManager.getApiDisplays();
        if (packetsManager != null) packetsManager.removeAll(); // If the plugin has been reloaded, remove and add all players again.
        packetsManager = new PacketsManager(this, Bukkit.getServer().getBukkitVersion().split("-")[0]);
        interactionsManager = new InteractionsManager(savedApiDisplays);
        displaysManager = new DisplaysManager(this, "displays", true, false);
        messagesManager = new MessagesManager(this.mainConfig);
    }

    @Override
    public void onEnable() {
        String version = getServer().getBukkitVersion().split("-")[0];
        int majorNumber = Integer.parseInt(version.split("\\.")[1]);
        int minorNumber = Integer.parseInt(version.split("\\.")[2]);
        if (majorNumber < 19 || (majorNumber == 19 && minorNumber < 4)) {
            Logger.log(Level.SEVERE, "The plugin will not work on this version as displays were not added until 1.19.4");
            Logger.log(Level.SEVERE, "Please update your server to 1.19.4 or higher to use this plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        ADAPIProvider.setImplementation(new ADAPIProviderImplementation(this));
        apiDisplays = new APIDisplays();

        // Set up files and managers.
        reloadConfigs();

        // Register events.
        getServer().getPluginManager().registerEvents(new PlayerEventsListener(this), this);
        getServer().getPluginManager().registerEvents(new InternalEntityClickListener(this), this);

        // Registers the main command and adds tab completions.
        MainCommand commandHandler = new MainCommand(this);
        Objects.requireNonNull(this.getCommand("ad")).setExecutor(commandHandler);
        Objects.requireNonNull(this.getCommand("ad")).setTabCompleter(commandHandler);

        Bukkit.getConsoleSender().sendMessage(messagesManager.getColoredMessage("&aThe plugin has been successfully enabled! &7Version: " + this.getDescription().getVersion(), true));
    }

    public ConfigManager getMainConfig() {
        return this.mainConfig;
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

    public APIDisplays getApiDisplays() {
        return this.apiDisplays;
    }
    
    public MessagesManager getMessagesManager() {
        return this.messagesManager;
    }
}