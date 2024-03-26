package me.lucaaa.advanceddisplays.nms_common;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class InternalEntityClickEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Player player;
    private final ClickType clickType;
    private final int interactionId;

    public InternalEntityClickEvent(Player player, ClickType clickType, int interactionId) {
        super(true); // So that players do not get kicked when the event is called.
        this.player = player;
        this.clickType = clickType;
        this.interactionId = interactionId;
    }

    @Override
    @Nonnull
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ClickType getClickType() {
        return this.clickType;
    }

    public int getInteractionId() {
        return this.interactionId;
    }

    public static ClickType getClickTypeFromPacket(boolean isSneaking, int clickTypeNumber) {
        ClickType clickType;
        if (clickTypeNumber == 1) {
            clickType = (isSneaking) ? ClickType.SHIFT_LEFT : ClickType.LEFT;
        } else {
            clickType = (isSneaking) ? ClickType.SHIFT_RIGHT : ClickType.RIGHT;
        }
        return clickType;
    }
}
