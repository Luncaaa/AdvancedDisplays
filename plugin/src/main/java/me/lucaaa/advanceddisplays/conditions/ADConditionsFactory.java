package me.lucaaa.advanceddisplays.conditions;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.conditions.Condition;
import me.lucaaa.advanceddisplays.api.conditions.ConditionsFactory;
import me.lucaaa.advanceddisplays.conditions.conditionTypes.DistanceCondition;
import me.lucaaa.advanceddisplays.conditions.conditionTypes.HasPermissionCondition;
import me.lucaaa.advanceddisplays.conditions.conditionTypes.LacksPermissionCondition;
import me.lucaaa.advanceddisplays.conditions.conditionTypes.PlaceholderCondition;

public class ADConditionsFactory implements ConditionsFactory {
    private final AdvancedDisplays plugin;

    public ADConditionsFactory(AdvancedDisplays plugin) {
        this.plugin = plugin;
    }

    @Override
    public Condition distance(double distance) {
        return new DistanceCondition(distance);
    }

    @Override
    public Condition hasPermission(String permission) {
        return new HasPermissionCondition(permission);
    }

    @Override
    public Condition lacksPermission(String permission) {
        return new LacksPermissionCondition(permission);
    }

    @Override
    public Condition placeholder(String input) {
        return new PlaceholderCondition(plugin, input);
    }
}