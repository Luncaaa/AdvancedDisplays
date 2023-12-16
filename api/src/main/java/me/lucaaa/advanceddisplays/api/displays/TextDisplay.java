package me.lucaaa.advanceddisplays.api.displays;

import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay.TextAlignment;

import java.util.List;

public interface TextDisplay extends BaseDisplay {
    /**
     * Gets the display's text alignment.
     * @return The display's text alignment.
     */
    TextAlignment getAlignment();
    /**
     * Sets the display's text alignment for everyone.
     * @param alignment The new text alignment.
     */
    void setAlignment(TextAlignment alignment);
    /**
     * Sets the display's text alignment for a specific player.
     * @param alignment The new text alignment.
     * @param player The player who will see the new text alignment.
     */
    void setAlignment(TextAlignment alignment, Player player);

    /**
     * Gets the display's background color.
     * @return The display's background color.
     */
    Color getBackgroundColor();
    /**
     * Sets the display's background color for everyone.
     * @param color The new color.
     */
    void setBackgroundColor(Color color);
    /**
     * Sets the display's background color for a specific player.
     * @param color The new color.
     * @param player The player who will see the new color.
     */
    void setBackgroundColor(Color color, Player player);

    /**
     *  Gets the display's line width,
     * @return The display's line width.
     */
    int getLineWidth();
    /**
     * Sets the display's line width for everyone.
     * @param width The new line width.
     */
    void setLineWidth(int width);
    /**
     * Sets the display's line width for a specific player.
     * @param width The new line width.
     * @param player The player who will see the new line width.
     */
    void setLineWidth(int width, Player player);

    /**
     * Gets the displayed text.
     * @return The displayed text.
     */
    List<String> getText();
    /**
     * Sets the text that will be displayed.
     * @param text The text that will be displayed.
     */
    void setText(List<String> text);
    /**
     * Adds a line of text that will be displayed along with the previous list of text.
     * @param text The new line of text.
     */
    void addText(String text);

    /**
     * Gets the text's opacity.
     * @return The text's opacity.
     */
    byte getTextOpacity();
    /**
     * Sets the text opacity for everyone.
     * @param opacity The new text opacity.
     */
    void setTextOpacity(byte opacity);
    /**
     * Sets the text opacity for a specific player.
     * @param opacity The new text opacity.
     * @param player The player that will see the new text opacity.
     */
    void setTextOpacity(byte opacity, Player player);

    /**
     * Gets whether the default background is used.
     * @return Whether the default background is used.
     */
    boolean getUseDefaultBackground();
    /**
     * Sets whether the default background is used for everyone or not.
     * @param defaultBackground Whether the default background is used.
     */
    void setUseDefaultBackground(boolean defaultBackground);
    /**
     * Sets whether the default background is used for a specific player or not.
     * @param defaultBackground Whether the default background is used.
     * @param player The player that will see the default background or not.
     */
    void setUseDefaultBackground(boolean defaultBackground, Player player);

    /**
     * Gets whether the display can be seen through.
     * @return Whether the display can be seen through.
     */
    boolean isSeeThrough();
    /**
     * Sets whether the display can be seen through for everyone or not.
     * @param seeThrough Whether the display can be seen through or not.
     */
    void setSeeThrough(boolean seeThrough);
    /**
     * Sets whether the display can be seen through for a specific player or not.
     * @param seeThrough Whether the display can be seen through or not.
     * @param player The player that will be able to see through the display or not.
     */
    void setSeeThrough(boolean seeThrough, Player player);

    /**
     * Gets whether the display is shadowed.
     * @return Whether the display is shadowed.
     */
    boolean isShadowed();
    /**
     * Sets whether the display is shadowed for everyone or not.
     * @param shadowed Whether the display is shadowed or not
     */
    void setShadowed(boolean shadowed);
    /**
     * Sets whether the display is shadowed for a specific player or not.
     * @param shadowed Whether the display is shadowed or not.
     * @param player The player that will see the display shadowed or not.
     */
    void setShadowed(boolean shadowed, Player player);

    /**
     * Gets the display's animation time (the time each text from the list is displayed).
     * @return The display's animation time.
     */
    int getAnimationTime();
    /**
     * Sets the display's animation time (for everyone).
     * @param animationTime The new animation time.
     */
    void setAnimationTime(int animationTime);

    /**
     * Gets the display's refresh time (the time each displayed text is updated).
     * @return The display's refresh time.
     */
    int getRefreshTime();
    /**
     * Sets the display's refresh time (for everyone).
     * @param refreshTime The new refresh time.
     */
    void setRefreshTime(int refreshTime);
}