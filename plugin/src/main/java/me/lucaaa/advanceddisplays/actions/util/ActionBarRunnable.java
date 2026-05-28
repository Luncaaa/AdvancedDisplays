package me.lucaaa.advanceddisplays.actions.util;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.actions.actionTypes.ActionbarAction;
import me.lucaaa.advanceddisplays.common.ADRunnable;
import me.lucaaa.advanceddisplays.managers.MessagesManager;
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
        MessagesManager manager = plugin.getMessagesManager();
        Component component = action.getText(message, clickedPlayer, actionPlayer);
        plugin.getTasksManager().runTaskTimer(plugin, new ADRunnable() {
            private int timeLeft = duration;

            @Override
            public void run() {
                manager.sendActionbar(actionPlayer, component);
                --timeLeft;
                if (timeLeft == 0) cancel();
            }
        }, 1L, 0L);
    }
}