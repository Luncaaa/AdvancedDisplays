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

        for (String clickTypeKey : actionsSection.getKeys(false)) {
            if (clickTypeKey.equalsIgnoreCase("ANY")) {
                for (ClickType clickType : ClickType.values()) {
                    this.addAction(clickType, actionsSection.getConfigurationSection(clickTypeKey));
                }

            } else if (clickTypeKey.contains(";")) {
                String[] clickTypes = clickTypeKey.split(";");
                for (String clickType : clickTypes) {
                    this.addAction(ClickType.valueOf(clickType), actionsSection.getConfigurationSection(clickTypeKey));
                }

            } else {
                this.addAction(ClickType.valueOf(clickTypeKey), actionsSection.getConfigurationSection(clickTypeKey));
            }
        }
    }

    public ActionsHandler() {
        this.isApiDisplay = true;
    }

    /**
     * Adds an action to the map.
     * @param clickType The click that should be used to execute the action.
     * @param actionsSection The section with the action data.
     */
    private void addAction(ClickType clickType, ConfigurationSection actionsSection) {
        if (actionsSection == null) return;

        for (Map.Entry<String, Object> actionsMap : actionsSection.getValues(false).entrySet()) {
            ConfigurationSection actionSection = (ConfigurationSection) actionsMap.getValue();
            ActionType actionType = ActionType.getFromConfigName(actionSection.getString("type"));

            if (actionType == null) {
                Logger.log(Level.WARNING, "Invalid action type detected in \"" + actionSection.getName() + "\" for click type " + clickType.name() + actionSection.getString("type"));
                continue;
            }

            Action action = switch (actionType) {
                case MESSAGE -> new MessageAction(actionSection);
                case CONSOLE_COMMAND -> new ConsoleCommandAction(actionSection);
                case PLAYER_COMMAND -> new PlayerCommandAction(actionSection);
                case TITLE -> new TitleAction(actionSection);
                case ACTIONBAR -> new ActionbarAction(actionSection);
                case PLAY_SOUND -> new SoundAction(actionSection);
            };

            if (!action.isFormatCorrect()) {
                String missingFields = String.join(", ", action.getMissingFields());
                Logger.log(Level.WARNING, "Your action \"" + actionSection.getName() + "\" is missing necessary fields: " + missingFields);
                continue;
            }

            this.actionsMap.computeIfAbsent(clickType, k -> new ArrayList<>());
            this.actionsMap.get(clickType).add(action);
        }
    }

    public void runActions(Player player, ClickType clickType, ADBaseDisplay display) {
        if (this.isApiDisplay && this.clickActions != null) {
            this.clickActions.onClick(player, clickType, display);

        } else {
            ArrayList<Action> actionsToRun = this.actionsMap.get(clickType);
            if (actionsToRun == null) return;

            for (Action action : actionsToRun) {
                if (action.isGlobal()) {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        this.executeAction(action, player, onlinePlayer);
                    }
                } else {
                    this.executeAction(action, player, player);
                }
            }
        }
    }

    /**
     * Runs the action for a specific player.
     * @param action The action to run.
     * @param clickedPlayer The player who clicked the display.
     * @param actionPlayer Who to run the action for.
     */
    public void executeAction(Action action, Player clickedPlayer, Player actionPlayer) {
        if (action.getDelay() > 0) {
            Bukkit.getScheduler().runTaskLater(AdvancedDisplays.getPlugin(), () -> action.runAction(clickedPlayer, actionPlayer), action.getDelay());
        } else {
            action.runAction(clickedPlayer, actionPlayer);
        }
    }

    public void setClickActions(DisplayActions clickActions) {
        this.clickActions = clickActions;
    }
}
