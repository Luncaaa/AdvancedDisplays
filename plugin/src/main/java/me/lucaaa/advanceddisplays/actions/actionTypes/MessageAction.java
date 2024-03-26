package me.lucaaa.advanceddisplays.actions.actionTypes;

import me.lucaaa.advanceddisplays.actions.Action;
import me.lucaaa.advanceddisplays.common.utils.Utils;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.entity.Player;

public class MessageAction extends Action {
    private final String message;

    public MessageAction(String message, int delay) {
        super(delay);
        this.message = message;
    }

    @Override
    public void runAction(Player player) {
        player.spigot().sendMessage(ComponentSerializer.parse(Utils.getColoredTextWithPlaceholders(player, this.message)));
    }
}
