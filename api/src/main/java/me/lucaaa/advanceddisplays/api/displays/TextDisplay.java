package me.lucaaa.advanceddisplays.api.displays;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay.TextAlignment;

import java.util.Map;

/**
 * An entity which displays some text.
 */
@SuppressWarnings("unused")
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
     * @return The displayed text. The map's key is the text's (the value) identifier.
     */
    Map<String, Component> getText();
    /**
     * Sets the text that will be displayed. Will remove the previously set texts.
     * @param text The text that will be displayed. The map's key will be the text's (the value) identifier.
     */
    void setAnimatedText(Map<String, Component> text);
    /**
     * Sets the text that will be displayed. Won't be animated.
     * @param identifier What the text will be identified with.
     * @param text The text that will be displayed.
     */
    void setSingleText(String identifier, Component text);
    /**
     * Adds new text that will be animated along with the previously set text.
     * @param identifier What this text will be identified with.
     * @param text The new lines of text.
     * @return False if a list of texts with that identifier already existed, true otherwise.
     */
    boolean addText(String identifier, Component text);

    /**
     * Removes a list of text from the display.
     * @param identifier The text to remove.
     * @return Whether the text existed and could be removed or not.
     */
    boolean removeText(String identifier);

    /**
     * Shows the next page/animation.
     */
    void nextPage();

    /**
     * Shows the previous page/animation.
     */
    void previousPage();

    /**
     * Shows the given page.
     * @param page The page to show.
     * @throws IllegalArgumentException If the display does not have the given page.
     */
    void setPage(String page);

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