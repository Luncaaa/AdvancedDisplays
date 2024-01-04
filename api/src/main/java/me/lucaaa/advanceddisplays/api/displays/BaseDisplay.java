package me.lucaaa.advanceddisplays.api.displays;

import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;

/**
 * The settings that Text, Item and Block displays have in common.
 */
@SuppressWarnings("unused")
public interface BaseDisplay {
    /**
     * Gets the type of the display.
     * @return The type of the display: Either BLOCK, ITEM or TEXT
     */
    DisplayType getType();

    /**
     * Gets the location of the display.
     * @return The location of the display.
     */
    Location getLocation();

    /**
     * Sets the location of the display.
     * @param location The new location.
     */
    void setLocation(Location location);

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
     * Gets the display's yaw.
     * @return The display's yaw.
     */
    float getYaw();
    /**
     * Gets the display's pitch.
     * @return The display's pitch.
     */
    float getPitch();

    /**
     * Sets the display's rotation for everyone.
     * @param yaw The new yaw.
     * @param pitch The new pitch.
     */
    void setRotation(float yaw, float pitch);
    /**
     * Sets the display's rotation for a specific player.
     * @param yaw The new yaw.
     * @param pitch The new pitch.
     * @param player The player who will see the new rotation.
     */
    void setRotation(float yaw, float pitch, Player player);

    /**
     * Makes the display glow for everyone.
     * @param isGlowing If the display will glow or not.
     */
    void setGlowing(boolean isGlowing);
    /**
     * Makes the display glow for everyone.
     * @param isGlowing If the display will glow or not.
     * @param player The player who will see the glow.
     */
    void setGlowing(boolean isGlowing, Player player);

    /**
     * Sets the glow color of the display. Only visible if the display is glowing.
     * @param color The glow color of the display.
     */
    void setGlowColor(Color color);
    /**
     * Sets the glow color of the display. Only visible if the display is glowing.
     * @param color The glow color of the display.
     * @param player The player who will see the glow color.
     */
    void setGlowColor(Color color, Player player);
}
