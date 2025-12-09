package me.lucaaa.advanceddisplays.managers;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.displays.ADBaseEntity;
import me.lucaaa.advanceddisplays.nms_common.InternalEntityClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class PlayerPacketManager extends ChannelDuplexHandler {
    private final AdvancedDisplays plugin;
    public static final String IDENTIFIER = "ad_packet_manager";
    private final Player player;
    private boolean pastInteraction = false;

    public PlayerPacketManager(AdvancedDisplays plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
        super.channelRead(ctx, packet);

        InternalEntityClickEvent clickEvent = plugin.getPacketsManager().getPackets().getClickEvent(player, packet);
        if (clickEvent == null) return;

        ClickType clickType = clickEvent.clickType();

        // Because the event is fired twice, the first time the event is run and the variable is set to "true".
        // The second time, when the variable is true, the event will be ignored.
        if (clickType == ClickType.RIGHT || clickType == ClickType.SHIFT_RIGHT) {
            if (pastInteraction) {
                pastInteraction = false;
                return;
            } else {
                pastInteraction = true;
            }

            /* Old System
            if (pastInteraction > 0) {
                long now = System.currentTimeMillis();
                if (now - pastInteraction <= 500) {
                    pastInteraction = 0;
                    return;
                }
            } else {
                pastInteraction = System.currentTimeMillis();
            } */
        }

        ADBaseEntity display = plugin.getInteractionsManager().getDisplay(clickEvent.interactionId());
        if (display == null) return;

        // Run sync to prevent errors
        plugin.getTasksManager().runTask(plugin, () -> display.runActions(player,clickType));
    }
}