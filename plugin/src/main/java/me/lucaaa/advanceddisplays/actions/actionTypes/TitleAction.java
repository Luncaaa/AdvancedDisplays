package me.lucaaa.advanceddisplays.actions.actionTypes;

import me.lucaaa.advanceddisplays.actions.Action;
import me.lucaaa.advanceddisplays.common.utils.Utils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class TitleAction extends Action {
    private final String title;
    private final String subtitle;
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    public TitleAction(ConfigurationSection actionSection, int delay) {
        super(delay, List.of("title", "subtitle", "fadeIn", "stay", "fadeOut"), actionSection);
        this.title = actionSection.getString("title");
        this.subtitle = actionSection.getString("subtitle");
        this.fadeIn = actionSection.getInt("fadeIn");
        this.stay = actionSection.getInt("stay");
        this.fadeOut = actionSection.getInt("fadeOut");
    }

    @Override
    public void runAction(Player player) {
        String title = BaseComponent.toLegacyText(ComponentSerializer.parse(Utils.getColoredTextWithPlaceholders(player, this.title)));
        String subtitle = BaseComponent.toLegacyText(ComponentSerializer.parse(Utils.getColoredTextWithPlaceholders(player, this.subtitle)));
        player.sendTitle(title,subtitle,this.fadeIn, this.stay, this.fadeOut);
    }
}
