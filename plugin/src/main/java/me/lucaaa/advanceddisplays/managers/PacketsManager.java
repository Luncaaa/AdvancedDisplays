package me.lucaaa.advanceddisplays.managers;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.common.utils.Logger;
import me.lucaaa.advanceddisplays.nms_common.PacketException;
import me.lucaaa.advanceddisplays.nms_common.PacketInterface;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.logging.Level;

public class PacketsManager {
    private final AdvancedDisplays plugin;
    private final PacketInterface packets;
    private static final String TAIL_CONTEXT_IDENTIFIER = "DefaultChannelPipeline$TailContext#0";

    public PacketsManager(AdvancedDisplays plugin) {
        this.plugin = plugin;
        try {
            Class<?> nmsClass = Class.forName("me.lucaaa.advanceddisplays." + plugin.getNmsVersion().name() + ".Packets");
            Object nmsClassInstance = nmsClass.getConstructor(Logger.class).newInstance(plugin);
            this.packets = (PacketInterface) nmsClassInstance;
            addAll();

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
        for (Player player : Bukkit.getOnlinePlayers()) {
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
        for (Player player : Bukkit.getOnlinePlayers()) {
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