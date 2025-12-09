package me.lucaaa.advanceddisplays.spigot;

import me.lucaaa.advanceddisplays.common.ITask;
import org.bukkit.scheduler.BukkitTask;

public class SpigotTask implements ITask {
    private final BukkitTask task;

    public SpigotTask(BukkitTask task) {
        this.task = task;
    }

    @Override
    public void cancel() {
        task.cancel();
    }

    @Override
    public boolean isCancelled() {
        return task.isCancelled();
    }
}