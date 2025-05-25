package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.displays.ADEntityDisplay;

import java.util.HashMap;

public class InteractionsManager {
    private final HashMap<Integer, ADEntityDisplay> displaysMap = new HashMap<>();
    private final HashMap<Integer, ADEntityDisplay> apiDisplaysMap;

    public InteractionsManager(HashMap<Integer, ADEntityDisplay> apiDisplaysMap) {
        this.apiDisplaysMap = apiDisplaysMap;
    }

    public void addInteraction(int interactionId, ADEntityDisplay display) {
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

    public ADEntityDisplay getDisplay(int interactionId) {
        if (displaysMap.get(interactionId) != null) {
            return displaysMap.get(interactionId);
        } else {
            return apiDisplaysMap.get(interactionId);
        }
    }

    public HashMap<Integer, ADEntityDisplay> getApiDisplays() {
        return apiDisplaysMap;
    }
}
