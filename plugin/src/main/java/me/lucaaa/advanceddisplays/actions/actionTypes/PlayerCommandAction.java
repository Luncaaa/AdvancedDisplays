package me.lucaaa.advanceddisplays.actions.actionTypes;

import me.lucaaa.advanceddisplays.actions.Action;
import me.lucaaa.advanceddisplays.common.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerCommandAction extends Action {
    private final String command;

    public PlayerCommandAction(ConfigurationSection actionSection) {
        super(List.of("command"), actionSection);
        this.command = actionSection.getString("command");
    }

    @Override
    public void runAction(Player actionPlayer, Player globalPlayer) {
        globalPlayer.performCommand(Utils.getColoredTextWithPlaceholders(globalPlayer, this.command, actionPlayer));
    }
}
