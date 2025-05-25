package me.lucaaa.advanceddisplays.api.displays.enums;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * Enum which contains the items in the display editor.
 * Used to disable items in the {@link me.lucaaa.advanceddisplays.api.displays.BaseDisplay#openEditor(Player, List)} method.
 */
public enum EditorItem {
    /**
     * Changes the display's size.
     */
    SCALE,

    /**
     * Changes the display's offset from its location.
     */
    TRANSLATION,

    /**
     * Changes the display's left rotation.
     */
    LEFT_ROTATION,

    /**
     * Changes the display's right rotation.
     */
    RIGHT_ROTATION,

    /**
     * Changes the display's yaw and pitch.
     */
    ROTATION,

    /**
     * Changes the display's hitbox size (if it's overriden. See {@link EditorItem#HITBOX_OVERRIDE}).
     */
    HITBOX_SIZE,

    /**
     * Changes the block lighting component of the display.
     */
    BLOCK_LIGHT,

    /**
     * Changes the sky lighting component of the display.
     */
    SKY_LIGHT,

    /**
     * Changes how big the shadow is.
     */
    SHADOW_RADIUS,

    /**
     * Changes how dark the shadow is.
     */
    SHADOW_STRENGTH,

    /**
     * Enables or disables the display's glowing status.
     */
    GLOW_TOGGLE,

    /**
     * Changes the display's glow color.
     */
    GLOW_COLOR_SELECTOR,

    /**
     * Teleports you to the display.
     */
    TELEPORT,

    /**
     * Moves the display to your location.
     */
    MOVE_HERE,

    /**
     * Centers the display on the block it's on.
     */
    CENTER,

    /**
     * Changes the display's rotation axis.
     */
    BILLBOARD,

    /**
     * Whether the hitbox size is set automatically or manually.
     */
    HITBOX_OVERRIDE,

    /**
     * Changes what the display is displaying.
     */
    CURRENT_VALUE,

    /**
     * Permanently removes this display.
     */
    REMOVE,

    /**
     * Block display only.
     * Changes values that makes the block look different.
     */
    BLOCK_DATA,

    /**
     * Item display only.
     * Changes how the displayed item is shown
     */
    ITEM_TRANSFORMATION,

    /**
     * Item displays only.
     * Changes whether the enchanted effect is visible or not.
     */
    ENCHANTED,

    /**
     * Text display only.
     * Changes the text's alignment.
     */
    TEXT_ALIGNMENT,

    /**
     * Text display only.
     * Changes the display's background color.
     */
    BACKGROUND_COLOR,

    /**
     * Text display only.
     * Changes the display's line width.
     */
    LINE_WIDTH,

    /**
     * Text display only.
     * Changes the text's opacity.
     */
    TEXT_OPACITY,

    /**
     * Text display only.
     * Changes whether the display uses the default background or not.
     */
    USE_DEFAULT_BACKGROUND,

    /**
     * Text display only.
     * Changes whether the display can be seen through blocks or not.
     */
    SEE_THROUGH,

    /**
     * Text display only.
     * Changes whether the text is shadowed or not.
     */
    SHADOWED,

    /**
     * Text display only.
     * Changes how often the text changes.
     */
    ANIMATION_TIME,

    /**
     * Text display only.
     * Changes how often placeholders update.
     */
    REFRESH_TIME
}