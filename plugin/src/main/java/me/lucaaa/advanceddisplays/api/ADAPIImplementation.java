package me.lucaaa.advanceddisplays.api;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.*;
import me.lucaaa.advanceddisplays.displays.ADBaseEntity;
import me.lucaaa.advanceddisplays.managers.DisplaysManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Level;

public class ADAPIImplementation implements ADAPI {
    private final AdvancedDisplays plugin;
    private final JavaPlugin apiPlugin;
    private final DisplaysManager displaysManager;

    public ADAPIImplementation(AdvancedDisplays plugin, JavaPlugin apiPlugin) {
        this.plugin = plugin;
        this.apiPlugin = apiPlugin;
        this.displaysManager = new DisplaysManager(plugin, apiPlugin.getName(), false, true);
    }

    @Override
    public BlockDisplay createBlockDisplay(String name, Location location, BlockData value, boolean saveToConfig) {
        BlockDisplay display = displaysManager.createBlockDisplay(location, name, value, saveToConfig);
        if (display == null) logWarning(name);
        return display;
    }

    @Override
    public ItemDisplay createItemDisplay(String name, Location location, Material value, boolean saveToConfig) {
        ItemDisplay display = displaysManager.createItemDisplay(location, name, value, saveToConfig);
        if (display == null) logWarning(name);
        return display;
    }

    @Override
    public TextDisplay createTextDisplay(String name, Location location, List<String> value, boolean saveToConfig) {
        TextDisplay display = displaysManager.createTextDisplay(location, name, String.join("\n", value), saveToConfig);
        if (display == null) logWarning(name);
        return display;
    }

    @Override
    public TextDisplay createTextDisplay(String name, Location location, Component value, boolean saveToConfig) {
        TextDisplay display = displaysManager.createTextDisplay(location, name, value, saveToConfig);
        if (display == null) logWarning(name);
        return display;
    }

    @Override
    public BaseEntity getDisplay(String name) {
        return displaysManager.getDisplayFromMap(name);
    }

    @Override
    public BaseEntity getDisplayFromLoc(Location location, double radius, boolean closest) {
        return displaysManager.getDisplayFromLoc(location, radius, closest);
    }

    @Override
    public void removeDisplay(String name) {
        ADBaseEntity display = displaysManager.getDisplayFromMap(name);

        if (display != null) {
            displaysManager.removeDisplay(display, true, true);
        }
    }

    @Override
    public void removeAll() {
        displaysManager.removeAll(false);
    }

    private void logWarning(String name) {
        plugin.log(Level.WARNING, "=".repeat(25));
        plugin.log(Level.SEVERE, "An error ocurred while processing displays for plugin \"" + apiPlugin.getName() + "\".");
        plugin.log(Level.WARNING, "The display \"" + name + "\" could not be created because another display with the same name already exists.");
        plugin.log(Level.WARNING, "=".repeat(25));
    }

    public DisplaysManager getDisplaysManager() {
        return displaysManager;
    }
}
