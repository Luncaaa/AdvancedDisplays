package me.lucaaa.advanceddisplays.actions.actionTypes;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.actions.Action;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerCommandAction extends Action {
    private final List<String> commands;

    public PlayerCommandAction(AdvancedDisplays plugin, ConfigurationSection actionSection) {
        super(
                plugin,
                ActionType.PLAYER_COMMAND,
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
    public void runAction(Player clickedPlayer, Player actionPlayer) {
        for (String command : commands) {
            actionPlayer.performCommand(getTextString(command, clickedPlayer, actionPlayer));
        }
    }
}