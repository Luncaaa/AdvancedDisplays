package me.lucaaa.advanceddisplays.api.displays;

import io.netty.util.internal.UnstableApi;
import me.lucaaa.advanceddisplays.api.displays.enums.Property;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

/**
 * A display which shows an entity which is not a {@link org.bukkit.entity.Display}
 */
@SuppressWarnings("unused")
public interface EntityDisplay extends BaseEntity {
    /**
     * Returns the entity currently being displayed.
     * @return The entity currently being displayed.
     */
    EntityType getEntityType();

    /**
     * Sets the entity that is being displayed.
     * @param type The entity that will be displayed.
     */
    void setEntityType(EntityType type);

    /**
     * Sets a property that modifies the entity's appearance for everyone.
     * <p>
     * Effects will only be visible if the property is applicable to the entity (for example, {@link Property#PAINTING_ART} won't affect villagers)
     * To check if the property is applicable or not, you can use {@link #isPropertyApplicable(Property)}
     * @param property The property that modifies the entity's appearance.
     * @param value The value for this property.
     * @param <T> The type of the property.
     *
     * @deprecated METHOD NOT YET IMPLEMENTED - Using it will not change the entity's appearance, only set the value in the class and the config file.
     */
    @ApiStatus.Experimental
    @UnstableApi
    @Deprecated
    <T> void setProperty(Property<T> property, T value);

    /**
     * Sets a property that modifies the entity's appearance for everyone.
     * <p>
     * Effects will only be visible if the property is applicable to the entity (for example, {@link Property#PAINTING_ART} won't affect villagers)
     * To check if the property is applicable or not, you can use {@link #isPropertyApplicable(Property)}
     * @param property The property that modifies the entity's appearance.
     * @param value The value for this property.
     * @param player The player that will see this property.
     * @param <T> The type of the property.
     *
     * @deprecated METHOD NOT YET IMPLEMENTED - Using it will not change the entity's appearance, only set the value in the class and the config file.
     */
    @ApiStatus.Experimental
    @UnstableApi
    @Deprecated
    <T> void setProperty(Property<T> property, T value, Player player);

    /**
     * Gets the value for a given property or the default value if it is not set.
     * @param property The property to look for.
     * @return The value for the given property of the default value if it is not set.
     * @param <T> The type of the property.
     */
    <T> T getPropertyValue(Property<T> property);

    /**
     * Gets the list of properties that the entity has.
     * @return The list of properties that the entity has.
     * @param <T> The type of the property.
     */
    <T> Map<Property<T>, T> getProperties();

    /**
     * Returns true if the entity's appearance will be affected by the given property, false otherwise.
     * @param property The property to check.
     * @return Whether the entity's appearance will be affected by the given property or not.
     */
    boolean isPropertyApplicable(Property<?> property);
}