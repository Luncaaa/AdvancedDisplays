package me.lucaaa.advanceddisplays.v1_20_R2;

import io.netty.channel.ChannelPipeline;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayHeadType;
import me.lucaaa.advanceddisplays.common.utils.HeadUtils;
import me.lucaaa.advanceddisplays.common.utils.Logger;
import me.lucaaa.advanceddisplays.nms_common.InternalEntityClickEvent;
import me.lucaaa.advanceddisplays.nms_common.PacketInterface;
import me.lucaaa.advanceddisplays.common.utils.Utils;
import net.minecraft.network.Connection;
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
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R2.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@SuppressWarnings("unused")
public class Packets implements PacketInterface {
    @Override
    public ChannelPipeline getPlayerPipeline(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;

        try {
            Field field = craftPlayer.getHandle().connection.getClass().getSuperclass().getDeclaredField("c");
            field.setAccessible(true);
            return ((Connection) field.get(craftPlayer.getHandle().connection)).channel.pipeline();

        } catch (Exception e) {
            Logger.logError(java.util.logging.Level.SEVERE, "An error occurred while getting " + player.getName() + "'s pipeline: ", e);
            return null;
        }
    }

    @Override
    public InternalEntityClickEvent getClickEvent(Player player, Object anyPacket) {
        if (!(anyPacket instanceof ServerboundInteractPacket packet)) return null;

        try {
            Field idField = packet.getClass().getDeclaredField("a");
            idField.setAccessible(true);
            int interactionId = (int) idField.get(packet);

            Field actionField = packet.getClass().getDeclaredField("b");
            actionField.setAccessible(true);
            Object action = actionField.get(packet);
            Method getActionTypeMethod = action.getClass().getDeclaredMethod("a");
            getActionTypeMethod.setAccessible(true);
            int clickTypeNumber = ((Enum<?>) getActionTypeMethod.invoke(action)).ordinal();

            ClickType clickType = InternalEntityClickEvent.getClickTypeFromPacket(packet.isUsingSecondaryAction(), clickTypeNumber);

            return new InternalEntityClickEvent(player, clickType, interactionId);

        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            Logger.logError(java.util.logging.Level.SEVERE, "An error occurred while handling a click on a display: ", e);
            return null;
        }
    }

    @Override
    public Interaction createInteractionEntity(Location location) {
        CraftWorld world = (CraftWorld) location.getWorld();
        Level level = Objects.requireNonNull(world).getHandle();

        net.minecraft.world.entity.Interaction interactionEntity = new net.minecraft.world.entity.Interaction(EntityType.INTERACTION, level);
        interactionEntity.setPos(location.getX(), location.getY(), location.getZ());

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            CraftPlayer cp = (CraftPlayer) onlinePlayer;
            ServerGamePacketListenerImpl connection = cp.getHandle().connection;

            connection.send(new ClientboundAddEntityPacket(interactionEntity));
        }

        return (Interaction) interactionEntity.getBukkitEntity();
    }

    @Override
    public void setInteractionSize(int interactionEntityId, float width, float height, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(8, EntityDataSerializers.FLOAT), width));
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(9, EntityDataSerializers.FLOAT), height));

        connection.send(new ClientboundSetEntityDataPacket(interactionEntityId, data));
    }

    @Override
    public TextDisplay createTextDisplay(Location location) {
        CraftWorld world = (CraftWorld) location.getWorld();
        Level level = Objects.requireNonNull(world).getHandle();

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
        Level level = Objects.requireNonNull(world).getHandle();

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
        Level level = Objects.requireNonNull(world).getHandle();

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
    public void spawnEntity(Entity entity, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        connection.send(new ClientboundAddEntityPacket(((CraftEntity) entity).getHandle()));
    }

    @Override
    public void removeEntity(int entityId) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            CraftPlayer cp = (CraftPlayer) onlinePlayer;
            ServerGamePacketListenerImpl connection = cp.getHandle().connection;

            connection.send(new ClientboundRemoveEntitiesPacket(entityId));
        }
    }

    @Override
    public void removeEntity(int entityId, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        connection.send(new ClientboundRemoveEntitiesPacket(entityId));
    }

    @Override
    public void setLocation(Entity entity, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        connection.send(new ClientboundTeleportEntityPacket(((CraftEntity) entity).getHandle()));
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
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(11, EntityDataSerializers.VECTOR3), transformation.getTranslation()));
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(12, EntityDataSerializers.VECTOR3), transformation.getScale()));
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(13, EntityDataSerializers.QUATERNION), transformation.getLeftRotation()));
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(14, EntityDataSerializers.QUATERNION), transformation.getRightRotation()));

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
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(15, EntityDataSerializers.BYTE), billboardByte));

        connection.send(new ClientboundSetEntityDataPacket(displayId, data));
    }

    @Override
    public void setBrightness(int displayId, org.bukkit.entity.Display.Brightness brightness, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(16, EntityDataSerializers.INT), brightness.getBlockLight() << 4 | brightness.getSkyLight() << 20));

        connection.send(new ClientboundSetEntityDataPacket(displayId, data));
    }

    @Override
    public void setShadow(int displayId, float radius, float strength, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(18, EntityDataSerializers.FLOAT), radius));
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(19, EntityDataSerializers.FLOAT), strength));

        connection.send(new ClientboundSetEntityDataPacket(displayId, data));
    }

    @Override
    public void setGlowing(int displayId, boolean isGlowing, Color color, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(0, EntityDataSerializers.BYTE), (byte) (isGlowing ? 0x40 : 0)));
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(22, EntityDataSerializers.INT), color.asRGB()));

        connection.send(new ClientboundSetEntityDataPacket(displayId, data));
    }

    @Override
    public void setText(int displayId, net.kyori.adventure.text.Component text, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(23, EntityDataSerializers.COMPONENT), net.minecraft.network.chat.Component.Serializer.fromJson(Utils.getColoredTextWithPlaceholders(player, text))));

        connection.send(new ClientboundSetEntityDataPacket(displayId, data));
    }

    @Override
    public void setBackgroundColor(int displayId, Color color, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(25, EntityDataSerializers.INT), color.asARGB()));

        connection.send(new ClientboundSetEntityDataPacket(displayId, data));
    }

    @Override
    public void setLineWidth(int displayId, int lineWidth, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(24, EntityDataSerializers.INT), lineWidth));

        connection.send(new ClientboundSetEntityDataPacket(displayId, data));
    }

    @Override
    public void setTextOpacity(int displayId, byte textOpacity, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(26, EntityDataSerializers.BYTE), textOpacity));

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
            case CENTER -> {}
            case LEFT -> options = (byte) (options | 0x08);
            case RIGHT -> options = (byte) (options | 0x10);
        }

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(27, EntityDataSerializers.BYTE), options));

        connection.send(new ClientboundSetEntityDataPacket(displayId, data));
    }

    @Override
    public void setBlock(int displayId, BlockData block, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(23, EntityDataSerializers.BLOCK_STATE), ((CraftBlockData) block).getState()));

        connection.send(new ClientboundSetEntityDataPacket(displayId, data));
    }

    @Override
    public void setItem(int displayId, Material material, boolean enchanted, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        ItemStack item = new ItemStack(material);
        if (enchanted) item.addUnsafeEnchantment(Enchantment.MENDING, 1);
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(23, EntityDataSerializers.ITEM_STACK), CraftItemStack.asNMSCopy(item)));

        connection.send(new ClientboundSetEntityDataPacket(displayId, data));
    }

    @Override
    public void setHead(int displayId, boolean enchanted, DisplayHeadType displayHeadType, String displayHeadValue, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        ItemStack head = HeadUtils.getHead(displayHeadType, displayHeadValue, player);
        if (enchanted) head.addUnsafeEnchantment(Enchantment.MENDING, 1);

        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(23, EntityDataSerializers.ITEM_STACK), CraftItemStack.asNMSCopy(head)));
        connection.send(new ClientboundSetEntityDataPacket(displayId, data));
    }

    @Override
    public void setItemDisplayTransformation(int displayId, ItemDisplay.ItemDisplayTransform transformation, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        ItemDisplayContext transform = switch (transformation) {
            case FIRSTPERSON_LEFTHAND -> ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
            case FIRSTPERSON_RIGHTHAND -> ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
            case THIRDPERSON_LEFTHAND -> ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
            case THIRDPERSON_RIGHTHAND -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
            default -> ItemDisplayContext.valueOf(transformation.name());
        };
        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(24, EntityDataSerializers.BYTE), transform.getId()));

        connection.send(new ClientboundSetEntityDataPacket(displayId, data));
    }
}