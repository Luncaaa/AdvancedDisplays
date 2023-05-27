package me.lucaaa.advanceddisplays;

import me.lucaaa.advanceddisplays.commands.MainCommand;
import me.lucaaa.advanceddisplays.commands.subCommands.SubCommandsFormat;
import me.lucaaa.advanceddisplays.managers.*;
import me.lucaaa.advanceddisplays.utils.ConfigVector3f;
import me.lucaaa.advanceddisplays.utils.EntitiesLoadListener;
import me.lucaaa.advanceddisplays.utils.Logger;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;

// TODO:
//d1. Fix remove subcommand.
// 2. Setup subcommand.
//d3. Add and fix properties.
//d  3.1 Display location in config.
//d  3.2 Add rotation
// 4. Placeholders in text displays.
//   4.1 Per-player placeholders.
// 5. Developer API.
//d6. Figure out transformation.

public class AdvancedDisplays extends JavaPlugin {
    // An instance of the plugin.
    private static Plugin plugin;

    // Subcommands for the HelpSubCommand class.
    public static HashMap<String, SubCommandsFormat> subCommands = MainCommand.subCommands;

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
        displaysManager = new DisplaysManager(plugin);
    }

    @Override
    public void onEnable() {
        plugin = this;

        // Set up files and managers.
        reloadConfigs();

        // Register events.
        getServer().getPluginManager().registerEvents(new EntitiesLoadListener(this), this);

        // Registers the main command and adds tab completions.
        Objects.requireNonNull(this.getCommand("ad")).setExecutor(new MainCommand());
        Objects.requireNonNull(this.getCommand("ad")).setTabCompleter(new MainCommand());

        ConfigurationSerialization.registerClass(ConfigVector3f.class);

        Logger.log(Level.INFO, "The plugin has been enabled.");
    }

    @Override
    public void onDisable() {
        // TODO
        Logger.log(Level.INFO, "The plugin has been disabled.");
    }
}