package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.util.ComponentSerializer;
import me.lucaaa.advanceddisplays.data.Utils;
import me.lucaaa.advanceddisplays.nms_common.PacketInterface;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnimatedTextRunnable {
    private final AdvancedDisplays plugin;
    private final PacketInterface packets;
    private int displayId;

    // Minimessage String rather than component for placeholder parsing (so that, for example, %prefix% which is <red>Owner is parsed correctly)
    private List<String> textsList;
    private int animationTime = 0;
    private int refreshTime = 0;

    private BukkitTask displayTask;
    private int index = 0;

    public AnimatedTextRunnable(AdvancedDisplays plugin, int displayId) {
        this.plugin = plugin;
        this.packets = plugin.getPacketsManager().getPackets();
        this.displayId = displayId;
    }

    public void start(Map<String, String> texts, int animationTime, int refreshTime) {
        start(new ArrayList<>(texts.values()), animationTime, refreshTime, 0);
    }

    private void start(List<String> texts, int animationTime, int refreshTime, int startIndex) {
        stop();

        this.textsList = texts;
        this.animationTime = animationTime;
        this.refreshTime = refreshTime;
        this.index = startIndex;

        if (textsList.isEmpty()) {
            return;
        }

        updateDisplay(textsList.get(index));

        if (animationTime <= 0 && refreshTime <= 0) {
            return;
        }

        displayTask = new BukkitRunnable() {
            private int animationTicks = 0;
            private int refreshTicks = 0;

            @Override
            public void run() {
                boolean shouldUpdate = false;
                boolean nextPage = false;

                // Handle animation (page switching)
                if (animationTime > 0 && textsList.size() > 1) {
                    animationTicks++;
                    if (animationTicks >= animationTime) {
                        animationTicks = 0;
                        shouldUpdate = true;
                        nextPage = true;
                    }
                }

                // Handle refresh (placeholder updates)
                if (refreshTime > 0) {
                    refreshTicks++;
                    if (refreshTicks >= refreshTime) {
                        refreshTicks = 0;
                        shouldUpdate = true;
                    }
                }

                // Update if needed
                if (shouldUpdate) {
                    if (nextPage) {
                        AnimatedTextRunnable.this.index = (index + 1) % textsList.size();
                    }
                    updateDisplay(textsList.get(index));
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 1L);
    }

    private void updateDisplay(String text) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            packets.setMetadata(displayId, player, plugin.metadata.VALUE,
                    ComponentSerializer.deserialize(Utils.getColoredTextWithPlaceholders(player, text)));
        }
    }

    // Method to stop the task
    public void stop() {
        if (displayTask != null && !displayTask.isCancelled()) {
            displayTask.cancel();
            displayTask = null;
        }

        textsList = null;
    }

    public void updateDisplayId(int newDisplayId) {
        displayId = newDisplayId;
    }

    public void nextPage() {
        start(textsList, animationTime, refreshTime, (index + 1) % textsList.size());
    }

    public void previousPage() {
        // index - 1 -> Previously displayed text
        int previousIndex;
        if (textsList.size() <= 1) {
            previousIndex = 0;
        } else if (index == 0) {
            previousIndex = textsList.size() - 1;
        } else {
            previousIndex = index - 1;
        }

        start(textsList, animationTime, refreshTime, previousIndex);
    }

    public void setPage(int index) {
        start(textsList, animationTime, refreshTime, index);
    }

    // Send the currently displayed text to players who just joined until the task refreshes/animates it.
    // If this wasn't run, the player wouldn't see any text until the task refreshed/animated it.
    public void sendToPlayer(Player player, PacketInterface packets) {
        packets.setMetadata(displayId, player, plugin.metadata.VALUE, ComponentSerializer.deserialize(Utils.getColoredTextWithPlaceholders(player, textsList.get(index))));
    }
}