package me.lucaaa.advanceddisplays.conditions.conditionTypes;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.conditions.Condition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class HasPermissionCondition extends Condition {
    private final String permission;

    public HasPermissionCondition(AdvancedDisplays plugin, ConfigurationSection section) {
        super(plugin, List.of("permission"), section);

        this.permission = section.getString("permission");
    }

    @Override
    public boolean meetsConditions(Player player) {
        if (permission.equalsIgnoreCase("none")) return true;
        return player.hasPermission(permission);
    }
}