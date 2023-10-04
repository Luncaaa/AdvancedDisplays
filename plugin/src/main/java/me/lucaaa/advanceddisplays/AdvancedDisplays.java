package me.lucaaa.advanceddisplays;

import me.lucaaa.advanceddisplays.api.DisplaysManager;
import me.lucaaa.advanceddisplays.commands.MainCommand;
import me.lucaaa.advanceddisplays.commands.subCommands.SubCommandsFormat;
import me.lucaaa.advanceddisplays.common.managers.ConfigManager;
import me.lucaaa.advanceddisplays.common.managers.PacketsManager;
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

//todo
/*
class ADAPI {
    public static displaysManager;

    enable() {
        this.displaysManager = new DisplaysManager();
    }
}

class AdvancedDisplaysAPI {
    static Display createText() {
        return ADAPI.dispManager.create(...)
    }
}

this class {
    onEnable() {
        ADAPI.enable();
    }
}
 */

// TODO:
// 1. Setup subcommand
// 2. Developer API.

public class AdvancedDisplays extends JavaPlugin {
    // An instance of the plugin.
    private static Plugin plugin;

    // Subcommands for the HelpSubCommand class.
    public final static HashMap<String, SubCommandsFormat> subCommands = MainCommand.subCommands;

    // Config file.
    public static ConfigManager mainConfig;

    // Managers.
    public static DisplaysManager displaysManager;

    // Reload the config files.
    public static void reloadConfigs() {
        // Creates the config file.
        if (!new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "config.yml").exists())
            plugin.saveResource("config.yml", false);

        mainConfig = new ConfigManager(plugin, "config.yml");

        // Managers
        PacketsManager.setPackets(Bukkit.getServer().getClass().getName().split("\\.")[3]);
        if (displaysManager != null) displaysManager.removeAllEntities(); // If the plugin has been reloaded, remove the displays to prevent duplicate displays.
        displaysManager = new DisplaysManager(plugin, "displays");
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