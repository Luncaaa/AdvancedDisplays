package me.lucaaa.advanceddisplays.actions.actionTypes;

import me.lucaaa.advanceddisplays.actions.Action;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

// TODO: Command list support
public class PlayerCommandAction extends Action {
    private final String command;
    private final String arguments;

    public PlayerCommandAction(ConfigurationSection actionSection) {
        super(List.of("command"), actionSection);

        List<String> fullCommand = new LinkedList<>(Arrays.asList(actionSection.getString("command", "").split(" ")));
        this.command = fullCommand.remove(0);
        this.arguments = String.join(" ", fullCommand);
    }

    @Override
    public void runAction(Player clickedPlayer, Player actionPlayer) {
        actionPlayer.performCommand(command + " " + getTextString(arguments, clickedPlayer, actionPlayer));
    }
}