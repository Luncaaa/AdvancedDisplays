package me.lucaaa.advanceddisplays.conditions.conditionTypes;

import me.lucaaa.advanceddisplays.api.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.conditions.ADCondition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class LacksPermissionCondition extends ADCondition {
    private final String permission;

    public LacksPermissionCondition(ConfigurationSection section) {
        super(List.of("permission"), section);
        this.permission = section.getString("permission");
    }

    public LacksPermissionCondition(String permission) {
        super(List.of(), null);
        this.permission = permission;
    }

    @Override
    public boolean meetsCondition(BaseDisplay display, Player player) {
        if (permission.equalsIgnoreCase("none")) return true;
        return !player.hasPermission(permission);
    }
}