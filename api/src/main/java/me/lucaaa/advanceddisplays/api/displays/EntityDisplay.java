package me.lucaaa.advanceddisplays.api.displays;

import org.bukkit.entity.EntityType;

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
}