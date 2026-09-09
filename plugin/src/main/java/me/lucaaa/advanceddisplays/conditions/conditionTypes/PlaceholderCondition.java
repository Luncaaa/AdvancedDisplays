package me.lucaaa.advanceddisplays.conditions.conditionTypes;

import me.clip.placeholderapi.PlaceholderAPI;
import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.BaseEntity;
import me.lucaaa.advanceddisplays.conditions.ADCondition;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class PlaceholderCondition extends ADCondition {
    private final AdvancedDisplays plugin;
    private final String condition;

    public PlaceholderCondition(AdvancedDisplays plugin, String condition) {
        this.plugin = plugin;
        this.condition = condition;
    }

    @Override
    public boolean meetsCondition(BaseEntity display, Player player) {
        int index = condition.indexOf(" == ");
        if (index == -1) {
            plugin.log(Level.WARNING, "Display " + display.getName() + " has an invalid placeholder condition - missing \" == \"");
            return true;
        }

        String placeholder = condition.substring(0, index);
        String value = condition.substring(index + 4); // 4 = length of " == "

        if (placeholder.equalsIgnoreCase("%player%")) {
            return value.equals(player.getName());
        }

        if (!plugin.isPapiInstalled()) {
            plugin.log(Level.WARNING, "Display " + display.getName() + " has a placeholder condition but PlaceholderAPI is not installed!");
            return true;
        }

        String parsed = PlaceholderAPI.setPlaceholders(player, placeholder);
        return parsed.equals(value);
    }
}