package me.lucaaa.advanceddisplays.api;

import me.lucaaa.advanceddisplays.api.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.api.displays.BlockDisplay;
import me.lucaaa.advanceddisplays.api.displays.ItemDisplay;
import me.lucaaa.advanceddisplays.api.displays.TextDisplay;
import me.lucaaa.advanceddisplays.managers.DisplaysManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.io.File;
import java.util.List;

public class ADAPIImplementation implements ADAPI {

    private final DisplaysManager displaysManager;

    public ADAPIImplementation(String pluginName) {
        this.displaysManager = new DisplaysManager("displays" + File.separator + pluginName, false);
    }

    @Override
    public BlockDisplay createBlockDisplay(String name, Location location, BlockData value) {
        return this.displaysManager.createBlockDisplay(location, name, value, false);
    }

    @Override
    public ItemDisplay createItemDisplay(String name, Location location, Material value) {
        return this.displaysManager.createItemDisplay(location, name, value, false);
    }

    @Override
    public TextDisplay createTextDisplay(String name, Location location, List<String> value) {
        return this.displaysManager.createTextDisplay(location, name, value, false);
    }

    @Override
    public BaseDisplay getDisplay(String name) {
        return this.displaysManager.getDisplayFromMap(name);
    }

    @Override
    public void removeDisplay(String name) {
        this.displaysManager.removeDisplay(name);
    }
}
