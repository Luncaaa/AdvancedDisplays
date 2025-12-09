package me.lucaaa.advanceddisplays.managers;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.nms_common.Logger;
import me.lucaaa.advanceddisplays.nms_common.PacketException;
import me.lucaaa.advanceddisplays.nms_common.PacketInterface;
import me.lucaaa.advanceddisplays.common.TasksManager;
import me.lucaaa.advanceddisplays.folia.FoliaTasksManager;
import me.lucaaa.advanceddisplays.spigot.SpigotTasksManager;
import org.bukkit.entity.Player;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.logging.Level;

public class VersionManager {
    private final PacketsManager packetsManager;
    private final TasksManager tasksManager;
    private static final String TAIL_CONTEXT_IDENTIFIER = "DefaultChannelPipeline$TailContext#0";

    public VersionManager(AdvancedDisplays plugin) {
        packetsManager = new PacketsManager(plugin);

        if (isFolia()) {
            plugin.log(Level.INFO, "Using the Folia tasks manager.");
            tasksManager = new FoliaTasksManager();
        } else {
            plugin.log(Level.INFO, "Using the Paper/Spigot tasks manager.");
            tasksManager = new SpigotTasksManager();
        }
    }

    public static class PacketsManager {
        private final AdvancedDisplays plugin;
        private final PacketInterface packets;

        public PacketsManager(AdvancedDisplays plugin) {
            this.plugin = plugin;

            try {
                Class<?> nmsClass = Class.forName("me.lucaaa.advanceddisplays." + plugin.getNmsVersion().name() + ".Packets");
                Object nmsClassInstance = nmsClass.getConstructor(Logger.class).newInstance(plugin);
                this.packets = (PacketInterface) nmsClassInstance;
                addAll();
                plugin.log(Level.INFO, "Using the PacketsManager for NMS version " + plugin.getNmsVersion().name());

            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        public PacketInterface getPackets() {
            return packets;
        }

        public void add(Player player) {
            playerPipelineOperation(player, pipeline -> {
                if (pipeline.get(PlayerPacketManager.IDENTIFIER) != null) {
                    pipeline.remove(PlayerPacketManager.IDENTIFIER);
                }

                try {
                    pipeline.addBefore(
                            "packet_handler",
                            PlayerPacketManager.IDENTIFIER,
                            new PlayerPacketManager(plugin, player)
                    );

                } catch (NoSuchElementException e) {
                    String firstName = pipeline.names().isEmpty() ? null : pipeline.names().get(0);
                    if (!TAIL_CONTEXT_IDENTIFIER.equals(firstName)) {
                        throw e;
                    }
                }
            });
        }

        public void addAll() {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                add(player);
            }
        }

        public void remove(Player player) {
            playerPipelineOperation(player, pipeline -> {
                if (pipeline.get(PlayerPacketManager.IDENTIFIER) != null) {
                    pipeline.remove(PlayerPacketManager.IDENTIFIER);
                }
            });
        }

        public void removeAll() {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                remove(player);
            }
        }

        private void playerPipelineOperation(Player player, Consumer<ChannelPipeline> operation) {
            try {
                ChannelPipeline pipeline = packets.getPlayerPipeline(player);

                if (pipeline == null) {
                    plugin.logError(Level.WARNING, "Player pipeline is null for player \"" + player.getName() + "\".", new PacketException("Missing pipeline"));
                    return;
                }

                @SuppressWarnings("resource") // Doesn't implement AutoCloseable in Java 17 (minimum version for plugin to work)
                EventLoop eventLoop = pipeline.channel().eventLoop();
                if (eventLoop.inEventLoop()) {
                    operation.accept(pipeline);
                } else {
                    eventLoop.execute(() -> playerPipelineOperation(player, operation));
                }

            } catch (Exception e) {
                plugin.logError(java.util.logging.Level.WARNING, "An error occurred while executing an operation on a player's pipeline! Player: " + player.getName(), e);
            }
        }
    }

    public PacketsManager getPacketsManager() {
        return packetsManager;
    }

    public TasksManager getTasksManager() {
        return tasksManager;
    }

    private boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}