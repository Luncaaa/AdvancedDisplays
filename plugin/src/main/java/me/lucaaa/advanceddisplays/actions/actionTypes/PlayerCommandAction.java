package me.lucaaa.advanceddisplays.actions.actionTypes;

import me.lucaaa.advanceddisplays.actions.Action;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerCommandAction extends Action {
    private final List<String> commands;

    public PlayerCommandAction(ConfigurationSection actionSection) {
        super(List.of("command"), actionSection);

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