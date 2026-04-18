package me.lucaaa.advanceddisplays.data;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.actions.Action;
import me.lucaaa.advanceddisplays.displays.ADTextDisplay;
import me.lucaaa.advanceddisplays.displays.AnimatedTextRunnable;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerData {
    private final Player player;
    private final Map<ADTextDisplay, AnimatedTextRunnable> runnables = new HashMap<>();
    private final Map<Action, Long> cooldowns = new HashMap<>();

    public PlayerData(Player player) {
        this.player = player;
    }

    public AnimatedTextRunnable getRunnable(AdvancedDisplays plugin, ADTextDisplay display) {
        AnimatedTextRunnable runnable = (runnables.containsKey(display)) ? runnables.get(display) : new AnimatedTextRunnable(plugin, display, player);
        // No need to call for the "start" method since "setPage" already does that.
        if (!runnables.containsKey(display)) runnables.put(display, runnable);
        return runnable;
    }

    public void stopRunnable(ADTextDisplay display) {
        if (runnables.containsKey(display)) {
            runnables.remove(display).stop();
        }
    }

    /**
     * Resets the player's view of the displays.
     */
    public void stopRunnables() {
        for (AnimatedTextRunnable runnable : runnables.values()) {
            runnable.stop();
        }
        runnables.clear();
    }

    public void setActionUsed(Action action) {
        cooldowns.put(action, System.currentTimeMillis());
    }

    public boolean isCoolingDown(Action action, int cooldown) {
        if (!cooldowns.containsKey(action)) return false;

        return System.currentTimeMillis() - cooldowns.get(action) < cooldown * 50L;
    }
}