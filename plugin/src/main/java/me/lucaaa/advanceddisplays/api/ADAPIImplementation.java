package me.lucaaa.advanceddisplays.api;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.api.displays.BlockDisplay;
import me.lucaaa.advanceddisplays.api.displays.ItemDisplay;
import me.lucaaa.advanceddisplays.api.displays.TextDisplay;
import me.lucaaa.advanceddisplays.common.utils.Logger;
import me.lucaaa.advanceddisplays.displays.ADBaseDisplay;
import me.lucaaa.advanceddisplays.managers.DisplaysManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

public class ADAPIImplementation implements ADAPI {

    private final DisplaysManager displaysManager;

    public ADAPIImplementation(String pluginName) {
        this.displaysManager = new DisplaysManager("displays" + File.separator + pluginName, false, true);
    }

    @Override
    public BlockDisplay createBlockDisplay(String name, Location location, BlockData value) {
        BlockDisplay display = this.displaysManager.createBlockDisplay(location, name, value, false);
        if (display == null) Logger.log(Level.WARNING, "The display \"" + name + "\" could not be created because another display with the same name already exists.");
        else AdvancedDisplays.apiDisplays.addDisplay(display);
        return display;
    }

    @Override
    public ItemDisplay createItemDisplay(String name, Location location, Material value) {
        ItemDisplay display = this.displaysManager.createItemDisplay(location, name, value, false);
        if (display == null) Logger.log(Level.WARNING, "The display \"" + name + "\" could not be created because another display with the same name already exists.");
        else AdvancedDisplays.apiDisplays.addDisplay(display);
        return display;
    }

    @Override
    public TextDisplay createTextDisplay(String name, Location location, List<String> value) {
        TextDisplay display = this.displaysManager.createTextDisplay(location, name, value, false);
        if (display == null) Logger.log(Level.WARNING, "The display \"" + name + "\" could not be created because another display with the same name already exists.");
        else AdvancedDisplays.apiDisplays.addDisplay(display);
        return display;
    }

    @Override
    public BaseDisplay getDisplay(String name) {
        return this.displaysManager.getDisplayFromMap(name);
    }

    @Override
    public void removeDisplay(String name) {
        ADBaseDisplay display = this.displaysManager.getDisplayFromMap(name);

        if (display != null) {
            AdvancedDisplays.interactionsManager.removeInteraction(display.getInteractionId());
            AdvancedDisplays.apiDisplays.removeDisplay(display);
            this.displaysManager.removeDisplay(name);
        }
    }
}
