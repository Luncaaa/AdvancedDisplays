package me.lucaaa.advanceddisplays;

import me.lucaaa.advanceddisplays.commands.MainCommand;
import me.lucaaa.advanceddisplays.commands.subCommands.SubCommandsFormat;
import me.lucaaa.advanceddisplays.events.PlayerEventsListener;
import me.lucaaa.advanceddisplays.managers.*;
import me.lucaaa.advanceddisplays.common.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;

//todo - API implementation
/*
public void setText(String text) {
    this.text = text;
    if (this.config != null) {
        this.settings.set("text", text);
        this.save();
    }

    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        this.setText(text, onlinePlayer);
    }
}

public void setText(String text, Player player) {
        // just send packets
        if (text.size() == 1) {
            this.packets.setText(this.displayId, text.get(0), player);
        } else {
            this.textRunnable.stop();
            this.textRunnable.start(text);
        }
}
 */

//todo
/*
class ADAPI {
    public static dispManager;

    enable() {
        this.dispManager = new DispManager();
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