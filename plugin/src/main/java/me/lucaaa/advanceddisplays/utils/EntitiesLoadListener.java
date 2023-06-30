package me.lucaaa.advanceddisplays.utils;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.managers.ConfigManager;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class EntitiesLoadListener implements Listener {
    private final HashMap<UUID, ConfigManager> configs = new HashMap<>();

    public EntitiesLoadListener(Plugin plugin) {
        File displaysFolder = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "displays");
        if (!displaysFolder.exists()) return;

        for (File file : Objects.requireNonNull(displaysFolder.listFiles())) {
            ConfigManager config = new ConfigManager(plugin, "displays" + File.separator + file.getName());
            this.configs.put(UUID.fromString(Objects.requireNonNull(config.getConfig().getString("id"))), config);
        }
    }

    @EventHandler
    public void onEntitiesLoad(EntitiesLoadEvent event)  {
        for (Entity entity : event.getEntities()) {
            if (!(entity instanceof Display)) continue;

            if (!this.configs.containsKey(entity.getUniqueId())) return;

            AdvancedDisplays.displaysManager.loadEntity(this.configs.get(entity.getUniqueId()));
        }
    }
}
