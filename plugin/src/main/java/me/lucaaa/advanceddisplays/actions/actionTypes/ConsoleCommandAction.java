package me.lucaaa.advanceddisplays.actions.actionTypes;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.actions.Action;
import me.lucaaa.advanceddisplays.api.displays.BaseEntity;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class ConsoleCommandAction extends Action {
    private final List<String> commands;

    public ConsoleCommandAction(AdvancedDisplays plugin, ConfigurationSection actionSection) {
        super(
                plugin,
                ActionType.CONSOLE_COMMAND,
                actionSection,
                List.of(
                        new Field("command", String.class, List.class)
                )
        );

        if (actionSection.isList("command")) {
            this.commands = actionSection.getStringList("command");

        } else {
            this.commands = List.of(actionSection.getString("command", ""));
        }
    }

    @Override
    public void runAction(Player clickedPlayer, Player actionPlayer, BaseEntity display) {
        for (String command : commands) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), getTextString(command, clickedPlayer, actionPlayer));
        }
    }
}