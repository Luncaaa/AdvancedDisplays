package me.lucaaa.advanceddisplays.nms_common;

import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;

public class Metadata {
    // - [ Common entity settings ]-
    public final int PROPERTIES = 0;

    // -[ Interaction entity ]-
    public final int HITBOX_WIDTH = 8; // Translation = start, scale = start + 1...
    public final int HITBOX_HEIGHT = 9;

    // -[ Common display settings ]-
    public final int TRANSFORMATION_START; // Translation = start, scale = start + 1...
    public final int BILLBOARD;
    public final int BRIGHTNESS;
    public final int SHADOW_RADIUS; // Radius = start, strength = start + 1
    public final int SHADOW_STRENGTH; // Radius = start, strength = start + 1
    public final int GLOW_COLOR;
    public final int VALUE;

    // -[ Text displays ]-
    public final int LINE_WIDTH;
    public final int BG_COLOR;
    public final int TEXT_OPACITY;
    public final int TEXT_PROPERTIES;

    // -[ Block displays ]-
    // Block uses the "VALUE" property.

    // -[ Item displays ]-
    // Item uses the "VALUE" property.
    public final int ITEM_TRANSFORM;

    public Metadata(Version version) {
        if (version.isEqualOrNewerThan(Version.v1_20_R2)) {
            TRANSFORMATION_START = 11;
            BILLBOARD = 15;
            BRIGHTNESS = 16;
            SHADOW_RADIUS = 18;
            SHADOW_STRENGTH = 19;
            GLOW_COLOR = 22;
            VALUE = 23;
            LINE_WIDTH = 24;
            BG_COLOR = 25;
            TEXT_OPACITY = 26;
            TEXT_PROPERTIES = 27;
            ITEM_TRANSFORM = 24;

        } else {
            TRANSFORMATION_START = 10;
            BILLBOARD = 14;
            BRIGHTNESS = 15;
            SHADOW_RADIUS = 17;
            SHADOW_STRENGTH = 18;
            GLOW_COLOR = 21;
            VALUE = 22;
            LINE_WIDTH = 23;
            BG_COLOR = 24;
            TEXT_OPACITY = 25;
            TEXT_PROPERTIES = 26;
            ITEM_TRANSFORM = 23;
        }
    }

    public record DataInfo<T>(int id, T value) {}

    public static byte getBillboardByte(Display.Billboard billboard) {
        return switch (billboard) {
            case FIXED -> 0;
            case VERTICAL -> 1;
            case HORIZONTAL -> 2;
            case CENTER -> 3;
        };
    }

    public static byte getProperties(boolean isShadowed, boolean isSeeThrough, boolean defaultBackground, TextDisplay.TextAlignment alignment) {
        byte options = 0;

        if (isShadowed) options = (byte) (options | 0x01);
        if (isSeeThrough) options = (byte) (options | 0x02);
        if (defaultBackground) options = (byte) (options | 0x04);
        switch (alignment) {
            case CENTER -> {}
            case LEFT -> options = (byte) (options | 0x08);
            case RIGHT -> options = (byte) (options | 0x10);
        }

        return options;
    }
}