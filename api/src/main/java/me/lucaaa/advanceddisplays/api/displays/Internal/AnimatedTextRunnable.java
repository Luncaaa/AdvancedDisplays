package me.lucaaa.advanceddisplays.api.displays.Internal;

import me.lucaaa.advanceddisplays.common.managers.PacketsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class AnimatedTextRunnable {
    private final int displayId;
    private List<String> textsList;
    private BukkitTask animateTask;
    private BukkitTask refreshTask;
    private int currentIndex = 0;

    public AnimatedTextRunnable(int displayId) {
        this.displayId = displayId;
    }

    public void start(Plugin plugin, List<String> texts, int animationTime, int refreshTime) {
        this.stop();
        this.textsList = texts;
        // Animated text runnable - displays new text from the list every x seconds.
        if (texts.size() > 1) {
            this.animateTask = new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        PacketsManager.getPackets().setText(displayId, textsList.get(currentIndex), onlinePlayer);
                    }

                    if (currentIndex + 1 == textsList.size()) currentIndex = 0;
                    else currentIndex++;

                }
            }.runTaskTimerAsynchronously(plugin, 0L, animationTime);
        }

        // Refresh text runnable - displays the current text again (to update placeholders) every x seconds.
        if (refreshTime > 0) {
            this.refreshTask = new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        PacketsManager.getPackets().setText(displayId, textsList.get(currentIndex), onlinePlayer);
                    }
                }
            }.runTaskTimerAsynchronously(plugin, 0L, refreshTime);
        } else if (texts.size() == 1) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                PacketsManager.getPackets().setText(displayId, textsList.get(currentIndex), onlinePlayer);
            }
        }
    }

    public void stop() {
        if (this.animateTask != null) {
            this.animateTask.cancel();
            this.animateTask = null;
        }

        if (this.refreshTask != null) {
            this.refreshTask.cancel();
            this.refreshTask = null;
        }

        this.textsList = null;
    }

    public void addText(String text) {
        this.textsList.add(text);
    }

    public boolean isRunning() {
        return !this.animateTask.isCancelled() && !this.refreshTask.isCancelled();
    }
}