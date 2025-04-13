package me.lucaaa.advanceddisplays.api.conditions;

import me.lucaaa.advanceddisplays.api.ADAPI;

/**
 * Interface used to get built-in conditions.
 * <p>
 * You can get an instance of this interface using {@link ADAPI#getConditionsFactory()}
 */
@SuppressWarnings("unused")
public interface ConditionsFactory {
    /**
     * The player must be within a specific distance from the display.
     * @param distance The maximum distance at which the player must be.
     * @return The distance condition.
     */
    Condition distance(double distance);

    /**
     * The player must have the provided permission.
     * @param permission The permission which  the player must have.
     * @return The permission condition.
     */
    Condition hasPermission(String permission);

    /**
     * The player must NOT have the provided permission.
     * @param permission The permission which the player must NOT have.
     * @return The permission condition.
     */
    Condition lacksPermission(String permission);
}