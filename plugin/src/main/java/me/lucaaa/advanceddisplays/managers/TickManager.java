package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.data.Ticking;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class TickManager {
    private final AdvancedDisplays plugin;
    private final List<Ticking> ticking = new CopyOnWriteArrayList<>();
    private BukkitTask task;
    private final AtomicBoolean isTicking = new AtomicBoolean(false);
    private final AtomicBoolean isCancelled = new AtomicBoolean(false);

    public TickManager(AdvancedDisplays plugin) {
        this.plugin = plugin;
        start();
    }

    private void start() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 1L);
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

        try {
            for (Ticking ticked : ticking) {
                ticked.tick();
            }
        } catch (Exception e) {
            plugin.logError(Level.SEVERE, "An error occurred while ticking a display: ", e);

        } finally {
            isTicking.set(false);
        }
    }
}