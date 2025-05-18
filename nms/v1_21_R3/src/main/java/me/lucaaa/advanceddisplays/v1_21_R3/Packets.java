package me.lucaaa.advanceddisplays.v1_21_R3;

import io.netty.channel.ChannelPipeline;
import me.lucaaa.advanceddisplays.common.utils.DisplayHeadType;
import me.lucaaa.advanceddisplays.common.utils.HeadUtils;
import me.lucaaa.advanceddisplays.nms_common.InternalEntityClickEvent;
import me.lucaaa.advanceddisplays.nms_common.PacketException;
import me.lucaaa.advanceddisplays.nms_common.PacketInterface;
import me.lucaaa.advanceddisplays.common.utils.Logger;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.advancement.AdvancementDisplayType;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@SuppressWarnings("unused")
public class Packets implements PacketInterface {
    private final Logger logger;

    public Packets(Logger logger) {
        this.logger = logger;
    }

    @Override
    public ChannelPipeline getPlayerPipeline(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;

        try {
            Field field = craftPlayer.getHandle().connection.getClass().getSuperclass().getDeclaredField("e");
            field.setAccessible(true);
            return ((Connection) field.get(craftPlayer.getHandle().connection)).channel.pipeline();

        } catch (Exception e) {
            logger.logError(java.util.logging.Level.SEVERE, "An error occurred while getting " + player.getName() + "'s pipeline.", e);
            return null;
        }
    }

    @Override
    public InternalEntityClickEvent getClickEvent(Player player, Object anyPacket) {
        // Some plugins may send their own packet implementing the interact packet, making using "instanceof"
        // not valid in this case because it'll return "true" but might not have the necessary methods o field.
        if (!anyPacket.getClass().equals(ServerboundInteractPacket.class)) return null;

        ServerboundInteractPacket packet = (ServerboundInteractPacket) anyPacket;
        try {
            Field idField = packet.getClass().getDeclaredField("b");
            idField.setAccessible(true);
            int interactionId = (int) idField.get(packet);

            Field actionField = packet.getClass().getDeclaredField("c");
            actionField.setAccessible(true);
            Object action = actionField.get(packet);
            Method getActionTypeMethod = action.getClass().getDeclaredMethod("a");
            getActionTypeMethod.setAccessible(true);
            int clickTypeNumber = ((Enum<?>) getActionTypeMethod.invoke(action)).ordinal();

            ClickType clickType = InternalEntityClickEvent.getClickTypeFromPacket(packet.isUsingSecondaryAction(), clickTypeNumber);

            return new InternalEntityClickEvent(clickType, interactionId);

        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            logger.logError(java.util.logging.Level.SEVERE, "An error occurred while handling a click on a display: ", e);
            return null;
        }
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
    public Entity createEntity(org.bukkit.entity.EntityType type, Location location) {
        Optional<Holder.Reference<EntityType<?>>> optional = BuiltInRegistries.ENTITY_TYPE.get(CraftNamespacedKey.toMinecraft(type.getKey()));
        if (optional.isEmpty()) {
            logger.logError(java.util.logging.Level.SEVERE, "Entity not found for entity type \"" + type.name() + "\". ", new PacketException("Invalid entity type"));
            return null;
        }

        EntityType<?> nmsType = optional.get().value();
        CraftWorld world = (CraftWorld) location.getWorld();
        Level level = Objects.requireNonNull(world).getHandle();

        net.minecraft.world.entity.Entity entity = nmsType.create(level, EntitySpawnReason.EVENT);
        if (entity == null) {
            logger.logError(java.util.logging.Level.SEVERE, "Entity couldn't be created for entity type \"" + type.name() + "\". ", new PacketException("Entity not created"));
            return null;
        }

        entity.setPos(location.getX(), location.getY(), location.getZ());
        Packet<ClientGamePacketListener> packet = entity.getAddEntityPacket(
                new ServerEntity(
                        level.getMinecraftWorld(),
                        entity,
                        0,
                        false,
                        consumer -> {},
                        Set.of()
                )
        );

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            CraftPlayer cp = (CraftPlayer) onlinePlayer;
            ServerGamePacketListenerImpl connection = cp.getHandle().connection;

            connection.send(packet);
        }

        return entity.getBukkitEntity();
    }

    @Override
    public void spawnEntity(Entity entity, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        Packet<ClientGamePacketListener> packet = nmsEntity.getAddEntityPacket(
                new ServerEntity(
                        ((CraftWorld) entity.getWorld()).getHandle(),
                        nmsEntity,
                        0,
                        false,
                        consumer -> {},
                        Set.of()
                )
        );
        connection.send(packet);
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
        net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) entity).getHandle();

        connection.send(new ClientboundTeleportEntityPacket(nmsEntity.getId(), PositionMoveRotation.of(nmsEntity), Set.of(), nmsEntity.onGround()));
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
    public void setText(int displayId, String textJSON, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(23, EntityDataSerializers.COMPONENT), Objects.requireNonNull(Component.Serializer.fromJson(textJSON, cp.getHandle().registryAccess()))));

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
    public void setItem(int displayId, ItemStack item, boolean enchanted, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        if (enchanted) item.addUnsafeEnchantment(Enchantment.MENDING, 1);
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(23, EntityDataSerializers.ITEM_STACK), CraftItemStack.asNMSCopy(item)));

        connection.send(new ClientboundSetEntityDataPacket(displayId, data));
    }

    @Override
    public void setHead(int displayId, boolean enchanted, DisplayHeadType displayHeadType, String displayHeadValue, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        ItemStack head = HeadUtils.getHead(displayHeadType, displayHeadValue, player, logger);
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

    @Override
    public void sendToast(JavaPlugin plugin, Player player, ItemStack item, String titleJSON, String descriptionJSON, AdvancementDisplayType type) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath("advanceddisplays", UUID.randomUUID().toString());

        DisplayInfo info = new DisplayInfo(
                CraftItemStack.asNMSCopy(item),
                Objects.requireNonNull(Component.Serializer.fromJson(titleJSON, cp.getHandle().registryAccess())),
                Objects.requireNonNull(Component.Serializer.fromJson(descriptionJSON, cp.getHandle().registryAccess())),
                Optional.empty(),
                AdvancementType.valueOf(type.name()),
                true,
                false,
                true
        );

        Map<String, Criterion<?>> criteria = Map.of("impossible", new Criterion<>(new ImpossibleTrigger(), new ImpossibleTrigger.TriggerInstance()));
        AdvancementRequirements requirements = new AdvancementRequirements(List.of(List.of("impossible")));

        Advancement advancement = new Advancement(
                Optional.empty(),
                Optional.of(info),
                AdvancementRewards.EMPTY,
                criteria,
                requirements,
                false
        );

        AdvancementProgress progress = new AdvancementProgress();
        progress.update(requirements);
        Objects.requireNonNull(progress.getCriterion("impossible")).grant();

        connection.send(new ClientboundUpdateAdvancementsPacket(
                false,
                List.of(new AdvancementHolder(resourceLocation, advancement)),
                Set.of(),
                Map.of(resourceLocation, progress)
        ));

        new BukkitRunnable() {
            @Override
            public void run() {
                connection.send(new ClientboundUpdateAdvancementsPacket(
                        false,
                        List.of(),
                        Set.of(resourceLocation),
                        Map.of()
                ));
            }
        }.runTaskLater(plugin, 0L);
    }
}