package me.lucaaa.advanceddisplays.data;

import me.lucaaa.advanceddisplays.AdvancedDisplays;

public abstract class Ticking {
    private final AdvancedDisplays plugin;

    public Ticking(AdvancedDisplays plugin) {
        this.plugin = plugin;
    }

    public abstract void tick();

    public void startTicking() {
        plugin.getTickManager().addTicking(this);
    }

    public void stopTicking() {
        plugin.getTickManager().removeTicking(this);
    }
}