package me.lucaaa.advanceddisplays.spigot;

import me.lucaaa.advanceddisplays.common.ADRunnable;
import me.lucaaa.advanceddisplays.common.ITask;
import me.lucaaa.advanceddisplays.common.TasksManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class SpigotTasksManager implements TasksManager {
    @Override
    public ITask runTask(Plugin plugin, Runnable task) {
        BukkitTask bukkitTask = plugin.getServer().getScheduler().runTask(plugin, task);
        return new SpigotTask(bukkitTask);
    }

    @Override
    public ITask runTaskLater(Plugin plugin, Runnable task, long delay) {
        BukkitTask bukkitTask = plugin.getServer().getScheduler().runTaskLater(plugin, task, delay);
        return new SpigotTask(bukkitTask);
    }

    @Override
    public ITask runTaskTimer(Plugin plugin, Runnable task, long delay, long period) {
        BukkitTask bukkitTask = plugin.getServer().getScheduler().runTaskTimer(plugin, task, delay, period);
        ITask iTask = new SpigotTask(bukkitTask);
        if (task instanceof ADRunnable adRunnable) adRunnable.setTask(iTask);
        return iTask;
    }

    @Override
    public ITask runTaskAsynchronously(Plugin plugin, Runnable task) {
        BukkitTask bukkitTask = plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
        return new SpigotTask(bukkitTask);
    }

    @Override
    public ITask runTaskTimerAsynchronously(Plugin plugin, Runnable task, long delay, long period) {
        BukkitTask bukkitTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period);
        return new SpigotTask(bukkitTask);
    }
}