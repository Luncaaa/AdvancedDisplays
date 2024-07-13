package me.lucaaa.advanceddisplays.api.displays.visibility;

import org.bukkit.entity.Player;

/**
 * Modifies the display's visibility state for everyone or for specific players.
 */
@SuppressWarnings("unused")
public interface VisibilityManager {
    /**
     * Sets whether the display is visible by default or not. It will affect online players whose individual visibility is not set.
     * @param visibility Whether the display is visible by default or not.
     */
    void setGlobalVisibility(Visibility visibility);

    /**
     * Gets whether the display is visible by default or not.
     * @return Whether the display is visible by default or not.
     */
    Visibility getGlobalVisibility();

    /**
     * Sets whether the given player can see the display or not. Overrides the global visibility. This is saved even if player leaves and joins again.
     * If you set it to SHOW, the player will also need the set permission to see the display.
     * @param visibility Whether the player can see the display or not.
     * @param player The player whose visibility status will change.
     */
    void setVisibility(Visibility visibility, Player player);

    /**
     * Returns whether the player can see the display or not. If not set by {@link VisibilityManager#setVisibility(Visibility, Player)}, it will return if the display is globally visible or not.
     * To return true, the display's default visibility or the player's individual visibility must be SHOW and the player must have permission to see the display.
     * @param player The player you want to check if they can see the display or not.
     * @return Whether the player can see the display or not.
     */
    boolean isVisibleByPlayer(Player player);

    /**
     * Sets a player's visibility to the default visibility.
     * @param player The player whose visible state you want to reset.
     */
    void removeIndividualVisibility(Player player);

    /**
     * Clears the individual visibilities and sets every player's visibility to the default visibility.
     */
    void clearPlayerVisibilities();
}
