package me.lucaaa.advanceddisplays.nms_common;

import org.bukkit.event.inventory.ClickType;

public record InternalEntityClickEvent(ClickType clickType, int interactionId) {

    public static ClickType getClickTypeFromPacket(boolean isSneaking, boolean isAttack) {
        ClickType clickType;
        if (isAttack) {
            clickType = (isSneaking) ? ClickType.SHIFT_LEFT : ClickType.LEFT;
        } else {
            clickType = (isSneaking) ? ClickType.SHIFT_RIGHT : ClickType.RIGHT;
        }
        return clickType;
    }
}