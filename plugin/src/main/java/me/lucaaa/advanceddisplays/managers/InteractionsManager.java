package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.displays.ADBaseDisplay;

import java.util.HashMap;

public class InteractionsManager {
    private final HashMap<Integer, ADBaseDisplay> displaysMap = new HashMap<>();
    private final HashMap<Integer, ADBaseDisplay> apiDisplaysMap;

    public InteractionsManager(HashMap<Integer, ADBaseDisplay> apiDisplaysMap) {
        this.apiDisplaysMap = apiDisplaysMap;
    }

    public void addInteraction(int interactionId, ADBaseDisplay display) {
        if (display.isApi()) {
            this.apiDisplaysMap.put(interactionId, display);
        } else {
            this.displaysMap.put(interactionId, display);
        }
    }

    public void removeInteraction(int interactionId) {
        this.displaysMap.remove(interactionId);
        this.apiDisplaysMap.remove(interactionId);
    }

    public ADBaseDisplay getDisplay(int interactionId) {
        if (this.displaysMap.get(interactionId) != null) {
            return this.displaysMap.get(interactionId);
        } else {
            return this.apiDisplaysMap.get(interactionId);
        }
    }

    public HashMap<Integer, ADBaseDisplay> getApiDisplays() {
        return this.apiDisplaysMap;
    }
}
