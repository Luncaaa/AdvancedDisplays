package me.lucaaa.advanceddisplays.actions.actionTypes;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.actions.Action;
import me.lucaaa.advanceddisplays.actions.util.ActionBarRunnable;
import me.lucaaa.advanceddisplays.api.displays.BaseEntity;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class ActionbarAction extends Action {
    private final ActionBarRunnable runnable;

    public ActionbarAction(AdvancedDisplays plugin, ConfigurationSection actionSection) {
        super(
                plugin,
                ActionType.ACTIONBAR,
                actionSection,
                List.of(
                        new Field("message", String.class),
                        new Field("duration", Integer.class)
                )
        );

        String message = actionSection.getString("message");
        int duration = actionSection.getInt("duration");
        this.runnable = new ActionBarRunnable(plugin, this, message, duration);
    }

    @Override
    public void runAction(Player clickedPlayer, Player actionPlayer, BaseEntity display) {
        runnable.sendToPlayer(clickedPlayer, actionPlayer);
    }
}