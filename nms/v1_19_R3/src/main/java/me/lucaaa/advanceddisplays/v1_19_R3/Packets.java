package me.lucaaa.advanceddisplays.v1_19_R3;

import me.lucaaa.advanceddisplays.common.PacketInterface;
import me.lucaaa.advanceddisplays.common.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftDisplay;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;

import java.util.ArrayList;
import java.util.List;

public class Packets implements PacketInterface {
    @Override
    public TextDisplay createTextDisplay(Location location) {
        CraftWorld world = (CraftWorld) location.getWorld();
        Level level = world.getHandle();

        Display.TextDisplay display = new Display.TextDisplay(EntityType.TEXT_DISPLAY, level);
        display.setPos(location.getX(), location.getY(), location.getZ());

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            CraftPlayer cp = (CraftPlayer) onlinePlayer;
            ServerGamePacketListenerImpl connection = cp.getHandle().connection;

            connection.send(new ClientboundAddEntityPacket(display));
        }

        return (TextDisplay) display.getBukkitEntity();
    }

    @Override
    public ItemDisplay createItemDisplay(Location location) {
        CraftWorld world = (CraftWorld) location.getWorld();
        Level level = world.getHandle();

        Display.ItemDisplay display = new Display.ItemDisplay(EntityType.ITEM_DISPLAY, level);
        display.setPos(location.getX(), location.getY(), location.getZ());

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            CraftPlayer cp = (CraftPlayer) onlinePlayer;
            ServerGamePacketListenerImpl connection = cp.getHandle().connection;

            connection.send(new ClientboundAddEntityPacket(display));
        }

        return (ItemDisplay) display.getBukkitEntity();
    }

    @Override
    public BlockDisplay createBlockDisplay(Location location) {
        CraftWorld world = (CraftWorld) location.getWorld();
        Level level = world.getHandle();

        Display.BlockDisplay display = new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, level);
        display.setPos(location.getX(), location.getY(), location.getZ());

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            CraftPlayer cp = (CraftPlayer) onlinePlayer;
            ServerGamePacketListenerImpl connection = cp.getHandle().connection;

            connection.send(new ClientboundAddEntityPacket(display));
        }

        return (BlockDisplay) display.getBukkitEntity();
    }

    @Override
    public void spawnDisplay(org.bukkit.entity.Display display, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        connection.send(new ClientboundAddEntityPacket(((CraftDisplay) display).getHandle()));
    }

    @Override
    public void removeDisplay(int displayId) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            CraftPlayer cp = (CraftPlayer) onlinePlayer;
            ServerGamePacketListenerImpl connection = cp.getHandle().connection;

            connection.send(new ClientboundRemoveEntitiesPacket(displayId));
        }
    }

    @Override
    public void setLocation(org.bukkit.entity.Display display, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        connection.send(new ClientboundTeleportEntityPacket(((CraftDisplay) display).getHandle()));
    }

    @Override
    public void setRotation(int displayId, float yaw, float pitch, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        connection.send(new ClientboundMoveEntityPacket.Rot(displayId, (byte) ((yaw / 360.0) * 256), (byte) ((pitch / 360.0) * 256), false));
    }

    @Override
    public void setTransformation(int displayId, Transformation transformation, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(10, EntityDataSerializers.VECTOR3), transformation.getTranslation()));
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(11, EntityDataSerializers.VECTOR3), transformation.getScale()));
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(12, EntityDataSerializers.QUATERNION), transformation.getLeftRotation()));
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(13, EntityDataSerializers.QUATERNION), transformation.getRightRotation()));

        connection.send(new ClientboundSetEntityDataPacket(displayId, data));
    }

    @Override
    public void setBillboard(int displayId, org.bukkit.entity.Display.Billboard billboard, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        byte billboardByte = switch (billboard) {
            case FIXED -> 0;
            case VERTICAL -> 1;
            case HORIZONTAL -> 2;
            case CENTER -> 3;
        };

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(14, EntityDataSerializers.BYTE), billboardByte));

        connection.send(new ClientboundSetEntityDataPacket(displayId, data));
    }

    @Override
    public void setBrightness(int displayId, org.bukkit.entity.Display.Brightness brightness, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(15, EntityDataSerializers.INT), brightness.getBlockLight() << 4 | brightness.getSkyLight() << 20));

        connection.send(new ClientboundSetEntityDataPacket(displayId, data));
    }

    @Override
    public void setShadow(int displayId, float radius, float strength, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(17, EntityDataSerializers.FLOAT), radius));
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(18, EntityDataSerializers.FLOAT), strength));

        connection.send(new ClientboundSetEntityDataPacket(displayId, data));
    }

    @Override
    public void setText(int displayId, String text, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(22, EntityDataSerializers.COMPONENT), Component.Serializer.fromJson(Utils.getColoredTextWithPlaceholders(player, text))));

        connection.send(new ClientboundSetEntityDataPacket(displayId, data));
    }

    @Override
    public void setBackgroundColor(int displayId, Color color, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(24, EntityDataSerializers.INT), color.asARGB()));

        connection.send(new ClientboundSetEntityDataPacket(displayId, data));
    }

    @Override
    public void setLineWidth(int displayId, int lineWidth, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(23, EntityDataSerializers.INT), lineWidth));

        connection.send(new ClientboundSetEntityDataPacket(displayId, data));
    }

    @Override
    public void setTextOpacity(int displayId, byte textOpacity, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(25, EntityDataSerializers.BYTE), textOpacity));

        connection.send(new ClientboundSetEntityDataPacket(displayId, data));
    }

    @Override
    public void setProperties(int displayId, boolean isShadowed, boolean isSeeThrough, boolean defaultBackground, TextDisplay.TextAlignment alignment, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;
        byte options = 0;

        if (isShadowed) options = (byte) (options | 0x01);
        if (isSeeThrough) options = (byte) (options | 0x02);
        if (defaultBackground) options = (byte) (options | 0x04);
        switch (alignment) {
            case CENTER: break;
            case LEFT:
                options = (byte) (options | 0x08);
                break;
            case RIGHT:
                options = (byte) (options | 0x10);
                break;
        }

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(26, EntityDataSerializers.BYTE), options));

        connection.send(new ClientboundSetEntityDataPacket(displayId, data));
    }

    @Override
    public void setBlock(int displayId, BlockData block, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(22, EntityDataSerializers.BLOCK_STATE), ((CraftBlockData) block).getState()));

        connection.send(new ClientboundSetEntityDataPacket(displayId, data));
    }

    @Override
    public void setItem(int displayId, Material material, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(22, EntityDataSerializers.ITEM_STACK), CraftItemStack.asNMSCopy(new ItemStack(material))));

        connection.send(new ClientboundSetEntityDataPacket(displayId, data));
    }

    @Override
    public void setItemDisplayTransformation(int displayId, ItemDisplay.ItemDisplayTransform transformation, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(23, EntityDataSerializers.BYTE), ItemDisplayContext.valueOf(transformation.name()).getId()));

        connection.send(new ClientboundSetEntityDataPacket(displayId, data));
    }
}