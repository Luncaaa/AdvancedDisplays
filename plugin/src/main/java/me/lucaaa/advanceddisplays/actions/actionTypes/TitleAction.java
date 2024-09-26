package me.lucaaa.advanceddisplays.actions.actionTypes;

import me.lucaaa.advanceddisplays.actions.Action;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class TitleAction extends Action {
    private final String title;
    private final String subtitle;
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    public TitleAction(ConfigurationSection actionSection) {
        super(List.of("title", "subtitle", "fadeIn", "stay", "fadeOut"), actionSection);
        this.title = actionSection.getString("title");
        this.subtitle = actionSection.getString("subtitle");
        this.fadeIn = actionSection.getInt("fadeIn", 20);
        this.stay = actionSection.getInt("stay");
        this.fadeOut = actionSection.getInt("fadeOut", 20);
    }

    @Override
    public void runAction(Player clickedPlayer, Player actionPlayer) {
        String top = getTextString(title, clickedPlayer, actionPlayer);
        String bottom = getTextString(subtitle, clickedPlayer, actionPlayer);
        actionPlayer.sendTitle(top, bottom, fadeIn, stay, fadeOut);
    }
}