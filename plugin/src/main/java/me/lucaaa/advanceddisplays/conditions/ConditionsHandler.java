package me.lucaaa.advanceddisplays.conditions;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.conditions.conditionTypes.ConditionType;
import me.lucaaa.advanceddisplays.conditions.conditionTypes.DistanceCondition;
import me.lucaaa.advanceddisplays.conditions.conditionTypes.HasPermissionCondition;
import me.lucaaa.advanceddisplays.conditions.conditionTypes.LacksPermissionCondition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ConditionsHandler {
    private final AdvancedDisplays plugin;
    private final BaseDisplay display;
    private final List<Condition> conditionsList = new ArrayList<>();

    public ConditionsHandler(AdvancedDisplays plugin, BaseDisplay display, ConfigurationSection conditionsSection) {
        this.plugin = plugin;
        this.display = display;

        if (conditionsSection != null) {
            for (String conditionTypeKey : conditionsSection.getKeys(false)) {
                addCondition(conditionsSection.getConfigurationSection(conditionTypeKey));
            }
        }
    }

    /**
     * Adds a condition to the list.
     * @param conditionSection The section with the condition data.
     */
    private void addCondition(ConfigurationSection conditionSection) {
        if (conditionSection == null) return;

        ConditionType conditionType = ConditionType.getFromConfigName(conditionSection.getName());

        if (conditionType == null) {
            plugin.log(Level.WARNING, "Invalid condition type detected for display \"" + display.getName() + "\": " + conditionSection.getName());
            return;
        }

        Condition condition = switch (conditionType) {
            case DISTANCE -> new DistanceCondition(plugin, conditionSection, display);
            case HAS_PERMISSION -> new HasPermissionCondition(plugin, conditionSection);
            case LACKS_PERMISSION -> new LacksPermissionCondition(plugin, conditionSection);
        };

        List<String> missingFields = condition.getMissingFields();
        if (!missingFields.isEmpty()) {
            String missing = String.join(", ", missingFields);
            plugin.log(Level.WARNING, "Your condition \"" + conditionSection.getName() + "\" is missing necessary fields: " + missing);
            return;
        }

        // The reason why it isn't correct is handled by the action class.
        if (!condition.isCorrect()) return;

        conditionsList.add(condition);
    }

    /**
     * Check the conditions for a player.
     * @param player The player for whom to check the conditions.
     * @return Whether the player meets all the conditions or not.
     */
    public boolean checkConditions(Player player) {
        for (Condition condition : conditionsList) {
            if (!condition.meetsConditions(player)) return false;
        }

        return true;
    }
}