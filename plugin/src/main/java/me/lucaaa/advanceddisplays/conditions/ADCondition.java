package me.lucaaa.advanceddisplays.conditions;

import me.lucaaa.advanceddisplays.api.conditions.Condition;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public abstract class ADCondition implements Condition {
    private final List<String> missingFields = new ArrayList<>();
    protected boolean isCorrect = true;

    public ADCondition(List<String> requiredFields, ConfigurationSection section) {
        for (String requiredField : requiredFields) {
            if (section.get(requiredField) == null) {
                this.missingFields.add(requiredField);
            }
        }
    }

    public List<String> getMissingFields() {
        return missingFields;
    }

    public boolean isCorrect() {
        return isCorrect;
    }
}