package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class ConfigManager {
    private final AdvancedDisplays plugin;
    private final File file;
    private final YamlConfiguration config;

    public ConfigManager(AdvancedDisplays plugin, String path, boolean createIfNotExists) {
        this.plugin = plugin;
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

    public YamlConfiguration getConfig() {
        return config;
    }

    public File getFile() {
        return file;
    }

    public void set(String path, Object value) {
        config.set(path, value);
    }

    public ConfigurationSection getSection(String name) {
        return getSection(name, true, config);
    }

    public ConfigurationSection getSection(String name, ConfigurationSection parent) {
        return getSection(name, true, parent);
    }

    public ConfigurationSection getSection(String name, boolean createIfNotExists, ConfigurationSection parent) {
        ConfigurationSection section = parent.getConfigurationSection(name);
        if (section == null && createIfNotExists) {
            section = parent.createSection(name);
            save();
            plugin.log(Level.WARNING, "Missing section \"" + name + "\" in \"" + file.getName() + "\" file! Created an empty section.");
        }

        return section;

    }

    public <T> T getOrDefault(String setting, T def, ConfigurationSection section) {
        if (!section.contains(setting)) {
            plugin.log(Level.WARNING, "Missing setting \"" + setting + "\" in \"" + file.getName() + "\" file! Setting to default value: " + def);
            section.set(setting, def);
            save();
            return def;
        }

        Object data = section.get(setting);
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) def.getClass();

        if (!clazz.isInstance(data)) {
            plugin.log(Level.WARNING, "Setting \"" + setting + "\" is not a \"" + clazz.getSimpleName() + "\" value in \"" + file.getName() + "\" file! Setting to default value: " + def);
            // Config value won't be set in case the user just forgot the quotes (so he doesn't lose data).
            // config.set(setting, def);
            return def;
        }

        return clazz.cast(data);
    }
}