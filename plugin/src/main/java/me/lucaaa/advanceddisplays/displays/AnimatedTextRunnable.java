package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.util.ComponentSerializer;
import me.lucaaa.advanceddisplays.common.utils.Utils;
import me.lucaaa.advanceddisplays.nms_common.PacketInterface;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;

public class AnimatedTextRunnable {
    private final AdvancedDisplays plugin;
    private int displayId;

    private Map<String, Component> textsList;
    private int animationTime;
    private int refreshTime;

    private Component displayedText;
    private BukkitTask animateTask;
    private BukkitTask refreshTask;
    private int nextIndex = 0;

    public AnimatedTextRunnable(AdvancedDisplays plugin, int displayId) {
        this.plugin = plugin;
        this.displayId = displayId;
    }

    public void start(Map<String, Component> texts, int animationTime, int refreshTime) {
        start(texts, animationTime, refreshTime, 0);
    }

    public void start(Map<String, Component> texts, int animationTime, int refreshTime, int index) {
        stop();
        nextIndex = index;

        this.textsList = texts;
        this.animationTime = animationTime;
        this.refreshTime = refreshTime;

        // Animated text runnable - displays new text from the list every x seconds.
        if (texts.size() > 1 && animationTime > 0) {
            this.animateTask = new BukkitRunnable() {
                @Override
                public void run() {
                    displayedText = textsList.values().stream().toList().get(nextIndex);

                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        plugin.getPacketsManager().getPackets().setText(displayId, Utils.getColoredTextWithPlaceholders(onlinePlayer, ComponentSerializer.toJSON(displayedText)), onlinePlayer);
                    }

                    nextIndex = (nextIndex + 1 == texts.size()) ? 0 : nextIndex + 1;
                }
            }.runTaskTimerAsynchronously(plugin, 0L, animationTime);

        } else {
            displayedText = textsList.values().stream().toList().get(nextIndex);

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                plugin.getPacketsManager().getPackets().setText(displayId, Utils.getColoredTextWithPlaceholders(onlinePlayer, ComponentSerializer.toJSON(displayedText)), onlinePlayer);
            }

            nextIndex = (nextIndex + 1 == texts.size()) ? 0 : nextIndex + 1;
        }

        // Refresh text runnable - displays the current text again (to update placeholders) every x seconds.
        if (refreshTime > 0) {
            this.refreshTask = new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        plugin.getPacketsManager().getPackets().setText(displayId, Utils.getColoredTextWithPlaceholders(onlinePlayer, ComponentSerializer.toJSON(displayedText)), onlinePlayer);
                    }
                }
            }.runTaskTimerAsynchronously(plugin, 0L, refreshTime);

        } else if (texts.size() == 1) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                plugin.getPacketsManager().getPackets().setText(displayId, Utils.getColoredTextWithPlaceholders(onlinePlayer, ComponentSerializer.toJSON(displayedText)), onlinePlayer);
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

    public void updateDisplayId(int newDisplayId) {
        this.displayId = newDisplayId;
    }

    public void nextPage() {
        start(textsList, animationTime, refreshTime, nextIndex);
    }

    public void previousPage() {
        // nextIndex -> Next from currently displayed text
        // nextIndex - 1 -> Currently displayed text
        // nextIndex - 2 -> Previously displayed text
        int previousIndex;
        if (nextIndex == 0 && textsList.size() > 1) {
            previousIndex = textsList.size() - 2;
        } else if (nextIndex - 2 < 0) {
            previousIndex = textsList.size() - 1;
        } else {
            previousIndex = nextIndex - 2;
        }

        start(textsList, animationTime, refreshTime, previousIndex);
    }

    public void setPage(String page) {
        start(textsList, animationTime, refreshTime, textsList.keySet().stream().toList().indexOf(page));
    }

    public void sendToPlayer(Player player, PacketInterface packets) {
        // Will happen when plugin is reloaded. In that case, when the runnable is started, it will update the text
        // for every online player.
        if (displayedText == null) return;

        packets.setText(this.displayId, Utils.getColoredTextWithPlaceholders(player, ComponentSerializer.toJSON(displayedText)), player);
    }
}