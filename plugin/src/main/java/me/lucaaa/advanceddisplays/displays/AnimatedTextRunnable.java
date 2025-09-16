package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.nms_common.PacketInterface;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AnimatedTextRunnable {
    private final AdvancedDisplays plugin;
    private final PacketInterface packets;
    private final ADTextDisplay display;

    // Minimessage String rather than component for placeholder parsing (so that, for example, %prefix% which is <red>Owner is parsed correctly)
    private List<String> textsList;
    private int animationTime = 0;
    private int refreshTime = 0;

    // Using a consumer is better than checking whether the runnable is for an individual player
    // or all online players every iteration...
    private final Consumer<String> updateDisplay;
    private final List<Player> excludedPlayers = new ArrayList<>();
    private BukkitTask displayTask;
    private int index = 0;

    public AnimatedTextRunnable(AdvancedDisplays plugin, ADTextDisplay display) {
        this.plugin = plugin;
        this.packets = plugin.getPacketsManager().getPackets();
        this.display = display;
        this.updateDisplay = (text) -> {
            for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                if (excludedPlayers.contains(onlinePlayer)) continue;

                packets.setMetadata(display.getEntityId(), onlinePlayer, plugin.metadata.TEXT, plugin.getMessagesManager().parseColorsAndPlaceholders(onlinePlayer, text));
            }
        };
    }

    // Use for per-player pages.
    public AnimatedTextRunnable(AdvancedDisplays plugin, ADTextDisplay display, Player player) {
        this.plugin = plugin;
        this.packets = plugin.getPacketsManager().getPackets();
        this.display = display;
        this.updateDisplay = (text) ->
                packets.setMetadata(display.getEntityId(), player, plugin.metadata.TEXT, plugin.getMessagesManager().parseColorsAndPlaceholders(player, text));
        // Initial values
        this.textsList = new ArrayList<>(display.getText().values());
        this.index = display.getTextRunnable().getCurrentIndex();
    }

    public void start() {
        start(0);
    }

    private void start(int startIndex) {
        stop();

        this.textsList = new ArrayList<>(display.getText().values());
        this.animationTime = display.getAnimationTime();
        this.refreshTime = display.getRefreshTime();
        this.index = startIndex;

        if (textsList.isEmpty()) {
            return;
        }

        updateDisplay.accept(textsList.get(index));

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
                        AnimatedTextRunnable.this.index = getNextIndex();
                    }
                    updateDisplay.accept(textsList.get(index));
                }
            }
            // TODO: Fix async (thread lock on Utils#getColoredTextWithPlaceholders -> PlaceholderAPI#setPlaceholders)
        }.runTaskTimerAsynchronously(plugin, 0L, 0L);
    }

    // Method to stop the task
    public void stop() {
        if (displayTask != null && !displayTask.isCancelled()) {
            displayTask.cancel();
            displayTask = null;
        }

        textsList = null;
    }

    public int nextPage() {
        int nextIndex = getNextIndex();
        start(nextIndex);
        return nextIndex;
    }

    public int previousPage() {
        int previousIndex = getPreviousIndex();
        start(previousIndex);
        return previousIndex;
    }

    public void setPage(int index) {
        start(index);
    }

    // Send the currently displayed text to players who just joined until the task refreshes/animates it.
    // If this wasn't run, the player wouldn't see any text until the task refreshed/animated it.
    public void sendToPlayer(Player player) {
        packets.setMetadata(display.getEntityId(), player, plugin.metadata.TEXT, plugin.getMessagesManager().parseColorsAndPlaceholders(player, textsList.get(index)));
    }

    public int getCurrentIndex() {
        return index;
    }

    public int getNextIndex() {
        return (index + 1) % textsList.size();
    }

    public int getPreviousIndex() {
        // index - 1 -> Previously displayed text
        if (textsList.size() <= 1) {
            return 0;
        } else if (index == 0) {
            return textsList.size() - 1;
        } else {
            return index - 1;
        }
    }

    public void excludePlayer(Player player) {
        if (!excludedPlayers.contains(player)) {
            excludedPlayers.add(player);
        }
    }

    public void resetPlayer(Player player) {
        if (excludedPlayers.contains(player)) {
            excludedPlayers.remove(player);
            packets.setMetadata(display.getEntityId(), player, plugin.metadata.TEXT, plugin.getMessagesManager().parseColorsAndPlaceholders(player, textsList.get(index)));
        }
    }
}