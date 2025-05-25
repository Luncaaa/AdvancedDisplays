package me.lucaaa.advanceddisplays.nms_common;

import io.netty.channel.ChannelPipeline;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.advancement.AdvancementDisplayType;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public interface PacketInterface {
    // --[ Packet reader ]--
    ChannelPipeline getPlayerPipeline(Player player);
    InternalEntityClickEvent getClickEvent(Player player, Object packet);

    // --[ Create displays ]--
    Entity createEntity(EntityType type, Location location);
    void spawnEntity(Entity spawnEntity, Player player);

    // --[ Remove displays ]--
    default void removeEntity(int entityId) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            removeEntity(entityId, player);
        }
    }
    void removeEntity(int entityId, Player player);

    // --[ Modify displays ]--
    // -[ General ]-
    void setLocation(Entity entity, Player player);
    void setRotation(int displayId, float yaw, float pitch, Player player);
    void setGlowing(Entity entity, boolean isGlowing, ChatColor color, Player player);

    // - [ Metadata ]-
    default <T> void setMetadata(int displayId, Player player, int dataId, T value) {
        setMetadata(displayId, player, new Metadata.DataInfo<>(dataId, value));
    }
    void setMetadata(int displayId, Player player, Metadata.DataInfo<?>... data);

    // --[ Other ]--
    void sendToast(JavaPlugin plugin, Player player, ItemStack item, String titleJSON, String descriptionJSON, AdvancementDisplayType type);
}