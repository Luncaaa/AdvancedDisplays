package me.lucaaa.advanceddisplays.actions.actionTypes;

import me.lucaaa.advanceddisplays.actions.Action;
import me.lucaaa.advanceddisplays.common.utils.Utils;
import net.md_5.bungee.chat.ComponentSerializer;
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
    public void runAction(Player actionPlayer, Player globalPlayer) {
        globalPlayer.spigot().sendMessage(ComponentSerializer.parse(Utils.getColoredTextWithPlaceholders(globalPlayer, this.message, actionPlayer)));
    }
}
