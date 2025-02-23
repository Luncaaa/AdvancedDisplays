package me.lucaaa.advanceddisplays.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private final File file;
    private final YamlConfiguration config;

    public ConfigManager(Plugin plugin, String path, boolean createIfNotExists) {
        this.file = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + path);

        if (!file.exists() && createIfNotExists) {
            plugin.saveResource(path, false);
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File getFile() {
        return file;
    }

    public YamlConfiguration getConfig() {
        return config;
    }
}