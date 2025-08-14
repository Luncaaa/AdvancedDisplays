package me.lucaaa.advanceddisplays.conditions.conditionTypes;

import me.lucaaa.advanceddisplays.api.displays.BaseEntity;
import me.lucaaa.advanceddisplays.conditions.ADCondition;
import org.bukkit.entity.Player;

public class DistanceCondition extends ADCondition {
    private final double distanceSquared;

    public DistanceCondition(double distance) {
        this.distanceSquared = Math.pow(distance, 2);
    }

    @Override
    public boolean meetsCondition(BaseEntity display, Player player) {
        return distanceSquared <= 0.0 || player.getLocation().distanceSquared(display.getLocation()) <= distanceSquared;
    }
}