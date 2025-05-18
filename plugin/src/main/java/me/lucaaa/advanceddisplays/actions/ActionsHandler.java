package me.lucaaa.advanceddisplays.actions;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.actions.actionTypes.*;
import me.lucaaa.advanceddisplays.api.actions.DisplayActions;
import me.lucaaa.advanceddisplays.api.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.common.utils.Utils;
import me.lucaaa.advanceddisplays.conditions.ConditionsHandler;
import me.lucaaa.advanceddisplays.displays.ADBaseDisplay;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ActionsHandler {
    private final AdvancedDisplays plugin;
    private final ConditionsHandler conditionsHandler;
    private final String conditionsNotMetMessage;
    private final Map<ClickType, List<Action>> actionsMap = new EnumMap<>(ClickType.class);
    private DisplayActions clickActions = null;

    public ActionsHandler(AdvancedDisplays plugin, BaseDisplay display, YamlConfiguration config) {
        this.plugin = plugin;

        if (config == null) {
            this.conditionsHandler = null;
            this.conditionsNotMetMessage = null;
            return;
        }

        ConfigurationSection actionsSection = config.getConfigurationSection("actions");
        if (actionsSection == null) {
            this.conditionsHandler = null;
            this.conditionsNotMetMessage = null;
            return;
        } else {
            this.conditionsHandler = new ConditionsHandler(plugin, display, actionsSection.getConfigurationSection("conditions"));
            this.conditionsNotMetMessage = actionsSection.getString("conditions-not-met", null);
        }

        List<ClickType> validClickTypes = List.of(ClickType.LEFT, ClickType.RIGHT, ClickType.SHIFT_LEFT, ClickType.SHIFT_RIGHT);
        for (String clickTypeKey : actionsSection.getKeys(false)) {
            if (clickTypeKey.equalsIgnoreCase("ANY")) {
                for (ClickType clickType : validClickTypes) {
                    addAction(clickType, actionsSection.getConfigurationSection(clickTypeKey));
                }

            } else if (clickTypeKey.contains(";")) {
                String[] clickTypes = clickTypeKey.split(";");
                for (String clickType : clickTypes) {
                    try {
                        addAction(ClickType.valueOf(clickType), actionsSection.getConfigurationSection(clickTypeKey));
                    } catch (IllegalArgumentException e) {
                        plugin.log(Level.WARNING, "Invalid click type found for display \"" + display.getName() + "\": " + clickType);
                    }
                }

            } else {
                try {
                    addAction(ClickType.valueOf(clickTypeKey), actionsSection.getConfigurationSection(clickTypeKey));
                } catch (IllegalArgumentException e) {
                    if (!clickTypeKey.equals("conditions") && !clickTypeKey.equals("conditions-not-met")) {
                        plugin.log(Level.WARNING, "Invalid click type found for display \"" + display.getName() + "\": " + clickTypeKey);
                    }
                }
            }
        }
    }

    /**
     * Adds an action to the map.
     * @param clickType The click that should be used to execute the action.
     * @param actionsSection The section with the action data.
     */
    private void addAction(ClickType clickType, ConfigurationSection actionsSection) {
        if (actionsSection == null) return;

        for (Map.Entry<String, Object> actions : actionsSection.getValues(false).entrySet()) {
            ConfigurationSection actionSection = (ConfigurationSection) actions.getValue();
            ActionType actionType = ActionType.getFromConfigName(actionSection.getString("type"));

            if (actionType == null) {
                plugin.log(Level.WARNING, "Invalid action type detected in \"" + actionSection.getName() + "\" for click type " + clickType.name() + actionSection.getString("type"));
                continue;
            }

            Action action = switch (actionType) {
                case MESSAGE -> new MessageAction(plugin, actionSection);
                case CONSOLE_COMMAND -> new ConsoleCommandAction(plugin, actionSection);
                case PLAYER_COMMAND -> new PlayerCommandAction(plugin, actionSection);
                case TITLE -> new TitleAction(plugin, actionSection);
                case ACTIONBAR -> new ActionbarAction(plugin, actionSection);
                case PLAY_SOUND -> new SoundAction(plugin, actionSection);
                case EFFECT -> new EffectAction(plugin, actionSection);
                case TOAST -> new ToastAction(plugin, actionSection);
            };

            List<String> missingFields = action.getMissingFields();
            if (!missingFields.isEmpty()) {
                String missing = String.join(", ", missingFields);
                plugin.log(Level.WARNING, "Your action \"" + actionSection.getName() + "\" is missing necessary fields: " + missing);
                continue;
            }

            // The reason why it isn't correct is handled by the action class.
            if (!action.isCorrect()) continue;

            actionsMap.computeIfAbsent(clickType, k -> new ArrayList<>());
            actionsMap.get(clickType).add(action);
        }
    }

    public void runActions(Player player, ClickType clickType, ADBaseDisplay display) {
        if (clickActions != null) {
            clickActions.onClick(player, clickType, display);
            return;
        }

        if (display.isApi() || conditionsHandler == null) return;

        boolean meetsConditions = conditionsHandler.checkConditions(player);

        if (!meetsConditions) {
            if (conditionsNotMetMessage != null && !conditionsNotMetMessage.isBlank()) {
                player.spigot().sendMessage(Utils.getTextComponent(conditionsNotMetMessage, player, null, false));
            }
            return;
        }

        List<Action> actionsToRun = actionsMap.get(clickType);
        if (actionsToRun == null) return;

        for (Action action : actionsToRun) {
            if (action.isGlobal()) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    executeAction(action, player, onlinePlayer);
                }
            } else {
                executeAction(action, player, player);
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
            Bukkit.getScheduler().runTaskLater(plugin, () -> action.runAction(clickedPlayer, actionPlayer), action.getDelay());
        } else {
            action.runAction(clickedPlayer, actionPlayer);
        }
    }

    public void setClickActions(DisplayActions clickActions) {
        this.clickActions = clickActions;
    }
}
