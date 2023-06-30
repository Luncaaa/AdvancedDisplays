package me.lucaaa.advanceddisplays.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private final File file;
    private final YamlConfiguration config;

    public ConfigManager(Plugin plugin, String path) {
        this.file = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + path);
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public void save() throws IOException {
        this.config.save(this.file);
    }

    public File getFile() {
        return this.file;
    }

    public YamlConfiguration getConfig() {
        return this.config;
    }
}