package me.lucaaa.advanceddisplays.v1_21_R6;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import io.netty.channel.ChannelPipeline;
import me.lucaaa.advanceddisplays.api.util.ComponentSerializer;
import me.lucaaa.advanceddisplays.nms_common.*;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.bukkit.*;
import org.bukkit.advancement.AdvancementDisplayType;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

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

            try  {
                Field handField = action.getClass().getDeclaredField("a");
                handField.setAccessible(true);
                Enum<?> hand = (Enum<?>) handField.get(action);
                // Prevents interaction event from being fired twice (once for main and once for offhand).
                if (hand == InteractionHand.OFF_HAND) return null;

            // Left-clicking doesn't have this field, so the error should be ignored.
            } catch (NoSuchFieldException ignored) {}

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
                        new ServerEntity.Synchronizer() {
                            @Override
                            public void sendToTrackingPlayers(@NotNull Packet<? super ClientGamePacketListener> packet) {}
                            @Override
                            public void sendToTrackingPlayersAndSelf(@NotNull Packet<? super ClientGamePacketListener> packet) {}
                            @Override
                            public void sendToTrackingPlayersFiltered(@NotNull Packet<? super ClientGamePacketListener> packet, @NotNull Predicate<ServerPlayer> predicate) {}
                        },
                        Set.of()
                )
        );

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            CraftPlayer cp = (CraftPlayer) onlinePlayer;
            ServerGamePacketListenerImpl connection = cp.getHandle().connection;

            connection.send(packet);
            // For some reason, entity.setNoGravity(true) will not make arrows not fall
            setMetadata(entity.getId(), onlinePlayer, Metadata.DataInfo.ofBoolean(5), true);
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
                        new ServerEntity.Synchronizer() {
                            @Override
                            public void sendToTrackingPlayers(@NotNull Packet<? super ClientGamePacketListener> packet) {}
                            @Override
                            public void sendToTrackingPlayersAndSelf(@NotNull Packet<? super ClientGamePacketListener> packet) {}
                            @Override
                            public void sendToTrackingPlayersFiltered(@NotNull Packet<? super ClientGamePacketListener> packet, @NotNull Predicate<ServerPlayer> predicate) {}
                        },
                        Set.of()
                )
        );
        connection.send(packet);
        // For some reason, entity.setNoGravity(true) will not make arrows not fall
        setMetadata(nmsEntity.getId(), player, Metadata.DataInfo.ofBoolean(5), true);
    }

    @Override
    public void removeEntity(int entityId, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        connection.send(new ClientboundRemoveEntitiesPacket(entityId));
    }

    @Override
    public void setLocation(Entity entity, Location location, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;
        net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        connection.send(new ClientboundTeleportEntityPacket(nmsEntity.getId(), PositionMoveRotation.of(nmsEntity), Set.of(), true));
    }

    @Override
    public void setGlowingColor(Entity entity, ChatColor color, Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

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
    public void setMetadata(int displayId, Player player, Metadata.DataPair<?>... data) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        List<SynchedEntityData.DataValue<?>> allMetadata = new ArrayList<>();

        for (Metadata.DataPair<?> metadata : data) {
            allMetadata.add(createDataValue(cp, metadata));
        }

        connection.send(new ClientboundSetEntityDataPacket(displayId, allMetadata));
    }

    private SynchedEntityData.DataValue<?> createDataValue(CraftPlayer cp, Metadata.DataPair<?> metadata) {
        Object value = metadata.value();
        int id = metadata.data().id();

        return switch (metadata.data().type()) {
            case BOOLEAN -> SynchedEntityData.DataValue.create(new EntityDataAccessor<>(id, EntityDataSerializers.BOOLEAN), (Boolean) value);
            case INT -> SynchedEntityData.DataValue.create(new EntityDataAccessor<>(id, EntityDataSerializers.INT), (int) value);
            case FLOAT -> SynchedEntityData.DataValue.create(new EntityDataAccessor<>(id, EntityDataSerializers.FLOAT), (float) value);
            case BYTE -> SynchedEntityData.DataValue.create(new EntityDataAccessor<>(id, EntityDataSerializers.BYTE), (byte) value);
            case COMPONENT ->
                    SynchedEntityData.DataValue.create(
                            new EntityDataAccessor<>(id, EntityDataSerializers.COMPONENT),
                            ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, ComponentSerializer.toJSON((net.kyori.adventure.text.Component) value)).getOrThrow()
                    );
            case OPTIONAL_COMPONENT -> {
                @SuppressWarnings("unchecked")
                Optional<net.kyori.adventure.text.Component> optional = (Optional<net.kyori.adventure.text.Component>) value;
                Optional<Component> component = optional.map(component1 -> ComponentSerialization.CODEC.parse(
                        JsonOps.INSTANCE,
                        ComponentSerializer.toJSON(component1)
                ).getOrThrow());

                yield SynchedEntityData.DataValue.create(new EntityDataAccessor<>(id, EntityDataSerializers.OPTIONAL_COMPONENT), component);
            }
            case ITEM_STACK -> SynchedEntityData.DataValue.create(new EntityDataAccessor<>(id, EntityDataSerializers.ITEM_STACK), CraftItemStack.asNMSCopy((ItemStack) value));
            case BLOCK_STATE -> SynchedEntityData.DataValue.create(new EntityDataAccessor<>(id, EntityDataSerializers.BLOCK_STATE), ((CraftBlockData) value).getState());
            case VECTOR3 -> SynchedEntityData.DataValue.create(new EntityDataAccessor<>(id, EntityDataSerializers.VECTOR3), (Vector3f) value);
            case QUATERNION -> SynchedEntityData.DataValue.create(new EntityDataAccessor<>(id, EntityDataSerializers.QUATERNION), (Quaternionf) value);
            case BLOCK_FACE -> {
                BlockFace blockFace = (BlockFace) value;
                logger.log(java.util.logging.Level.WARNING, blockFace.name() + " - " + blockFace.isCartesian());
                Direction direction = blockFace.isCartesian() ? Direction.valueOf(blockFace.name()) : Direction.SOUTH;
                yield SynchedEntityData.DataValue.create(new EntityDataAccessor<>(id, EntityDataSerializers.DIRECTION), direction);
            }
            case ROTATION -> SynchedEntityData.DataValue.create(new EntityDataAccessor<>(id, EntityDataSerializers.INT), ((Rotation) value).ordinal());
            case ART -> SynchedEntityData.DataValue.create(new EntityDataAccessor<>(id, EntityDataSerializers.PAINTING_VARIANT), Holder.direct(CraftRegistry.bukkitToMinecraft((Art) value)));
            case DYE_COLOR -> SynchedEntityData.DataValue.create(new EntityDataAccessor<>(id, EntityDataSerializers.INT), DyeColor.valueOf(((org.bukkit.DyeColor) value).name()).ordinal());
        };
    }

    @Override
    public void sendToast(JavaPlugin plugin, Player player, ItemStack item, String titleJSON, String descriptionJSON, AdvancementDisplayType type) {
        CraftPlayer cp = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = cp.getHandle().connection;

        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath("advanceddisplays", UUID.randomUUID().toString());

        DisplayInfo info = new DisplayInfo(
                CraftItemStack.asNMSCopy(item),
                ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(titleJSON)).getOrThrow(),
                ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(descriptionJSON)).getOrThrow(),
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
                Map.of(resourceLocation, progress),
                true
        ));

        new BukkitRunnable() {
            @Override
            public void run() {
                connection.send(new ClientboundUpdateAdvancementsPacket(
                        false,
                        List.of(),
                        Set.of(resourceLocation),
                        Map.of(),
                        true
                ));
            }
        }.runTaskLater(plugin, 0L);
    }
}