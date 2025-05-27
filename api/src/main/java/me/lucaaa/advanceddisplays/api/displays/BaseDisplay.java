package me.lucaaa.advanceddisplays.api.displays;

import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;

/**
 * The settings that Text, Item and Block displays have in common.
 */
@SuppressWarnings("unused")
public interface BaseDisplay extends BaseEntity {
    /**
     * Gets the display's billboard.
     * @return The display's billboard.
     */
    Display.Billboard getBillboard();

    /**
     * Sets the display's billboard for everyone.
     * @param billboard The new display's billboard.
     */
    void setBillboard(Display.Billboard billboard);

    /**
     * Sets the display's billboard for a specific player.
     * @param billboard The new display's billboard.
     * @param player The player who will see the new billboard.
     */
    void setBillboard(Display.Billboard billboard, Player player);

    /**
     * Gets the display's brightness.
     * @return The display's brightness.
     */
    Display.Brightness getBrightness();

    /**
     * Sets the display's brightness for everyone.
     * @param brightness The new display's brightness.
     */
    void setBrightness(Display.Brightness brightness);

    /**
     * Sets the display's brightness for a specific player.
     * @param brightness The new display's brightness.
     * @param player The player who will see the new brightness.
     */
    void setBrightness(Display.Brightness brightness, Player player);

    /**
     * Gets the display's shadow radius.
     * @return The display's shadow radius.
     */
    float getShadowRadius();

    /**
     * Gets the display's shadow strength.
     * @return The display's shadow strength.
     */
    float getShadowStrength();

    /**
     * Sets the display's shadow for everyone.
     * @param shadowRadius The new shadow radius.
     * @param shadowStrength The new shadow strength.
     */
    void setShadow(float shadowRadius, float shadowStrength);

    /**
     * Sets the display's shadow for a specific player.
     * @param shadowRadius The new shadow radius.
     * @param shadowStrength The new shadow strength.
     * @param player The player who will see the new shadow.
     */
    void setShadow(float shadowRadius, float shadowStrength, Player player);

    /**
     * Gets the display's transformation (size, rotation and translation).
     * @return The display's transformation.
     */
    Transformation getTransformation();

    /**
     * Sets the display's transformation for everyone.
     * @param transformation The new transformation.
     */
    void setTransformation(Transformation transformation);

    /**
     * Sets the display's transformation for a specific player.
     * @param transformation The new transformation.
     * @param player The player who will see the new transformation.
     */
    void setTransformation(Transformation transformation, Player player);

    /**
     * Returns the overriden width of the hitbox.
     * @return The width of the hitbox.
     */
    float getHitboxWidth();

    /**
     * Returns the overriden height of the hitbox.
     * @return The height of the hitbox.
     */
    float getHitboxHeight();

    /**
     * Returns whether the display uses an automatic hitbox size or it was set automatically.
     * @return Whether the display uses an automatic hitbox size or it was set automatically.
     */
    boolean isHitboxSizeOverriden();

    /**
     * Sets the new size of the hitbox.
     * @param override Whether these values should be used or not.
     * @param width The new width of the hitbox.
     * @param height The new height of the hitbox.
     */
    void setHitboxSize(boolean override, float width, float height);

    /**
     * Gets the overridden display's glow color.
     * @return The overridden display's glow color.
     */
    Color getGlowColorOverride();

    /**
     * Sets the overridden glow color of the display for everyone. Only visible if the display is glowing.
     * @param color The overridden glow color of the display.
     */
    void setGlowColorOverride(Color color);

    /**
     * Sets the overridden glow color of the display for a specific player. Only visible if the display is glowing.
     * @param color The overridden glow color of the display.
     * @param player The player who will see the glow color.
     */
    void setGlowColorOverride(Color color, Player player);
}