package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.displays.ADBaseDisplay;

import java.util.HashMap;

public class InteractionsManager {
    private final HashMap<Integer, ADBaseDisplay> displaysMap = new HashMap<>();

    public void addInteraction(int interactionId, ADBaseDisplay display) {
        this.displaysMap.put(interactionId, display);
    }

    public void removeInteraction(int interactionId) {
        this.displaysMap.remove(interactionId);
    }

    public ADBaseDisplay getDisplay(int interactionId) {
        return this.displaysMap.get(interactionId);
    }
}
