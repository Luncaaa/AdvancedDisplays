package me.lucaaa.advanceddisplays.conditions;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.conditions.Condition;
import me.lucaaa.advanceddisplays.api.displays.BaseEntity;
import me.lucaaa.advanceddisplays.conditions.conditionTypes.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ConditionsHandler {
    private final AdvancedDisplays plugin;
    private final BaseEntity display;
    private final List<Condition> conditionsList = new ArrayList<>();

    public ConditionsHandler(AdvancedDisplays plugin, BaseEntity display, ConfigurationSection conditionsSection) {
        this.plugin = plugin;
        this.display = display;

        for (String conditionTypeKey : conditionsSection.getKeys(false)) {
            addCondition(conditionTypeKey, conditionsSection.get(conditionTypeKey));
        }
    }

    public ConditionsHandler(AdvancedDisplays plugin, BaseEntity display) {
        this.plugin = plugin;
        this.display = display;
    }

    /**
     * Adds a condition to the list.
     * @param key The type of the condition
     * @param value The condition's value
     */
    private void addCondition(String key, Object value) {
        ConditionType conditionType = ConditionType.getFromConfigName(key);

        if (conditionType == null) {
            plugin.log(Level.WARNING, "Invalid condition type detected for display \"" + display.getName() + "\": " + key);
            return;
        }

        if (!value.getClass().equals(conditionType.getType())) {
            plugin.log(Level.WARNING, "Your condition \"" + key + "\" is not a valid " + conditionType.getType().getSimpleName() + " for display \"" + display.getName() + "\"!");
            return;
        }

        ADCondition condition = switch (conditionType) {
            case DISTANCE -> new DistanceCondition((double) value);
            case HAS_PERMISSION -> new HasPermissionCondition((String) value);
            case LACKS_PERMISSION -> new LacksPermissionCondition((String) value);
        };

        // The reason why it isn't correct is handled by the action class.
        if (!condition.isCorrect()) return;

        conditionsList.add(condition);
    }

    public void addCondition(Condition condition) {
        conditionsList.add(condition);
    }

    public void clearConditions() {
        conditionsList.clear();
    }

    /**
     * Check the conditions for a player.
     * @param player The player for whom to check the conditions.
     * @return Whether the player meets all the conditions or not.
     */
    public boolean checkConditions(Player player) {
        for (Condition condition : conditionsList) {
            if (!condition.meetsCondition(display, player)) return false;
        }

        return true;
    }
}