package me.lucaaa.advanceddisplays.common;

import org.bukkit.plugin.Plugin;

@SuppressWarnings("UnusedReturnValue")
public interface TasksManager {
    ITask runTask(Plugin plugin, Runnable task);
    ITask runTaskLater(Plugin plugin, Runnable task, long delay);
    ITask runTaskTimer(Plugin plugin, Runnable task, long delay, long period);
    ITask runTaskAsynchronously(Plugin plugin, Runnable task);
    ITask runTaskTimerAsynchronously(Plugin plugin, Runnable task, long delay, long period);
}