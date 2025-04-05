package me.lucaaa.advanceddisplays.conditions.conditionTypes;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.conditions.Condition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class DistanceCondition extends Condition {
    private final double distance;
    private final BaseDisplay display;

    public DistanceCondition(AdvancedDisplays plugin, ConfigurationSection section, BaseDisplay display) {
        super(plugin, List.of("distance"), section);

        this.distance = section.getDouble("distance");
        this.display = display;
    }

    @Override
    public boolean meetsConditions(Player player) {
        return distance <= 0.0 || player.getLocation().distanceSquared(display.getLocation()) <= Math.pow(distance, 2);
    }
}