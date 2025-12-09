package me.lucaaa.advanceddisplays.actions;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.actions.actionTypes.*;
import me.lucaaa.advanceddisplays.api.actions.DisplayActions;
import me.lucaaa.advanceddisplays.api.displays.BaseEntity;
import me.lucaaa.advanceddisplays.data.Utils;
import me.lucaaa.advanceddisplays.conditions.ConditionsHandler;
import me.lucaaa.advanceddisplays.displays.ADBaseEntity;
import me.lucaaa.advanceddisplays.managers.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;
import java.util.logging.Level;

public class ActionsHandler {
    private final AdvancedDisplays plugin;
    private final BaseEntity display;
    private final ConditionsHandler conditionsHandler;
    private final String conditionsNotMetMessage;
    private final Map<ClickType, List<Action>> actionsMap = new EnumMap<>(ClickType.class);
    private DisplayActions clickActions = null;

    public ActionsHandler(AdvancedDisplays plugin, BaseEntity display, ConfigManager configManager) {
        this.plugin = plugin;
        this.display = display;

        if (configManager == null) {
            this.conditionsHandler = new ConditionsHandler(plugin, display);
            this.conditionsNotMetMessage = null;
            return;
        }

        ConfigurationSection actionsSection = configManager.getSection("actions", false, configManager.getConfig());
        if (actionsSection == null) {
            this.conditionsHandler = new ConditionsHandler(plugin, display);
            this.conditionsNotMetMessage = null;
            return;
        }

        ConfigurationSection conditionsSection = configManager.getSection("conditions", false, actionsSection);
        this.conditionsHandler = (conditionsSection == null) ? null : new ConditionsHandler(plugin, display, conditionsSection);
        this.conditionsNotMetMessage = actionsSection.getString("conditions-not-met", null);

        List<ClickType> validClickTypes = List.of(ClickType.LEFT, ClickType.RIGHT, ClickType.SHIFT_LEFT, ClickType.SHIFT_RIGHT);
        for (String clickTypeKey : actionsSection.getKeys(false)) {
            if (!actionsSection.isConfigurationSection(clickTypeKey) || clickTypeKey.equalsIgnoreCase("conditions")) continue;

            ConfigurationSection actionSection = Objects.requireNonNull(actionsSection.getConfigurationSection(clickTypeKey));
            List<Action> validActions = getActions(actionSection);

            if (clickTypeKey.equalsIgnoreCase("ANY")) {
                for (ClickType clickType : validClickTypes) {
                    addActions(clickType, validActions);
                }

            } else if (clickTypeKey.contains(";")) {
                String[] clickTypes = clickTypeKey.split(";");
                for (String clickType : clickTypes) {
                    try {
                        addActions(ClickType.valueOf(clickType), validActions);
                    } catch (IllegalArgumentException e) {
                        plugin.log(Level.WARNING, "Invalid click type found for display \"" + display.getName() + "\": " + clickType);
                    }
                }

            } else {
                try {
                    addActions(ClickType.valueOf(clickTypeKey), validActions);
                } catch (IllegalArgumentException e) {
                    plugin.log(Level.WARNING, "Invalid click type found for display \"" + display.getName() + "\": " + clickTypeKey);
                }
            }
        }
    }

    /**
     * Adds actions to the map.
     * @param clickType The type.
     * @param actions The actions.
     */
    private void addActions(ClickType clickType, List<Action> actions) {
        actionsMap.computeIfAbsent(clickType, k -> new ArrayList<>());
        actionsMap.get(clickType).addAll(actions);
    }

    /**
     * Creates all the actions defined in a configuration section.
     * @param actionsSection The configuration section.
     * @return All created actions (those without errors)
     */
    private List<Action> getActions(ConfigurationSection actionsSection) {
        List<Action> validActions = new ArrayList<>();
        for (Map.Entry<String, Object> actions : actionsSection.getValues(false).entrySet()) {
            ConfigurationSection actionSection = (ConfigurationSection) actions.getValue();

            Action action = createAction(actions.getKey(), actionSection);
            if (action != null) {
                validActions.add(action);
            }
        }

        return validActions;
    }

    /**
     * Creates an action from a configuration section.
     * @param name The action's name.
     * @param actionSection The action's section.
     * @return The created action.
     */
    private Action createAction(String name, ConfigurationSection actionSection) {
        String typeName = actionSection.getString("type");

        if (typeName == null) {
            plugin.log(Level.WARNING, "Action \"" + name + "\" for display " + display.getName() + " does not have a type set!");
            return null;
        }

        ActionType actionType = ActionType.getFromConfigName(actionSection.getString("type"));

        if (actionType == null) {
            plugin.log(Level.WARNING, "Action \"" + name + "\" for display " + display.getName() + " does not have a type set: " + typeName);
            return null;
        }

        Action action = switch (actionType) {
            case MESSAGE -> new MessageAction(plugin, actionSection);
            case CONSOLE_COMMAND -> new ConsoleCommandAction(plugin, actionSection);
            case PLAYER_COMMAND -> new PlayerCommandAction(plugin, actionSection);
            case TITLE -> new TitleAction(plugin, actionSection);
            case ACTIONBAR -> new ActionbarAction(plugin, actionSection);
            case PLAY_SOUND -> new SoundAction(plugin, actionSection);
            case EFFECT -> new EffectAction(plugin, actionSection);
            case TOAST -> new ToastAction(plugin, actionSection, display);
            case PARTICLE -> new ParticleAction(plugin, actionSection);
        };

        if (action.hasErrors()) {
            plugin.log(Level.WARNING, "=== Found errors for action \"" + name + "\" ===");
            for (String error : action.getErrors()) {
                plugin.log(Level.WARNING, " - " + error);
            }
            return null;
        }

        return action;
    }

    public void runActions(Player player, ClickType clickType, ADBaseEntity display) {
        if (clickActions != null) {
            clickActions.onClick(player, clickType, display);
            return;
        }

        if (display.isApi()) return;

        boolean meetsConditions = conditionsHandler == null || conditionsHandler.checkConditions(player);

        if (!meetsConditions) {
            if (conditionsNotMetMessage != null && !conditionsNotMetMessage.isBlank()) {
                plugin.getAudience(player).sendMessage(Utils.getText(conditionsNotMetMessage, player, null, false));
            }
            return;
        }

        List<Action> actionsToRun = actionsMap.get(clickType);
        if (actionsToRun == null) return;

        for (Action action : actionsToRun) {
            if (action.getDelay() > 0) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> executeAction(action, player), action.getDelay());
            } else {
                executeAction(action, player);
            }
        }
    }

    /**
     * Runs the action for a specific player.
     * @param action The action to run.
     * @param clickedPlayer The player who clicked the display.
     */
    public void executeAction(Action action, Player clickedPlayer) {
        if (action.isGlobal()) {
            for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                action.run(clickedPlayer, onlinePlayer, display);
            }
        } else {
            action.run(clickedPlayer, clickedPlayer, display);
        }
    }

    public void setClickActions(DisplayActions clickActions) {
        this.clickActions = clickActions;
    }
}