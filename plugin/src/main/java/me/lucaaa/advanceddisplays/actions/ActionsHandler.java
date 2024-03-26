package me.lucaaa.advanceddisplays.actions;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.actions.actionTypes.*;
import me.lucaaa.advanceddisplays.api.actions.DisplayActions;
import me.lucaaa.advanceddisplays.api.actions.ClickType;
import me.lucaaa.advanceddisplays.common.utils.Logger;
import me.lucaaa.advanceddisplays.displays.ADBaseDisplay;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;

public class ActionsHandler {
    private final Map<ClickType, ArrayList<Action>> actionsMap = new EnumMap<>(ClickType.class);
    private DisplayActions clickActions = null;
    private final boolean isApiDisplay;

    public ActionsHandler(YamlConfiguration config) {
        this.isApiDisplay = false;

        ConfigurationSection actionsSection = config.getConfigurationSection("actions");
        if (actionsSection == null) return;

        for (String clickTypesSection : actionsSection.getKeys(false)) {
            if (clickTypesSection.equalsIgnoreCase("ANY")) {
                for (ClickType clickType : ClickType.values()) {
                    this.addAction(clickType, actionsSection.getConfigurationSection(clickTypesSection));
                }

            } else if (clickTypesSection.contains(";")) {
                String[] clickTypes = clickTypesSection.split(";");
                for (String clickType : clickTypes) {
                    this.addAction(ClickType.valueOf(clickType), actionsSection.getConfigurationSection(clickTypesSection));
                }

            } else {
                this.addAction(ClickType.valueOf(clickTypesSection), actionsSection.getConfigurationSection(clickTypesSection));
            }
        }
    }

    private void addAction(ClickType clickType, ConfigurationSection actionsSection) {
        if (actionsSection == null) return;

        for (Map.Entry<String, Object> actionsMap : actionsSection.getValues(false).entrySet()) {
            ConfigurationSection actionSection = (ConfigurationSection) actionsMap.getValue();
            ActionType actionType = ActionType.getFromConfigName(actionSection.getString("type"));
            String value = actionSection.getString("value");
            int delay = actionSection.getInt("delay", 0); // If delay is not set, it will be 0 by default.

            if (actionType == null) {
                Logger.log(Level.WARNING, "Invalid action type detected in \"" + actionSection.getName() + "\": " + actionSection.getString("type"));
                return;
            }

            Action action = null;
            switch (actionType) {
                case MESSAGE -> action = new MessageAction(value, delay);
                case CONSOLE_COMMAND -> action = new ConsoleCommandAction(value, delay);
                case PLAYER_COMMAND -> action = new PlayerCommandAction(value, delay);
                case TITLE -> action = new TitleAction(actionSection, delay);
            }

            this.actionsMap.computeIfAbsent(clickType, k -> new ArrayList<>());
            this.actionsMap.get(clickType).add(action);
        }
    }

    public ActionsHandler() {
        this.isApiDisplay = true;
    }

    public void runActions(Player player, ClickType clickType, ADBaseDisplay display) {
        if (this.isApiDisplay && this.clickActions != null) {
            this.clickActions.onClick(player, clickType, display);

        } else {
            ArrayList<Action> actionsToRun = this.actionsMap.get(clickType);
            if (actionsToRun == null) return;

            for (Action action : actionsToRun) {
                if (action.getDelay() > 0) {
                    Bukkit.getScheduler().runTaskLater(AdvancedDisplays.getPlugin(), () -> action.runAction(player), action.getDelay());
                } else {
                    action.runAction(player);
                }
            }
        }
    }

    public void setClickActions(DisplayActions clickActions) {
        this.clickActions = clickActions;
    }
}
