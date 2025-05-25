package me.lucaaa.advanceddisplays.conditions.conditionTypes;

import me.lucaaa.advanceddisplays.api.displays.EntityDisplay;
import me.lucaaa.advanceddisplays.conditions.ADCondition;
import org.bukkit.entity.Player;

public class LacksPermissionCondition extends ADCondition {
    private final String permission;

    public LacksPermissionCondition(Object permission) {
        this.permission = (String) permission;
    }

    public LacksPermissionCondition(String permission) {
        this.permission = permission;
    }

    @Override
    public boolean meetsCondition(EntityDisplay display, Player player) {
        if (permission.equalsIgnoreCase("none")) return true;
        return !player.hasPermission(permission);
    }
}