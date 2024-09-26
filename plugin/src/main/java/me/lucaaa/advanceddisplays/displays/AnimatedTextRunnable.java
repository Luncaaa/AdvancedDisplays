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

import java.util.LinkedHashMap;
import java.util.Map;

public class AnimatedTextRunnable {
    private final AdvancedDisplays plugin;
    private int displayId;

    // Minimessage String rather than component for placeholder parsing (so that %prefix% which is <red>Owner is parsed correctly)
    private Map<String, String> textsList;
    private int animationTime = 0;
    private int refreshTime = 0;

    private String displayedText;
    private BukkitTask animateTask;
    private BukkitTask refreshTask;
    private int nextIndex = 0;

    public AnimatedTextRunnable(AdvancedDisplays plugin, int displayId) {
        this.plugin = plugin;
        this.displayId = displayId;
    }

    public void start(Map<String, Component> texts, int animationTime, int refreshTime) {
        Map<String, String> mmTexts = new LinkedHashMap<>();
        for (Map.Entry<String, Component> entry : texts.entrySet()) {
            mmTexts.put(entry.getKey(), String.join("\n", ComponentSerializer.serialize(entry.getValue())));
        }
        start(mmTexts, animationTime, refreshTime, 0);
    }

    private void start(Map<String, String> texts, int animationTime, int refreshTime, int index) {
        stop();
        nextIndex = index;

        textsList = texts;
        displayedText = textsList.values().stream().toList().get(index);
        this.animationTime = animationTime;
        this.refreshTime = refreshTime;

        // Animated text runnable - displays new text from the list every x seconds.
        if (texts.size() > 1 && animationTime > 0) {
            animateTask = new BukkitRunnable() {
                @Override
                public void run() {
                    displayedText = textsList.values().stream().toList().get(nextIndex);

                    // If higher than 0, the refresh task will handle this
                    if (refreshTime <= 0) {
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            plugin.getPacketsManager().getPackets().setText(displayId, ComponentSerializer.toJSON(ComponentSerializer.deserialize(Utils.getColoredTextWithPlaceholders(onlinePlayer, displayedText))), onlinePlayer);
                        }
                    }

                    nextIndex = (nextIndex + 1 == texts.size()) ? 0 : nextIndex + 1;
                }
            }.runTaskTimerAsynchronously(plugin, 0L, animationTime);

        } else {
            displayedText = textsList.values().stream().toList().get(nextIndex);

            // If higher than 0, the refresh task will handle this
            if (refreshTime <= 0) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    plugin.getPacketsManager().getPackets().setText(displayId, ComponentSerializer.toJSON(ComponentSerializer.deserialize(Utils.getColoredTextWithPlaceholders(onlinePlayer, displayedText))), onlinePlayer);
                }
            }

            nextIndex = (nextIndex + 1 == texts.size()) ? 0 : nextIndex + 1;
        }

        // Refresh text runnable - displays the current text again (to update placeholders) every x seconds.
        if (refreshTime > 0) {
            refreshTask = new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        plugin.getPacketsManager().getPackets().setText(displayId, ComponentSerializer.toJSON(ComponentSerializer.deserialize(Utils.getColoredTextWithPlaceholders(onlinePlayer, displayedText))), onlinePlayer);
                    }
                }
            }.runTaskTimerAsynchronously(plugin, 0L, refreshTime);

        } else {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                plugin.getPacketsManager().getPackets().setText(displayId, ComponentSerializer.toJSON(ComponentSerializer.deserialize(Utils.getColoredTextWithPlaceholders(onlinePlayer, displayedText))), onlinePlayer);
            }
        }
    }

    public void stop() {
        if (animateTask != null) {
            animateTask.cancel();
            animateTask = null;
        }

        if (refreshTask != null) {
            refreshTask.cancel();
            refreshTask = null;
        }

        textsList = null;
    }

    public void updateDisplayId(int newDisplayId) {
        displayId = newDisplayId;
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

        packets.setText(displayId, ComponentSerializer.toJSON(ComponentSerializer.deserialize(Utils.getColoredTextWithPlaceholders(player, displayedText))), player);
    }
}