package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.EntityDisplay;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.api.displays.enums.Property;
import me.lucaaa.advanceddisplays.data.Utils;
import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.managers.DisplaysManager;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

@SuppressWarnings("unchecked")
public class ADEntityDisplay extends ADBaseEntity implements EntityDisplay {
    private final Map<Property<?>, Object> properties = new HashMap<>();

    @SuppressWarnings("rawtypes")
    public ADEntityDisplay(AdvancedDisplays plugin, DisplaysManager displaysManager, ConfigManager config, String name, EntityType entityType) {
        super(plugin, displaysManager, config, name, DisplayType.ENTITY, entityType);

        ConfigurationSection dataSection = config.getSection("data", true, entitySection);
        for (Property<?> property : Property.getProperties()) {
            Object data = dataSection.get(property.name());
            if (data == null) continue;

            if (!property.ownerEntity().isAssignableFrom(entity.getClass())) continue;

            if (property.type().isEnum()) {
                try {
                    data = Enum.valueOf((Class<Enum>) property.type(), data.toString().toUpperCase());
                } catch (IllegalArgumentException e) {
                    errors.add("Invalid value set for property \"" + property.name() + "\": " + data);
                    return;
                }

            } else if (Keyed.class.isAssignableFrom(property.type())) {
                Class<? extends Keyed> clazz = (Class<? extends Keyed>) property.type();
                // Get the actual class for the registry (for example, org.bukkit.Art from org.bukkit.craftbukkit.CraftArt)
                for (Class<?> interfaceClass : property.type().getInterfaces()) {
                    if (Keyed.class.isAssignableFrom(interfaceClass) && interfaceClass != Keyed.class) {
                        clazz = (Class<? extends Keyed>) interfaceClass;
                    }
                }

                NamespacedKey key = NamespacedKey.minecraft(data.toString().toLowerCase());
                // data = property.registry().get(key);
                data = Objects.requireNonNull(Bukkit.getRegistry(clazz)).get(key);

            } else if (ItemStack.class.isAssignableFrom(property.type())) {
                try {
                    // The section must exist because "data" is not null, and data is dataSection.get(property.name())
                    ConfigurationSection itemSection = Objects.requireNonNull(dataSection.getConfigurationSection(property.name()));
                    ItemStack item = new ItemStack(Material.valueOf(itemSection.getString("material")));
                    data = Utils.loadItemData(item, itemSection, getLocation().getWorld(), plugin);
                } catch (IllegalArgumentException e) {
                    plugin.log(Level.WARNING, "Invalid material set for an item for entity display \"" + getName() + "\"!");
                    continue;
                }

            } else if (!property.type().isInstance(data)) {
                plugin.log(Level.WARNING, "Data \"" + property.name() + "\" is not a \"" + property.type().getSimpleName() + "\" value for entity display \"" + getName() + "\"!");
                continue;
            }

            properties.put(property, property.type().cast(data));
        }
    }

    public ADEntityDisplay(AdvancedDisplays plugin, DisplaysManager displaysManager, String name, Location location, EntityType entityType, boolean saveToConfig) {
        super(plugin, displaysManager, name, DisplayType.ENTITY, entityType, location, saveToConfig);
    }

    @Override
    protected ConfigManager createConfig(Location location) {
        ConfigManager config = super.createConfig(location);

        // Set properties in the display file.
        // The "entity" section is retrieved and not created because it was already created in the parent method.
        entitySection = Objects.requireNonNull(config.getConfig().getConfigurationSection("entity"));
        entitySection.createSection("data");

        config.save();
        return config;
    }

    @Override
    public void sendMetadataPackets(Player player) {
        super.sendMetadataPackets(player);

        /* TODO: Fix implementation
        Metadata.DataPair<?>[] dataPairs = properties.entrySet().stream()
                .map(entry -> plugin.metadata.createDataPair(properties, (Property<Object>) entry.getKey(), entry.getValue()))
                .filter(Objects::nonNull)
                .toArray(Metadata.DataPair[]::new);

        if (dataPairs.length > 0) {
            packets.setMetadata(entityId, player, dataPairs);
        }
         */
    }

    public ADEntityDisplay create() {
        super.create();
        return this;
    }

    @Override
    public EntityType getEntityType() {
        return entityType;
    }

    @Override
    public void setEntityType(EntityType type) {
        if (config != null) {
            entitySection.set("type", type.name());
            save();
        }

        this.entityType = type;

        packets.removeEntity(entityId);
        plugin.getInteractionsManager().removeInteraction(getInteractionId());

        entity = packets.createEntity(type, location);
        entityId = entity.getEntityId();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            sendMetadataPackets(onlinePlayer);
        }

        plugin.getInteractionsManager().addInteraction(getInteractionId(), this);
    }

    @Override
    public <T> void setProperty(Property<T> property, T value) {
        if (!property.ownerEntity().isAssignableFrom(entity.getClass())) return;
        if (!isPropertyApplicable(property)) return;

        properties.put(property, value);
        ConfigurationSection dataSection = config.getSection("data", true, entitySection);
        Class<?> type = property.type();

        if (type.isEnum()) {
            dataSection.set(property.name(), ((Enum<?>) value).name());

        } else if (Keyed.class.isAssignableFrom(type)) {
            dataSection.set(property.name(), ((Keyed) value).getKey().getKey());

        } else if (ItemStack.class.isAssignableFrom(type)) {
            ConfigurationSection itemSection = dataSection.createSection(property.name());
            itemSection.set("material", ((ItemStack) value).getType().name());
            Utils.saveItemData((ItemStack) value, itemSection, plugin.getNmsVersion());

        } else {
            dataSection.set(property.name(), value);
        }

        save();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            setProperty(property, value, onlinePlayer);
        }
    }

    @Override
    public <T> void setProperty(Property<T> property, T value, Player player) {
        /* TODO: Fix implementation
        Metadata.DataPair<T> dataPair = plugin.metadata.createDataPair(properties, property, value);
        if (dataPair != null) packets.setMetadata(entityId, player, dataPair);
         */
    }

    @Override
    public <T> T getPropertyValue(Property<T> property) {
        return properties.containsKey(property) ? (T) properties.get(property) : property.getDefaultValue();
    }

    @Override
    public <T> Map<Property<T>, T> getProperties() {
        // The cast should not throw any errors because type safety is already ensured
        // in the setProperty method and the constructor.
        return (Map<Property<T>, T>) (Map<?, ?>) properties;
    }

    @Override
    public boolean isPropertyApplicable(Property<?> property) {
        return property.ownerEntity().isAssignableFrom(entity.getClass());
    }
}