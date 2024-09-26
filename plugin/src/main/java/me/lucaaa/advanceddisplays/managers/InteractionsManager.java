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
            apiDisplaysMap.put(interactionId, display);
        } else {
            displaysMap.put(interactionId, display);
        }
    }

    public void removeInteraction(int interactionId) {
        displaysMap.remove(interactionId);
        apiDisplaysMap.remove(interactionId);
    }

    public ADBaseDisplay getDisplay(int interactionId) {
        if (displaysMap.get(interactionId) != null) {
            return displaysMap.get(interactionId);
        } else {
            return apiDisplaysMap.get(interactionId);
        }
    }

    public HashMap<Integer, ADBaseDisplay> getApiDisplays() {
        return apiDisplaysMap;
    }
}
