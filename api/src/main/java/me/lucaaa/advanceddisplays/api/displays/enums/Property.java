package me.lucaaa.advanceddisplays.api.displays.enums;

import com.google.common.annotations.Beta;
import io.netty.util.internal.UnstableApi;
import org.bukkit.Art;
import org.bukkit.entity.*;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A property that modifies an entity's appearance.
 * <p>
 * For example, the art of a painting or the equipment of a mob.
 * @param <T> The type of this property. For example, a boolean.
 */
@ApiStatus.Experimental
@UnstableApi
@Beta
public class Property<T> {
    private final String name;
    private final T def;
    private final Class<T> type;
    private final BytePack bytePack;
    private final Class<?> ownerEntity;

    private static final Map<String, Property<?>> REGISTRY = new ConcurrentHashMap<>();

    /**
     * Creates a property that modifies the entity's appearance.
     * @param name The name of the property in the config file.
     * @param def The default value.
     * @param ownerEntity The entity interface that has this property. For example, Tameable has TAMEABLE_FLAGS
     */
    private Property(String name, T def, Class<?> ownerEntity) {
        this(name, def, null, ownerEntity);
    }

    /**
     * Creates a property that modifies the entity's appearance.
     * @param name The name of the property in the config file.
     * @param def The default value.
     * @param bytePack The properties that this property shares a byte with (for example, "sitting" and "tamed" for tameable animals).
     * @param ownerEntity The entity interface that has this property. For example, Tameable has TAMEABLE_FLAGS
     */
    @SuppressWarnings("unchecked")
    private Property(String name, T def, BytePack bytePack, Class<?> ownerEntity) {
        this.name = name;
        this.def = def;
        this.type = (Class<T>) def.getClass();
        this.bytePack = bytePack;
        this.ownerEntity = ownerEntity;

        // Add each property to the registry so that they can be looped in the entity display
        // Useful because that way it won't be needed to manually check for every property.
        REGISTRY.put(name, this);
    }

    /* TODO: Fix Implementation
    public static final Property<BlockFace> ITEM_FRAME_DIRECTION = new Property<>("direction", BlockFace.SOUTH, ItemFrame.class);
    public static final Property<ItemStack> ITEM_FRAME_ITEM = new Property<>("item", new ItemStack(Material.AIR), ItemFrame.class);
    public static final Property<Rotation> ITEM_FRAME_ROTATION = new Property<>("rotation", Rotation.NONE, ItemFrame.class);
    public static final Property<BlockFace> PAINTING_DIRECTION = new Property<>("direction", BlockFace.SOUTH, Painting.class);
    */
    /**
     * Changes the art a painting is showing.
     */
    public static final Property<Art> PAINTING_ART = new Property<>("painting", Art.KEBAB, Painting.class);
    /* TODO: Fix Implementation
    public static final Property<Boolean> ALLAY_DANCING = new Property<>("dancing", false, Allay.class);
    public static final Property<Boolean> ANIMAL_SITTING = new Property<>("sitting", false, BytePack.TAMEABLE_FLAGS, Tameable.class);
    public static final Property<Boolean> ANIMAL_TAMED = new Property<>("tamed", false, BytePack.TAMEABLE_FLAGS, Tameable.class);
    public static final Property<Boolean> WOLF_BEGGING = new Property<>("begging", false, Wolf.class);
    public static final Property<DyeColor> WOLF_COLLAR_COLOR = new Property<>("collar-color", DyeColor.ORANGE, Wolf.class);
     */

    // TODO: 1.21 features
    // public static final Property<> WOLF_VARIANT = new Property<>("variant", Boolean.class);
    // public static final Property<ItemStack> OMINOUS_ITEM_SPAWNER_ITEM = new Property<>("item", new ItemStack(Material.AIR), OminousItemSpawner.class)

    /**
     * Gets the name of this property.
     * @return The name of this property.
     */
    public String name() {
        return name;
    }

    /**
     * Gets the type of this property.
     * @return The type of this property.
     */
    public Class<T> type() {
        return type;
    }

    /**
     * Gets the byte for this property after setting all the properties which share the same byte.
     * @param properties The display's properties.
     * @return The byte for this property after parsing all flags.
     */
    @ApiStatus.Internal
    public byte getByteFlags(Map<Property<?>, Object> properties) {
        if (bytePack == null) return 0;

        byte options = 0;

        /* TODO: Fix Implementation
        if (bytePack == BytePack.TAMEABLE_FLAGS) {
            if (properties.containsKey(Property.ANIMAL_SITTING) &&
                    (Boolean) properties.get(Property.ANIMAL_SITTING)) options = (byte) (options | 0x01);
            if (properties.containsKey(Property.ANIMAL_TAMED) &&
                    (Boolean) properties.get(Property.ANIMAL_TAMED)) options = (byte) (options | 0x04);
        }
         */

        return options;
    }

    /**
     * Returns whether this property shares a byte with others or not.
     * @return Whether this property shares a byte with others or not.
     */
    @ApiStatus.Internal
    public boolean hasBytePack() {
       return bytePack != null;
    }

    /**
     * Returns the entity's interface which "owns" this property.
     * <p>
     * For example, while a wolf can be tamed, that property belongs to the {@link Tameable} interface.
     * @return The entity's interface which "owns" this property.
     */
    public Class<?> ownerEntity() {
        return ownerEntity;
    }

    /**
     * Gets the default value for a property.
     * @return The default value for a property.
     */
    public T getDefaultValue() {
        return def;
    }

    /**
     * Gets all the registered properties
     * @return All the registered properties.
     */
    public static Collection<Property<?>> getProperties() {
        return REGISTRY.values();
    }

    /**
     * Used for properties which share a byte in an entity's metadata.
     * @hidden
     */
    @ApiStatus.Internal
    enum BytePack {
        TAMEABLE_FLAGS
    }
}