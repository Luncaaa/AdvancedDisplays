package me.lucaaa.advanceddisplays.conditions.conditionTypes;

import me.lucaaa.advanceddisplays.api.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.conditions.ADCondition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class DistanceCondition extends ADCondition {
    private final double distance;

    public DistanceCondition(ConfigurationSection section) {
        super(List.of("distance"), section);
        this.distance = section.getDouble("distance");
    }

    public DistanceCondition(double distance) {
        super(List.of(), null);
        this.distance = distance;
    }

    @Override
    public boolean meetsCondition(BaseDisplay display, Player player) {
        return distance <= 0.0 || player.getLocation().distanceSquared(display.getLocation()) <= Math.pow(distance, 2);
    }
}