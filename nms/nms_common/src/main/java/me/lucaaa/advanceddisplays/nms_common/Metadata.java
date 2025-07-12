package me.lucaaa.advanceddisplays.nms_common;

import me.lucaaa.advanceddisplays.api.displays.enums.Property;
import net.kyori.adventure.text.Component;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Metadata {
    public final Map<Property<?>, DataInfo<?>> propertyData = new HashMap<>();

    // - [ Common entity settings ]-
    public final DataInfo<Byte> PROPERTIES = DataInfo.ofByte(0);
    public final DataInfo<Optional<Component>> CUSTOM_NAME = new DataInfo<>(2, DataType.OPTIONAL_COMPONENT);
    public final DataInfo<Boolean> CUSTOM_NAME_VISIBLE = DataInfo.ofBoolean(3);

    // -[ Interaction entity ]-
    public final DataInfo<Float> HITBOX_WIDTH = DataInfo.ofFloat(8);
    public final DataInfo<Float> HITBOX_HEIGHT = DataInfo.ofFloat(9);

    // -[ Common display settings ]-
    public final DataInfo<Vector3f> TRANSLATION;
    public final DataInfo<Vector3f> SCALE;
    public final DataInfo<Quaternionf> LEFT_ROTATION;
    public final DataInfo<Quaternionf> RIGHT_ROTATION;
    public final DataInfo<Byte> BILLBOARD;
    public final DataInfo<Integer> BRIGHTNESS;
    public final DataInfo<Float> SHADOW_RADIUS;
    public final DataInfo<Float> SHADOW_STRENGTH;
    public final DataInfo<Integer> GLOW_COLOR;

    // -[ Text displays ]-
    public final DataInfo<Component> TEXT;
    public final DataInfo<Integer> LINE_WIDTH;
    public final DataInfo<Integer> BG_COLOR;
    public final DataInfo<Byte> TEXT_OPACITY;
    public final DataInfo<Byte> TEXT_PROPERTIES;

    // -[ Block displays ]-
    public final DataInfo<BlockData> BLOCK;

    // -[ Item displays ]-
    public final DataInfo<ItemStack> ITEM;
    public final DataInfo<Byte> ITEM_TRANSFORM;

    public Metadata(Version version) {
        propertyData.put(Property.ALLAY_DANCING, new DataInfo<>(16, DataType.BOOLEAN));
        propertyData.put(Property.ANIMAL_SITTING, new DataInfo<>(17, DataType.BYTE));
        propertyData.put(Property.ANIMAL_TAMED, new DataInfo<>(17, DataType.BYTE));
        propertyData.put(Property.WOLF_BEGGING, new DataInfo<>(19, DataType.BOOLEAN));
        propertyData.put(Property.WOLF_COLLAR_COLOR, new DataInfo<>(20, DataType.DYE_COLOR));

        if (version.isEqualOrNewerThan(Version.v1_21_R5)) {
            propertyData.put(Property.ITEM_FRAME_DIRECTION, new DataInfo<>(8, DataType.BLOCK_FACE));
            propertyData.put(Property.ITEM_FRAME_ITEM, new DataInfo<>(9, DataType.ITEM_STACK));
            propertyData.put(Property.ITEM_FRAME_ROTATION, new DataInfo<>(10, DataType.ROTATION));
            propertyData.put(Property.PAINTING_DIRECTION, new DataInfo<>(8, DataType.BLOCK_FACE));
            propertyData.put(Property.PAINTING_ART, new DataInfo<>(9, DataType.ART));

        } else {
            propertyData.put(Property.ITEM_FRAME_ITEM, new DataInfo<>(8, DataType.ITEM_STACK));
            propertyData.put(Property.ITEM_FRAME_ROTATION, new DataInfo<>(9, DataType.ROTATION));
            propertyData.put(Property.PAINTING_ART, new DataInfo<>(8, DataType.ART));
        }

        if (version.isEqualOrNewerThan(Version.v1_20_R2)) {
            TRANSLATION = DataInfo.ofVector3f(11);
            SCALE = DataInfo.ofVector3f(12);
            LEFT_ROTATION = DataInfo.ofQuaternionf(13);
            RIGHT_ROTATION = DataInfo.ofQuaternionf(14);
            BILLBOARD = DataInfo.ofByte(15);
            BRIGHTNESS = DataInfo.ofInt(16);
            SHADOW_RADIUS = DataInfo.ofFloat(18);
            SHADOW_STRENGTH = DataInfo.ofFloat(19);
            GLOW_COLOR = DataInfo.ofInt(22);
            TEXT = DataInfo.ofComponent(23);
            LINE_WIDTH = DataInfo.ofInt(24);
            BG_COLOR = DataInfo.ofInt(25);
            TEXT_OPACITY = DataInfo.ofByte(26);
            TEXT_PROPERTIES = DataInfo.ofByte(27);
            ITEM = DataInfo.ofItemStack(23);
            ITEM_TRANSFORM = DataInfo.ofByte(24);
            BLOCK = DataInfo.ofBlockData(23);

        } else {

            TRANSLATION = DataInfo.ofVector3f(10);
            SCALE = DataInfo.ofVector3f(11);
            LEFT_ROTATION = DataInfo.ofQuaternionf(12);
            RIGHT_ROTATION = DataInfo.ofQuaternionf(13);
            BILLBOARD = DataInfo.ofByte(14);
            BRIGHTNESS = DataInfo.ofInt(15);
            SHADOW_RADIUS = DataInfo.ofFloat(17);
            SHADOW_STRENGTH = DataInfo.ofFloat(18);
            GLOW_COLOR = DataInfo.ofInt(21);
            TEXT = DataInfo.ofComponent(22);
            LINE_WIDTH = DataInfo.ofInt(23);
            BG_COLOR = DataInfo.ofInt(24);
            TEXT_OPACITY = DataInfo.ofByte(25);
            TEXT_PROPERTIES = DataInfo.ofByte(26);
            ITEM = DataInfo.ofItemStack(22);
            ITEM_TRANSFORM = DataInfo.ofByte(23);
            BLOCK = DataInfo.ofBlockData(22);
        }
    }

    /**
     * A pair of the data's ID along with the value of its type.
     * @param data The data to set.
     * @param value The value to set the data to.
     * @param <T> The data's type.
     */
    public record DataPair<T>(DataInfo<T> data, T value) {}

    /**
     * Stores info related to a certain metadata.
     * @param id The data's ID (see Protocol wiki).
     * @param type The data's type (see Protocol wiki).
     * @param <T> The data's type.
     */
    @SuppressWarnings("unused")
    public record DataInfo<T>(int id, DataType type) {
        public static DataInfo<Boolean> ofBoolean(int id) {
            return new DataInfo<>(id, DataType.BOOLEAN);
        }

        public static DataInfo<Integer> ofInt(int id) {
            return new DataInfo<>(id, DataType.INT);
        }

        public static DataInfo<Float> ofFloat(int id) {
            return new DataInfo<>(id, DataType.FLOAT);
        }

        public static DataInfo<Byte> ofByte(int id) {
            return new DataInfo<>(id, DataType.BYTE);
        }

        public static DataInfo<Component> ofComponent(int id) {
            return new DataInfo<>(id, DataType.COMPONENT);
        }

        public static DataInfo<ItemStack> ofItemStack(int id) {
            return new DataInfo<>(id, DataType.ITEM_STACK);
        }

        public static DataInfo<BlockData> ofBlockData(int id) {
            return new DataInfo<>(id, DataType.BLOCK_STATE);
        }

        public static DataInfo<Vector3f> ofVector3f(int id) {
            return new DataInfo<>(id, DataType.VECTOR3);
        }

        public static DataInfo<Quaternionf> ofQuaternionf(int id) {
            return new DataInfo<>(id, DataType.QUATERNION);
        }
    }

    public enum DataType {
        BOOLEAN,
        INT,
        FLOAT,
        BYTE,
        COMPONENT,
        OPTIONAL_COMPONENT,
        ITEM_STACK,
        BLOCK_STATE,
        VECTOR3,
        QUATERNION,
        BLOCK_FACE,
        ROTATION,
        ART,
        DYE_COLOR
    }

    @SuppressWarnings("unchecked")
    public <T> Metadata.DataPair<T> createDataPair(Map<Property<?>, Object> properties, Property<T> property, T value) {
        // Property is not present in the server's version (e.g painting direction in 1.19.4)
        if (!propertyData.containsKey(property)) return null;

        Metadata.DataInfo<T> dataInfo = (DataInfo<T>) propertyData.get(property);

        if (property.hasBytePack()) {
            return new DataPair<>(dataInfo, (T) (Byte) property.getByteFlags(properties));

        } else {
            return new Metadata.DataPair<>(dataInfo, value);
        }
    }

    public static byte getProperties(boolean onFire, boolean sprinting, boolean glowing) {
        byte options = 0;

        if (onFire) options = (byte) (options | 0x01);
        if (sprinting) options = (byte) (options | 0x08);
        if (glowing) options = (byte) (options | 0x40);

        return options;
    }

    public static byte getBillboardByte(Display.Billboard billboard) {
        return switch (billboard) {
            case FIXED -> 0;
            case VERTICAL -> 1;
            case HORIZONTAL -> 2;
            case CENTER -> 3;
        };
    }

    public static byte getTextProperties(boolean isShadowed, boolean isSeeThrough, boolean defaultBackground, TextDisplay.TextAlignment alignment) {
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