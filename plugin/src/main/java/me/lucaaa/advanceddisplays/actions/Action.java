package me.lucaaa.advanceddisplays.actions;

import org.bukkit.entity.Player;

public abstract class Action {
    private final int delay;

    public Action(int delay) {
        this.delay = delay;
    }

    public abstract void runAction(Player player);

    public int getDelay() {
        return this.delay;
    }
}
