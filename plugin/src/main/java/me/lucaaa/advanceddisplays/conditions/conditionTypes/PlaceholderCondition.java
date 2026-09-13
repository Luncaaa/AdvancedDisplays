package me.lucaaa.advanceddisplays.conditions.conditionTypes;

import me.clip.placeholderapi.PlaceholderAPI;
import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.BaseEntity;
import me.lucaaa.advanceddisplays.conditions.ADCondition;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderCondition extends ADCondition {
    private final AdvancedDisplays plugin;
    private final List<Condition> conditions = new ArrayList<>();

    // If changed in a future, update the wiki too.
    // Matches the format <placeholder> <comparison> <value>, where value can or cannot be between simple quotes and comparison must be ==, !=, <(=) or >(=)
    private static final Pattern CONDITION_PATTERN = Pattern.compile("(%[a-zA-Z0-9_]+%)\\s*(==|!=|<=|>=|<|>)\\s*(?:'([^']*)'|(\\S+))");

    public PlaceholderCondition(AdvancedDisplays plugin, String input) {
        this.plugin = plugin;

        Matcher matcher = CONDITION_PATTERN.matcher(input);

        while (matcher.find()) {
            String placeholder = matcher.group(1);
            if (!placeholder.equalsIgnoreCase("%player%") && !plugin.isPapiInstalled()) {
                plugin.log(Level.WARNING, "A display has a placeholder condition other than \"%player%\" but PlaceholderAPI is not installed!");
                isCorrect = false;
            }

            String operator = matcher.group(2);
            Comparison comparison = Comparison.getComparison(operator);
            if (comparison == null) {
                plugin.log(Level.WARNING, "Invalid placeholder condition comparison type: " +  operator);
                isCorrect = false;
                comparison = Comparison.EQUALS;
            }

            // matcher.group(3) is inside simple quotes, matcher.group(4) is without them.
            String value = matcher.group(3) != null ? matcher.group(3) : matcher.group(4);

            if (comparison.numberOnly) {
                try {
                    Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    plugin.log(Level.WARNING, "Non-number value provided for a number-only comparison type in a placeholder condition: " +  value);
                    isCorrect = false;
                }
            }

            conditions.add(new Condition(placeholder, comparison, value));
        }
    }

    @Override
    public boolean meetsCondition(BaseEntity display, Player player) {
        for (Condition condition : conditions) {
            String placeholder = condition.placeholder;

            if (placeholder.equalsIgnoreCase("%player%")) {
                placeholder = player.getName();
            } else if (plugin.isPapiInstalled()) {
                placeholder = PlaceholderAPI.setPlaceholders(player, placeholder);
            }

            String value = condition.value;
            double numValue = 0.0;
            double plValue = 0.0;

            if (condition.comparison.numberOnly) {
                try {
                    numValue = Double.parseDouble(value);
                    plValue = Double.parseDouble(placeholder);
                } catch (NumberFormatException e) {
                    plugin.log(Level.WARNING, "Display " + display.getName() + " has a non-number placeholder or value provided for a number-only comparison type in a placeholder condition: " +  value);
                    return false;
                }
            }

            boolean matches = switch (condition.comparison) {
                case EQUALS -> placeholder.equals(value);
                case NOT_EQUALS -> !placeholder.equals(value);
                case LOWER -> plValue < numValue;
                case LOWER_OR_EQUAL -> plValue <= numValue;
                case GREATER -> plValue > numValue;
                case GREATER_OR_EQUAL -> plValue >= numValue;
            };

            if (!matches) return false;
        }

        return true;
    }

    private record Condition(String placeholder, Comparison comparison, String value) {}

    private enum Comparison {
        EQUALS("==", false),
        NOT_EQUALS("!=", false),
        LOWER("<", true),
        LOWER_OR_EQUAL("<=", true),
        GREATER(">", true),
        GREATER_OR_EQUAL(">=", true);

        private final String value;
        private final boolean numberOnly;

        Comparison(String value, boolean numberOnly) {
            this.value = value;
            this.numberOnly = numberOnly;
        }

        public static Comparison getComparison(String value) {
            for (Comparison comparison : values()) {
                if (comparison.value.equals(value)) return comparison;
            }

            return null;
        }
    }
}