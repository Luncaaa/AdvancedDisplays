package me.lucaaa.advanceddisplays.actions.actionTypes;

import me.lucaaa.advanceddisplays.actions.Action;
import me.lucaaa.advanceddisplays.common.utils.Utils;
import org.bukkit.entity.Player;

public class PlayerCommandAction extends Action {
    private final String command;

    public PlayerCommandAction(String command, int delay) {
        super(delay);
        this.command = command;
    }

    @Override
    public void runAction(Player player) {
        player.performCommand(Utils.getTextWithPlaceholders(player, this.command));
    }
}
