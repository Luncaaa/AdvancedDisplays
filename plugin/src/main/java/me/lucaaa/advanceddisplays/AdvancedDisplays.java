package me.lucaaa.advanceddisplays;

import me.lucaaa.advanceddisplays.api.ADAPIProvider;
import me.lucaaa.advanceddisplays.api.ADAPIProviderImplementation;
import me.lucaaa.advanceddisplays.api.APIDisplays;
import me.lucaaa.advanceddisplays.displays.ADBaseDisplay;
import me.lucaaa.advanceddisplays.events.InternalEntityClickListener;
import me.lucaaa.advanceddisplays.managers.DisplaysManager;
import me.lucaaa.advanceddisplays.commands.MainCommand;
import me.lucaaa.advanceddisplays.commands.subCommands.SubCommandsFormat;
import me.lucaaa.advanceddisplays.common.managers.ConfigManager;
import me.lucaaa.advanceddisplays.managers.InteractionsManager;
import me.lucaaa.advanceddisplays.managers.MessagesManager;
import me.lucaaa.advanceddisplays.managers.PacketsManager;
import me.lucaaa.advanceddisplays.events.PlayerEventsListener;
import me.lucaaa.advanceddisplays.common.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
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
// 2. Better eveloper API.
// 3. More actions on click.

public class AdvancedDisplays extends JavaPlugin {
    // An instance of the plugin.
    private static Plugin plugin;

    // Subcommands for the HelpSubCommand class.
    public final static HashMap<String, SubCommandsFormat> subCommands = MainCommand.subCommands;

    // Config file.
    public static ConfigManager mainConfig;

    // Managers.
    public static PacketsManager packetsManager;
    public static InteractionsManager interactionsManager;
    public static DisplaysManager displaysManager;
    public static APIDisplays apiDisplays;

    // Reload the config files.
    public static void reloadConfigs() {
        // Creates the config file.
        if (!new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "config.yml").exists())
            plugin.saveResource("config.yml", false);

        mainConfig = new ConfigManager(plugin, "config.yml");

        // Managers
        HashMap<Integer, ADBaseDisplay> savedApiDisplays = new HashMap<>(); // If the plugin is reloaded, this will save the click actions for API displays.
        if (displaysManager != null) displaysManager.removeAllEntities(); // If the plugin has been reloaded, remove the displays to prevent duplicate displays.
        if (interactionsManager != null) savedApiDisplays = interactionsManager.getApiDisplays();
        if (packetsManager != null) packetsManager.removeAll(); // If the plugin has been reloaded, remove and add all players again.
        packetsManager = new PacketsManager(Bukkit.getServer().getBukkitVersion().split("-")[0]);
        interactionsManager = new InteractionsManager(savedApiDisplays);
        displaysManager = new DisplaysManager("displays", true, false);
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

        plugin = this;
        ADAPIProvider.setImplementation(new ADAPIProviderImplementation());
        apiDisplays = new APIDisplays();

        // Set up files and managers.
        reloadConfigs();

        // Register events.
        getServer().getPluginManager().registerEvents(new PlayerEventsListener(), this);
        getServer().getPluginManager().registerEvents(new InternalEntityClickListener(), this);

        // Registers the main command and adds tab completions.
        MainCommand commandHandler = new MainCommand();
        Objects.requireNonNull(this.getCommand("ad")).setExecutor(commandHandler);
        Objects.requireNonNull(this.getCommand("ad")).setTabCompleter(commandHandler);

        Bukkit.getConsoleSender().sendMessage( MessagesManager.getColoredMessage("&aThe plugin has been successfully enabled!", true));
    }

    public static Plugin getPlugin() {
        return plugin;
    }
}