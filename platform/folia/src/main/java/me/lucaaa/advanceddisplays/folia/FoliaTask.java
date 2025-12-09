package me.lucaaa.advanceddisplays.folia;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.lucaaa.advanceddisplays.common.ITask;

public class FoliaTask implements ITask {
    private final ScheduledTask task;

    public FoliaTask(ScheduledTask task) {
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