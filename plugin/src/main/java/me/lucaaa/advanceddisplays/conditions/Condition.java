package me.lucaaa.advanceddisplays.conditions;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class Condition {
    protected final AdvancedDisplays plugin;
    private final List<String> missingFields = new ArrayList<>();
    protected boolean isCorrect = true;

    public Condition(AdvancedDisplays plugin, List<String> requiredFields, ConfigurationSection section) {
        this.plugin = plugin;

        for (String requiredField : requiredFields) {
            if (section.get(requiredField) == null) {
                this.missingFields.add(requiredField);
            }
        }
    }

    /**
     * Checks whether a player meets the conditions or not.
     * @param player The player to check.
     * @return Whether the player meets the conditions or not.
     */
    public abstract boolean meetsConditions(Player player);

    public List<String> getMissingFields() {
        return missingFields;
    }

    public boolean isCorrect() {
        return isCorrect;
    }
}