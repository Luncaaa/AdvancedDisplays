package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.data.Ticking;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class TickManager {
    private final AdvancedDisplays plugin;
    private final List<Ticking> ticking = new ArrayList<>();
    private final List<Ticking> toAdd = new ArrayList<>();
    private final List<Ticking> toRemove = new ArrayList<>();
    private BukkitTask task;
    private boolean isTicking = false;

    public TickManager(AdvancedDisplays plugin) {
        this.plugin = plugin;
        this.start();
    }

    private synchronized void start() {
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimerAsynchronously(this.plugin, 0L, 1L);
    }

    public synchronized void stop() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
        this.ticking.clear();
        this.toAdd.clear();
        this.toRemove.clear();
    }

    public void addTicking(Ticking ticked) {
        synchronized (this.toAdd) {
            this.toAdd.add(ticked);
        }
    }

    public void removeTicking(Ticking ticked) {
        synchronized (this.toRemove) {
            this.toRemove.add(ticked);
        }
    }

    private void tick() {
        if (this.isTicking) return;

        this.isTicking = true;

        synchronized (this.ticking) {
            for (Ticking ticked : this.ticking) {
                ticked.tick();
            }
        }

        synchronized (this.toAdd) {
            this.ticking.addAll(this.toAdd);
            this.toAdd.clear();
        }

        synchronized (this.toRemove) {
            this.ticking.removeAll(this.toRemove);
            this.toRemove.clear();
        }

        this.isTicking = false;
    }
}