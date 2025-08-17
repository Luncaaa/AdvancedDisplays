package me.lucaaa.advanceddisplays.actions;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.actions.actionTypes.ActionType;
import me.lucaaa.advanceddisplays.data.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class Action {
    protected final AdvancedDisplays plugin;
    protected final List<String> errors = new ArrayList<>();

    private final ActionType type;
    private final boolean isGlobal;
    private final int delay;
    private final boolean globalPlaceholders;

    public Action(AdvancedDisplays plugin, ActionType type, ConfigurationSection section, List<Field> requiredFields) {
        this.plugin = plugin;
        this.type = type;

        this.delay = section.getInt("delay", 0);
        this.isGlobal = section.getBoolean("global", false);
        this.globalPlaceholders = section.getBoolean("global-placeholders", true);

        for (Field field : requiredFields) {
            if (!section.contains(field.name)) {
                errors.add("Missing field \"" + field.name + "\" of type " + field.getTypesParsed());
                break;
            }

            Object value = Objects.requireNonNull(section.get(field.name));
            if (!field.isAssignable(value.getClass())) {
                errors.add("Invalid field type for field \"" + field.name + "\". It must be: " + field.getTypesParsed());
                break;
            }
        }
    }

    /* TODO: In-game editor
    public Action(AdvancedDisplays plugin, ActionType type, String name, boolean isGlobal) {
        this.plugin = plugin;
        this.type = type;
        this.name = name;
        this.isGlobal = isGlobal;
    }*/

    public ActionType getType() {
        return type;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * Runs the action for a specific player.
     * @param clickedPlayer The player who clicked the display.
     * @param actionPlayer Who to run the action for.
     */
    public abstract void runAction(Player clickedPlayer, Player actionPlayer);

    public int getDelay() {
        return delay;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public boolean useGlobalPlaceholders() {
        return globalPlaceholders;
    }

    public String getTextString(String message, Player clickedPlayer, Player actionPlayer) {
        return Utils.getTextString(message, clickedPlayer, actionPlayer, useGlobalPlaceholders());
    }

    public Component getText(String message, Player clickedPlayer, Player actionPlayer) {
        return Utils.getText(message, clickedPlayer, actionPlayer, useGlobalPlaceholders());
    }

    // TODO: In-game editor
    // public abstract void saveToConfig(ConfigurationSection section);

    public record Field(String name, Class<?>... types) {
        public String getTypesParsed() {
            return String.join(" OR ", Arrays.stream(types).map(Class::getSimpleName).toList());
        }

        public boolean isAssignable(Class<?> clazz) {
            for (Class<?> fieldType : types) {
                if (fieldType.isAssignableFrom(clazz)) return true;
            }
            return false;
        }
    }
}