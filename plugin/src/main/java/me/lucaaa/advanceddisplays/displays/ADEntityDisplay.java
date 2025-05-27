package me.lucaaa.advanceddisplays.displays;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.EntityDisplay;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.managers.DisplaysManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class ADEntityDisplay extends ADBaseEntity implements EntityDisplay {
    public ADEntityDisplay(AdvancedDisplays plugin, DisplaysManager displaysManager, ConfigManager config, String name, EntityType entityType) {
        super(plugin, displaysManager, config, name, DisplayType.ENTITY, entityType);
    }

    public ADEntityDisplay(AdvancedDisplays plugin, DisplaysManager displaysManager, String name, Location location, EntityType entityType, boolean saveToConfig) {
        super(plugin, displaysManager, name, DisplayType.ENTITY, entityType, location, saveToConfig);
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

        packets.removeEntity(entityId);
        plugin.getInteractionsManager().removeInteraction(getInteractionId());

        entity = packets.createEntity(type, location);
        entityId = entity.getEntityId();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            sendMetadataPackets(onlinePlayer);
        }

        plugin.getInteractionsManager().addInteraction(getInteractionId(), this);

        this.entityType = type;
    }
}