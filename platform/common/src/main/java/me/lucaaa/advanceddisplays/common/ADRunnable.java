package me.lucaaa.advanceddisplays.common;

public abstract class ADRunnable implements Runnable {
    private ITask task;

    public void setTask(ITask task) {
        this.task = task;
    }

    public void cancel() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}