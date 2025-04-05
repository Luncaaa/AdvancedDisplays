package me.lucaaa.advanceddisplays.actions.actionTypes;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.actions.Action;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class ConsoleCommandAction extends Action {
    private final List<String> commands;

    public ConsoleCommandAction(AdvancedDisplays plugin, ConfigurationSection actionSection) {
        super(plugin, List.of("command"), actionSection);

        if (actionSection.isList("command")) {
            this.commands = actionSection.getStringList("command");

        } else {
            this.commands = List.of(actionSection.getString("command", ""));
        }
    }

    @Override
    public void runAction(Player clickedPlayer, Player actionPlayer) {
        for (String command : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getTextString(command, clickedPlayer, actionPlayer));
        }
    }
}