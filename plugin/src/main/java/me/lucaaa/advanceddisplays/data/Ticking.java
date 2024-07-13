package me.lucaaa.advanceddisplays.data;

import me.lucaaa.advanceddisplays.AdvancedDisplays;

public abstract class Ticking {
    private final AdvancedDisplays plugin;

    public Ticking(AdvancedDisplays plugin) {
        this.plugin = plugin;
    }

    public abstract void tick();

    public void startTicking() {
        this.plugin.getTickManager().addTicking(this);
    }

    public void stopTicking() {
        this.plugin.getTickManager().removeTicking(this);
    }
}