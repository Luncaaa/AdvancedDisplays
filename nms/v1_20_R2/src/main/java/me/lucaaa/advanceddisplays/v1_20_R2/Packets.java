package me.lucaaa.advanceddisplays.v1_20_R2;

import io.netty.channel.ChannelPipeline;
import me.lucaaa.advanceddisplays.api.util.ComponentSerializer;
import me.lucaaa.advanceddisplays.nms_common.*;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.advancement.AdvancementDisplayType;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R2.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R2.util.CraftNamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@SuppressWarnings("unused")
public class Packets implements PacketInterface {
    private final Logger logger;
    private final Map<ChatColor, PlayerTeam> teams = new EnumMap<>(ChatColor.class);

    public Packets(Logger logger) {
        this.logger = logger;
    }

    @Override
    public ChannelPipeline getPlayerPipeline(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;

        try {
            Field field = craftPlayer.getHandle().connection.getClass().getSuperclass().getDeclaredField("c");
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
            Field idField = packet.getClass().getDeclaredField("a");
            idField.setAccessible(true);
            int interactionId = (int) idField.get(packet);

            Field actionField = packet.getClass().getDeclaredField("b");
            actionField.setAccessible(true);
            Object action = actionField.get(packet);

            Field handField = action.getClass().getDeclaredField("a");
            handField.setAccessible(true);
            Enum<?> hand = (Enum<?>) handField.get(action);
            // Prevents interaction event from being fired twice (once for main and once for off hand).
            if (hand == InteractionHand.OFF_HAND) return null;

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
    public Entity createEntity(org.bukkit.entity.EntityType type, Location location) {
        EntityType<?> nmsType = BuiltInRegistries.ENTITY_TYPE.get(CraftNamespacedKey.toMinecraft(type.getKey()));
        CraftWorld world = (CraftWorld) location.getWorld();
        Level level = Objects.requireNonNull(world).getHandle();

        net.minecraft.world.entity.Entity entity = nmsType.create(level);
        if (entity == null) {
            logger.logError(java.util.logging.Level.SEVERE, "Entity couldn't be created for entity type \"" + type.name() + "\". ", new PacketException("Entity not created"));
            return null;
        }

        entity.setPos(location.getX(), location.getY(), location.getZ());
        Packet<ClientGamePacketListener> packet = entity.getAddEntityPacket();

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

        connection.send(new ClientboundAddEntityPacket(((CraftEntity) entity).getHandle()));
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
    public void setGlowing(Entity entity, boolean isGlowing, ChatColor color, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        data.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(0, EntityDataSerializers.BYTE), (byte) (isGlowing ? 0x40 : 0)));
        connection.send(new ClientboundSetEntityDataPacket(entity.getEntityId(), data));

        ServerLevel level = ((CraftWorld) cp.getWorld()).getHandle();
        PlayerTeam team;
        if (!teams.containsKey(color)) {
            team = new PlayerTeam(level.getScoreboard(), String.valueOf(entity.getEntityId()));
            team.setColor(ChatFormatting.valueOf(color.name()));
            team.setCollisionRule(Team.CollisionRule.NEVER);
            teams.put(color, team);
        } else {
            team = teams.get(color);
        }

        connection.send(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true));
        connection.send(ClientboundSetPlayerTeamPacket.createPlayerPacket(team, String.valueOf(entity.getUniqueId()), ClientboundSetPlayerTeamPacket.Action.ADD));
    }

    @Override
    public void setMetadata(int displayId, Player player, Metadata.DataInfo<?>... data) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> allMetadata = new ArrayList<>();

        for (Metadata.DataInfo<?> metadata : data) {
            SynchedEntityData.DataValue<?> value = createDataValue(cp, metadata);

            if (value == null) {
                logger.logError(
                        java.util.logging.Level.WARNING,
                        "An error occurred while setting metadata for display with ID " + displayId + "! Metadata: " + metadata,
                        new PacketException("Unexpected metadata type!")
                );
                return;
            }

            allMetadata.add(value);
        }

        connection.send(new ClientboundSetEntityDataPacket(displayId, allMetadata));
    }

    private SynchedEntityData.DataValue<?> createDataValue(CraftPlayer cp, Metadata.DataInfo<?> metadata) {
        Object value = metadata.value();

        if (value instanceof String s) {
            return SynchedEntityData.DataValue.create(new EntityDataAccessor<>(metadata.id(), EntityDataSerializers.STRING), s);
        } else if (value instanceof Float f) {
            return SynchedEntityData.DataValue.create(new EntityDataAccessor<>(metadata.id(), EntityDataSerializers.FLOAT), f);
        } else if (value instanceof Byte b) {
            return SynchedEntityData.DataValue.create(new EntityDataAccessor<>(metadata.id(), EntityDataSerializers.BYTE), b);
        } else if (value instanceof Integer i) {
            return SynchedEntityData.DataValue.create(new EntityDataAccessor<>(metadata.id(), EntityDataSerializers.INT), i);
        } else if (value instanceof Vector3f v) {
            return SynchedEntityData.DataValue.create(new EntityDataAccessor<>(metadata.id(), EntityDataSerializers.VECTOR3), v);
        } else if (value instanceof Quaternionf q) {
            return SynchedEntityData.DataValue.create(new EntityDataAccessor<>(metadata.id(), EntityDataSerializers.QUATERNION), q);
        } else if (value instanceof ItemStack item) {
            return SynchedEntityData.DataValue.create(new EntityDataAccessor<>(metadata.id(), EntityDataSerializers.ITEM_STACK), CraftItemStack.asNMSCopy(item));
        } else if (value instanceof BlockData block) {
            return SynchedEntityData.DataValue.create(new EntityDataAccessor<>(metadata.id(), EntityDataSerializers.BLOCK_STATE), ((CraftBlockData) block).getState());
        } else if (value instanceof net.kyori.adventure.text.Component c) {
            return SynchedEntityData.DataValue.create(
                    new EntityDataAccessor<>(
                            metadata.id(),
                            EntityDataSerializers.COMPONENT
                    ),
                    Objects.requireNonNull(
                            Component.Serializer.fromJson(
                                    ComponentSerializer.toJSON(c)
                            )
                    )
            );
        } else if (value instanceof ItemDisplay.ItemDisplayTransform t) {
            ItemDisplayContext transform = switch (t) {
                case FIRSTPERSON_LEFTHAND -> ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
                case FIRSTPERSON_RIGHTHAND -> ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
                case THIRDPERSON_LEFTHAND -> ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
                case THIRDPERSON_RIGHTHAND -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                default -> ItemDisplayContext.valueOf(t.name());
            };

            return SynchedEntityData.DataValue.create(new EntityDataAccessor<>(metadata.id(), EntityDataSerializers.BYTE), transform.getId());
        } else {
            return null;
        }
    }

    @Override
    public void sendToast(JavaPlugin plugin, Player player, ItemStack item, String titleJSON, String descriptionJSON, AdvancementDisplayType type) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        ResourceLocation resourceLocation = new ResourceLocation("advanceddisplays", UUID.randomUUID().toString());

        DisplayInfo info = new DisplayInfo(
                CraftItemStack.asNMSCopy(item),
                Objects.requireNonNull(Component.Serializer.fromJson(titleJSON)),
                Objects.requireNonNull(Component.Serializer.fromJson(descriptionJSON)),
                null,
                FrameType.valueOf(type.name()),
                true,
                false,
                true
        );

        Map<String, Criterion<?>> criteria = Map.of("impossible", new Criterion<>(new ImpossibleTrigger(), new ImpossibleTrigger.TriggerInstance()));
        AdvancementRequirements requirements = new AdvancementRequirements(new String[][]{{"impossible"}});

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