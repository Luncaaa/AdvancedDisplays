package me.lucaaa.advanceddisplays.common;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.util.Transformation;

public interface PacketInterface {
    // --[ Create displays ]--
    // If the player is null, the display (packet) will be created for everyone. Otherwise, the display will just be created for the player.
    TextDisplay createTextDisplay(Location location);
    ItemDisplay createItemDisplay(Location location);
    BlockDisplay createBlockDisplay(Location location);
    void spawnDisplay(Display display, Player player);

    // --[ Remove displays ]--
    void removeDisplay(int displayId);

    // --[ Modify displays ]--
    // -[ General ]-
    void setLocation(Display display, Player player);
    void setRotation(int displayId, float yaw, float pitch, Player player);

    void setTransformation(int displayId, Transformation transformation, Player player);
    void setBillboard(int displayId, Display.Billboard billboard, Player player);
    void setBrightness(int displayId, Display.Brightness brightness, Player player);
    void setShadow(int displayId, float radius, float strength, Player player);

    // -[ Text displays ]-
    void setText(int displayId, String text, Player player);
    void setBackgroundColor(int displayId, Color color, Player player);
    void setLineWidth(int displayId, int lineWidth, Player player);
    void setTextOpacity(int displayId, byte textOpacity, Player player);
    void setProperties(int displayId, boolean isShadowed, boolean isSeeThrough, boolean defaultBackground, TextDisplay.TextAlignment alignment, Player player);

    // -[ Block displays ]-
    void setBlock(int displayId, BlockData block, Player player);

    // -[ Item displays ]-
    void setItem(int displayId, Material material, boolean enchanted, Player player);
    void setHead(int displayId, boolean enchanted, String displayHeadType, String displayHeadValue, Player player);
    void setItemDisplayTransformation(int displayId, ItemDisplay.ItemDisplayTransform transformation, Player player);
}