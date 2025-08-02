package me.lucaaa.advanceddisplays.actions.util;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.actions.actionTypes.ActionbarAction;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
        new BukkitRunnable() {
            private  int timeLeft = duration;
            @Override
            public void run() {
                audience.sendActionBar(component);
                --timeLeft;
                if (timeLeft == 0) cancel();
            }
        }.runTaskTimer(plugin, 0L, 0L);
    }
}