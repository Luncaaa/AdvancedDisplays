package me.lucaaa.advanceddisplays.api;

import me.lucaaa.advanceddisplays.api.displays.BaseDisplay;

import java.util.ArrayList;
import java.util.List;

public class APIDisplays {
    private final List<BaseDisplay> displays;

    public APIDisplays() {
        this.displays = new ArrayList<>();
    }

    public void addDisplay(BaseDisplay display) {
        this.displays.add(display);
    }

    public void removeDisplay(BaseDisplay display) {
        this.displays.remove(display);
    }

    public List<BaseDisplay> getDisplays() {
        return this.displays;
    }
}
