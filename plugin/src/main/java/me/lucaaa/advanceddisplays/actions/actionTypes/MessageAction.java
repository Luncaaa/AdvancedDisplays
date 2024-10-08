package me.lucaaa.advanceddisplays.actions.actionTypes;

import me.lucaaa.advanceddisplays.actions.Action;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class MessageAction extends Action {
    private String message;
    private List<String> messages;

    public MessageAction(ConfigurationSection actionSection) {
        super(List.of("message"), actionSection);

        if (actionSection.isList("message")) {
            this.messages = actionSection.getStringList("message");
        } else {
            this.message = actionSection.getString("message");
        }
    }

    @Override
    public void runAction(Player clickedPlayer, Player actionPlayer) {
        if (message != null) {
            actionPlayer.spigot().sendMessage(getTextComponent(message, clickedPlayer, actionPlayer));
        } else {
            for (String message : messages) {
                actionPlayer.spigot().sendMessage(getTextComponent(message, clickedPlayer, actionPlayer));
            }
        }
    }
}