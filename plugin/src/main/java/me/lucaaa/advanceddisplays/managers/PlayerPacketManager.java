package me.lucaaa.advanceddisplays.managers;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.nms_common.InternalEntityClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerPacketManager extends ChannelDuplexHandler {

    public static final String IDENTIFIER = "ad_packet_manager";
    private final Player player;

    public PlayerPacketManager(Player player) {
        this.player = player;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
        InternalEntityClickEvent clickEvent = AdvancedDisplays.packetsManager.getPackets().getClickEvent(this.player, packet);

        if (clickEvent != null) {
            Bukkit.getPluginManager().callEvent(clickEvent);
        }

        super.channelRead(ctx, packet);
    }
}
