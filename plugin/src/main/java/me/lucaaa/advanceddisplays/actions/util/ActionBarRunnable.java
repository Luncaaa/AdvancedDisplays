package me.lucaaa.advanceddisplays.actions.util;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.actions.actionTypes.ActionbarAction;
import me.lucaaa.advanceddisplays.common.ADRunnable;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class ActionBarRunnable {
    private final AdvancedDisplays plugin;
    private final ActionbarAction action;
    private final String message;
    private final int duration;

    public ActionBarRunnable(AdvancedDisplays plugin, ActionbarAction action, String message, int duration) {
        this.plugin = plugin;
        this.action = action;
        this.message = message;
        this.duration = duration;
    }

    public void sendToPlayer(Player clickedPlayer, Player actionPlayer) {
        Audience audience = plugin.getAudience(actionPlayer);
        Component component = action.getText(message, clickedPlayer, actionPlayer);
        plugin.getTasksManager().runTaskTimer(plugin, new ADRunnable() {
            private int timeLeft = duration;

            @Override
            public void run() {
                audience.sendActionBar(component);
                --timeLeft;
                if (timeLeft == 0) cancel();
            }
        }, 1L, 0L);
    }
}