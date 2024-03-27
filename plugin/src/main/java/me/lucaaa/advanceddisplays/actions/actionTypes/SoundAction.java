package me.lucaaa.advanceddisplays.actions.actionTypes;

import me.lucaaa.advanceddisplays.actions.Action;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class SoundAction extends Action {
    private final Sound sound;
    private final float volume;
    private final float pitch;

    public SoundAction(ConfigurationSection actionSection) {
        super(List.of("sound", "volume", "pitch"), actionSection);
        this.sound = Sound.valueOf(actionSection.getString("sound"));
        this.volume = (float) actionSection.getDouble("volume");
        this.pitch = (float) actionSection.getDouble("pitch");
    }

    @Override
    public void runAction(Player clickedPlayer, Player actionPlayer) {
        actionPlayer.playSound(actionPlayer, this.sound, this.volume, this.pitch);
    }
}
