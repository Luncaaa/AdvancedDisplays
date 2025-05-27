package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.displays.ADBaseEntity;

import java.util.HashMap;

public class InteractionsManager {
    private final HashMap<Integer, ADBaseEntity> displaysMap = new HashMap<>();
    private final HashMap<Integer, ADBaseEntity> apiDisplaysMap;

    public InteractionsManager(HashMap<Integer, ADBaseEntity> apiDisplaysMap) {
        this.apiDisplaysMap = apiDisplaysMap;
    }

    public void addInteraction(int interactionId, ADBaseEntity display) {
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

    public ADBaseEntity getDisplay(int interactionId) {
        if (displaysMap.get(interactionId) != null) {
            return displaysMap.get(interactionId);
        } else {
            return apiDisplaysMap.get(interactionId);
        }
    }

    public HashMap<Integer, ADBaseEntity> getApiDisplays() {
        return apiDisplaysMap;
    }
}
