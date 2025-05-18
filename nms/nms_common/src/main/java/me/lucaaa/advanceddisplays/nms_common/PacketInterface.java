package me.lucaaa.advanceddisplays.nms_common;

import io.netty.channel.ChannelPipeline;
import me.lucaaa.advanceddisplays.common.utils.DisplayHeadType;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.advancement.AdvancementDisplayType;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Transformation;

public interface PacketInterface {
    // --[ Packet reader ]--
    ChannelPipeline getPlayerPipeline(Player player);
    InternalEntityClickEvent getClickEvent(Player player, Object packet);

    // --[ Interaction entity ]--
    void setInteractionSize(int interactionEntityId, float width, float height, Player player);

    // --[ Create displays ]--
    Entity createEntity(EntityType type, Location location);
    void spawnEntity(Entity spawnEntity, Player player);

    // --[ Remove displays ]--
    void removeEntity(int entityId);
    void removeEntity(int entityId, Player player);

    // --[ Modify displays ]--
    // -[ General ]-
    void setLocation(Entity entity, Player player);
    void setRotation(int displayId, float yaw, float pitch, Player player);

    void setTransformation(int displayId, Transformation transformation, Player player);
    void setBillboard(int displayId, Display.Billboard billboard, Player player);
    void setBrightness(int displayId, Display.Brightness brightness, Player player);
    void setShadow(int displayId, float radius, float strength, Player player);
    void setGlowing(int displayId, boolean isGlowing, Color color, Player player);

    // -[ Text displays ]-
    void setText(int displayId, String textJSON, Player player);
    void setBackgroundColor(int displayId, Color color, Player player);
    void setLineWidth(int displayId, int lineWidth, Player player);
    void setTextOpacity(int displayId, byte textOpacity, Player player);
    void setProperties(int displayId, boolean isShadowed, boolean isSeeThrough, boolean defaultBackground, TextDisplay.TextAlignment alignment, Player player);

    // -[ Block displays ]-
    void setBlock(int displayId, BlockData block, Player player);

    // -[ Item displays ]-
    void setItem(int displayId, ItemStack item, boolean enchanted, Player player);
    void setHead(int displayId, boolean enchanted, DisplayHeadType displayHeadType, String displayHeadValue, Player player);
    void setItemDisplayTransformation(int displayId, ItemDisplay.ItemDisplayTransform transformation, Player player);

    // -[ Other ]-
    void sendToast(JavaPlugin plugin, Player player, ItemStack item, String titleJSON, String descriptionJSON, AdvancementDisplayType type);
}