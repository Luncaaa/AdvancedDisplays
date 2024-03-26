package me.lucaaa.advanceddisplays.managers;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import me.lucaaa.advanceddisplays.nms_common.PacketInterface;
import me.lucaaa.advanceddisplays.common.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class PacketsManager {
    private final PacketInterface packets;

    public PacketsManager(String version) {
        try {
            Class<?> nmsClass = Class.forName("me.lucaaa.advanceddisplays." + version + ".Packets");
            Object nmsClassInstance = nmsClass.getConstructor().newInstance();
            this.packets = (PacketInterface) nmsClassInstance;
            this.addAll();

        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public PacketInterface getPackets() {
        return this.packets;
    }

    public void add(Player player) {
        playerPipelineOperation(player, pipeline -> {
            if (pipeline.get(PlayerPacketManager.IDENTIFIER) != null) {
                pipeline.remove(PlayerPacketManager.IDENTIFIER);
            }
            pipeline.addBefore(
                    "packet_handler",
                    PlayerPacketManager.IDENTIFIER,
                    new PlayerPacketManager(player)
            );
        });
    }

    public void addAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.add(player);
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
            this.remove(player);
        }
    }

    private void playerPipelineOperation(Player player, Consumer<ChannelPipeline> operation) {
        try {
            ChannelPipeline pipeline = this.packets.getPlayerPipeline(player);
            EventLoop eventLoop = pipeline.channel().eventLoop();

            if (eventLoop.inEventLoop()) {
                operation.accept(pipeline);
            } else {
                eventLoop.execute(() -> playerPipelineOperation(player, operation));
            }
        } catch (Exception e) {
            Logger.logError(java.util.logging.Level.WARNING, "An error occurred while executing an operation on a player's pipeline! Player: " + player.getName(), e);
        }
    }
}