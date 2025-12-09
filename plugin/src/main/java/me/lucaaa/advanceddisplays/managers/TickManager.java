package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.data.Ticking;
import me.lucaaa.advanceddisplays.common.ITask;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class TickManager {
    private final AdvancedDisplays plugin;
    private final List<Ticking> ticking = new CopyOnWriteArrayList<>();
    private ITask task;
    private final AtomicBoolean isTicking = new AtomicBoolean(false);
    private final AtomicBoolean isCancelled = new AtomicBoolean(false);

    public TickManager(AdvancedDisplays plugin) {
        this.plugin = plugin;
        start();
    }

    private void start() {
        task = plugin.getTasksManager().runTaskTimerAsynchronously(plugin, this::tick, 0L, 1L);
    }

    public void stop() {
        isCancelled.set(true);

        if (task != null) {
            task.cancel();
            task = null;
        }

        ticking.clear();
    }

    public void addTicking(Ticking ticked) {
        ticking.add(ticked);
    }

    public void removeTicking(Ticking ticked) {
        ticking.remove(ticked);
    }

    private void tick() {
        if (isCancelled.get() || !isTicking.compareAndSet(false, true)) return;

        for (Ticking ticked : ticking) {
            try {
                ticked.tick();
            } catch (Exception e) {
                ticking.remove(ticked);
                plugin.logError(Level.SEVERE, "An error occurred while ticking a display: ", e);
            }
        }

        isTicking.set(false);
    }
}