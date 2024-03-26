package me.lucaaa.advanceddisplays.actions;

import me.lucaaa.advanceddisplays.common.utils.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.logging.Level;

public abstract class Action {
    private final int delay;

    public Action(int delay) {
        this.delay = delay;
    }

    public Action(int delay, List<String> requiredFields, ConfigurationSection section) {
        for (String requiredField : requiredFields) {
            if (section.get(requiredField) == null) {
                Logger.log(Level.WARNING, "Your action \"" + section.getName() + "\" is missing a necessary field: " + requiredField);
            }
        }
        this.delay = delay;
    }

    public abstract void runAction(Player player);

    public int getDelay() {
        return this.delay;
    }
}
