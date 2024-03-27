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
                    boolean success = this.addAction(clickType, actionsSection.getConfigurationSection(clickTypesSection));
                    if (!success) break;
                }

            } else if (clickTypesSection.contains(";")) {
                String[] clickTypes = clickTypesSection.split(";");
                for (String clickType : clickTypes) {
                    boolean success = this.addAction(ClickType.valueOf(clickType), actionsSection.getConfigurationSection(clickTypesSection));
                    if (!success) break;
                }

            } else {
                this.addAction(ClickType.valueOf(clickTypesSection), actionsSection.getConfigurationSection(clickTypesSection));
            }
        }
    }

    /**
     * Adds an action to the map.
     * @param clickType The click that should be used to execute the action.
     * @param actionsSection The section with the action data.
     * @return True if the action exists, its format is correct and could be added. False otherwise.
     */
    private boolean addAction(ClickType clickType, ConfigurationSection actionsSection) {
        if (actionsSection == null) return false;

        for (Map.Entry<String, Object> actionsMap : actionsSection.getValues(false).entrySet()) {
            ConfigurationSection actionSection = (ConfigurationSection) actionsMap.getValue();
            ActionType actionType = ActionType.getFromConfigName(actionSection.getString("type"));

            if (actionType == null) {
                Logger.log(Level.WARNING, "Invalid action type detected in \"" + actionSection.getName() + "\": " + actionSection.getString("type"));
                return false;
            }

            Action action = switch (actionType) {
                case MESSAGE -> new MessageAction(actionSection);
                case CONSOLE_COMMAND -> new ConsoleCommandAction(actionSection);
                case PLAYER_COMMAND -> new PlayerCommandAction(actionSection);
                case TITLE -> new TitleAction(actionSection);
                case ACTIONBAR -> new ActionbarAction(actionSection);
                case PLAY_SOUND -> new SoundAction(actionSection);
            };

            if (!action.isFormatCorrect()) return false;

            this.actionsMap.computeIfAbsent(clickType, k -> new ArrayList<>());
            this.actionsMap.get(clickType).add(action);
        }

        return true;
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
