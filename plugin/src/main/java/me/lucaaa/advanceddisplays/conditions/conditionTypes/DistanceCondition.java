package me.lucaaa.advanceddisplays.conditions.conditionTypes;

import me.lucaaa.advanceddisplays.api.displays.EntityDisplay;
import me.lucaaa.advanceddisplays.conditions.ADCondition;
import org.bukkit.entity.Player;

public class DistanceCondition extends ADCondition {
    private final double distance;

    public DistanceCondition(Object value) {
        this.distance = (double) value;
    }

    public DistanceCondition(double distance) {
        this.distance = distance;
    }

    @Override
    public boolean meetsCondition(EntityDisplay display, Player player) {
        return distance <= 0.0 || player.getLocation().distanceSquared(display.getLocation()) <= Math.pow(distance, 2);
    }
}