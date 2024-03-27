package me.lucaaa.advanceddisplays.actions.actionTypes;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.actions.Action;
import me.lucaaa.advanceddisplays.common.utils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class ActionbarAction extends Action {
    private final String message;
    private final int duration;

    public ActionbarAction(ConfigurationSection actionSection) {
        super(List.of("message", "duration"), actionSection);
        this.message = actionSection.getString("message");
        this.duration = actionSection.getInt("duration");
    }

    @Override
    public void runAction(Player actionPlayer, Player globalPlayer) {
        Bukkit.getScheduler().runTaskTimer(AdvancedDisplays.getPlugin(), () -> globalPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, ComponentSerializer.parse(Utils.getColoredTextWithPlaceholders(globalPlayer, message, actionPlayer))), 0, this.duration);
    }
}
