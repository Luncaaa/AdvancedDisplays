package me.lucaaa.advanceddisplays;

import me.lucaaa.advanceddisplays.commands.MainCommand;
import me.lucaaa.advanceddisplays.commands.subCommands.SubCommandsFormat;
import me.lucaaa.advanceddisplays.events.PlayerEventsListener;
import me.lucaaa.advanceddisplays.managers.*;
import me.lucaaa.advanceddisplays.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;

// TODO:
// 1. Setup subcommand
// 2. Text displays.
//   2.1 - Animated texts.
// 3. Developer API.

public class AdvancedDisplays extends JavaPlugin {
    // An instance of the plugin.
    private static Plugin plugin;

    // Subcommands for the HelpSubCommand class.
    public static HashMap<String, SubCommandsFormat> subCommands = MainCommand.subCommands;

    // Config file.
    public static ConfigManager mainConfig;

    // Managers.
    public static PacketsManager packetsManager;
    public static DisplaysManager displaysManager;

    // Conversion
    public static boolean needsConversion = false;

    // Reload the config files.
    public static void reloadConfigs() {
        // Creates the config file.
        if (!new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "config.yml").exists())
            plugin.saveResource("config.yml", false);

        mainConfig = new ConfigManager(plugin, "config.yml");

        // Managers
        packetsManager = new PacketsManager(Bukkit.getServer().getClass().getName().split("\\.")[3]);
        if (displaysManager != null) displaysManager.removeAllEntities(); // If the plugin has been reloaded, remove the displays to prevent duplicate displays.
        displaysManager = new DisplaysManager(plugin);
    }

    @Override
    public void onEnable() {
        plugin = this;

        // Set up files and managers.
        reloadConfigs();

        // Register events.
        getServer().getPluginManager().registerEvents(new PlayerEventsListener(), this);

        // Registers the main command and adds tab completions.
        Objects.requireNonNull(this.getCommand("ad")).setExecutor(new MainCommand());
        Objects.requireNonNull(this.getCommand("ad")).setTabCompleter(new MainCommand());

        Logger.log(Level.INFO, "The plugin has been enabled.");
    }

    public static Plugin getPlugin() {
        return plugin;
    }
}