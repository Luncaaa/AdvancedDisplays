package me.lucaaa.advanceddisplays.utils;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.logging.Level;

public class AnimatedTextRunnable {
    private final int displayId;
    private BukkitTask task;

    public AnimatedTextRunnable(int displayId) {
        this.displayId = displayId;
    }

    public void start(List<String> texts) {
        this.task = new BukkitRunnable() {
            private int index = 0;

            @Override
            public void run() {
                Logger.log(Level.WARNING, "Called! Index:" + this.index);
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    AdvancedDisplays.packetsManager.getPackets().setText(displayId, texts.get(index), onlinePlayer);
                }

                if (this.index + 1 == texts.size()) index = 0;
                else this.index++;

            }
        }.runTaskTimer(AdvancedDisplays.getPlugin(), 0L, AdvancedDisplays.mainConfig.getConfig().getInt("text-update"));
    }

    public void stop() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }
}