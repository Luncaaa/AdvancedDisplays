package me.lucaaa.advanceddisplays.folia;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.lucaaa.advanceddisplays.common.ADRunnable;
import me.lucaaa.advanceddisplays.common.ITask;
import me.lucaaa.advanceddisplays.common.TasksManager;
import org.bukkit.plugin.Plugin;

public class FoliaTasksManager implements TasksManager {
    @Override
    public ITask runTask(Plugin plugin, Runnable task) {
        ScheduledTask foliaTask = plugin.getServer().getGlobalRegionScheduler().run(plugin, t -> task.run());
        return new FoliaTask(foliaTask);
    }

    @Override
    public ITask runTaskLater(Plugin plugin, Runnable task, long delay) {
        ScheduledTask foliaTask = plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, t -> task.run(), delay);
        return new FoliaTask(foliaTask);
    }

    @Override
    public ITask runTaskTimer(Plugin plugin, Runnable task, long delay, long period) {
        ScheduledTask foliaTask = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, t -> task.run(), delay, period);
        ITask itask = new FoliaTask(foliaTask);
        if (task instanceof ADRunnable adRunnable) adRunnable.setTask(itask);
        return itask;
    }

    @Override
    public ITask runTaskAsynchronously(Plugin plugin, Runnable task) {
        ScheduledTask foliaTask = plugin.getServer().getGlobalRegionScheduler().run(plugin, t -> task.run());
        return new FoliaTask(foliaTask);
    }

    @Override
    public ITask runTaskTimerAsynchronously(Plugin plugin, Runnable task, long delay, long period) {
        long parsedDelay = (delay == 0) ? 1L : delay;
        ScheduledTask foliaTask = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, t -> task.run(), parsedDelay, period);
        return new FoliaTask(foliaTask);
    }
}