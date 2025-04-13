package me.lucaaa.advanceddisplays.conditions;

import me.lucaaa.advanceddisplays.api.conditions.Condition;
import me.lucaaa.advanceddisplays.api.conditions.ConditionsFactory;
import me.lucaaa.advanceddisplays.conditions.conditionTypes.DistanceCondition;
import me.lucaaa.advanceddisplays.conditions.conditionTypes.HasPermissionCondition;
import me.lucaaa.advanceddisplays.conditions.conditionTypes.LacksPermissionCondition;

public class ADConditionsFactory implements ConditionsFactory {
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
}