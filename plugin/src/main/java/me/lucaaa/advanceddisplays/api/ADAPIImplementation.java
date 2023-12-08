package me.lucaaa.advanceddisplays.api;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.common.utils.Logger;
import me.lucaaa.advanceddisplays.displays.ADBaseDisplay;

import java.util.HashMap;
import java.util.logging.Level;

public class ADAPIImplementation implements ADAPI {

    private final AdvancedDisplays ad;
    private final HashMap<String, ADBaseDisplay> holograms = new HashMap<>();

    public ADAPIImplementation(AdvancedDisplays ad) {
        this.ad = ad;
    }

    @Override
    public String testMethod() {
        Logger.log(Level.WARNING, "VIVA!");
        return "Works";
    }
}
