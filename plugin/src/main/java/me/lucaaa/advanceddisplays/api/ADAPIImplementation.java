package me.lucaaa.advanceddisplays.api;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.common.utils.Logger;
import me.lucaaa.advanceddisplays.managers.DisplaysManager;

import java.io.File;
import java.util.logging.Level;

public class ADAPIImplementation implements ADAPI {

    private final AdvancedDisplays ad;
    private final DisplaysManager displaysManager;

    public ADAPIImplementation(AdvancedDisplays ad, String pluginName) {
        this.ad = ad;
        this.displaysManager = new DisplaysManager("displays" + File.separator + pluginName, false);
    }

    @Override
    public String testMethod() {
        Logger.log(Level.WARNING, "VIVA!");
        return "Works";
    }
}
