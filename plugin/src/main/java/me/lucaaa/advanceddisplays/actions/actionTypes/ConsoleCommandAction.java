package me.lucaaa.advanceddisplays.actions.actionTypes;

import me.lucaaa.advanceddisplays.actions.Action;
import me.lucaaa.advanceddisplays.common.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class ConsoleCommandAction extends Action {
    private final String command;

    public ConsoleCommandAction(ConfigurationSection actionSection) {
        super(List.of("command"), actionSection);
        this.command = actionSection.getString("command");
    }

    @Override
    public void runAction(Player actionPlayer, Player globalPlayer) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Utils.getColoredTextWithPlaceholders(globalPlayer, this.command, actionPlayer));
    }
}
