package me.lucaaa.advanceddisplays.actions.actionTypes;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.actions.Action;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class MessageAction extends Action {
    private final List<String> messages;

    public MessageAction(AdvancedDisplays plugin, ConfigurationSection actionSection) {
        super(plugin, List.of("message"), actionSection);

        if (actionSection.isList("message")) {
            this.messages = actionSection.getStringList("message");
        } else {
            this.messages = List.of(actionSection.getString("message", "No message set"));
        }
    }

    @Override
    public void runAction(Player clickedPlayer, Player actionPlayer) {
        for (String message : messages) {
            actionPlayer.spigot().sendMessage(getTextComponent(message, clickedPlayer, actionPlayer));
        }
    }
}