package me.lucaaa.advanceddisplays.actions.actionTypes;

import me.lucaaa.advanceddisplays.actions.Action;
import me.lucaaa.advanceddisplays.common.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ConsoleCommandAction extends Action {
    private final String command;

    public ConsoleCommandAction(String command, int delay) {
        super(delay);
        this.command = command;
    }

    @Override
    public void runAction(Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Utils.getTextWithPlaceholders(player, this.command));
    }
}
