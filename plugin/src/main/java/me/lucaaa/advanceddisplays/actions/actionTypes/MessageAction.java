package me.lucaaa.advanceddisplays.actions.actionTypes;

import me.lucaaa.advanceddisplays.actions.Action;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class MessageAction extends Action {
    private final String message;

    public MessageAction(ConfigurationSection actionSection) {
        super(List.of("message"), actionSection);
        this.message = actionSection.getString("message");
    }

    @Override
    public void runAction(Player clickedPlayer, Player actionPlayer) {
        actionPlayer.spigot().sendMessage(this.getTextComponent(this.message, clickedPlayer, actionPlayer));
    }
}
