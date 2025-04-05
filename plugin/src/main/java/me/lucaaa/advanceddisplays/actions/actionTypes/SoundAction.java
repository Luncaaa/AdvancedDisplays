package me.lucaaa.advanceddisplays.actions.actionTypes;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.actions.Action;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.logging.Level;

public class SoundAction extends Action {
    private final Sound sound;
    private final float volume;
    private final float pitch;

    public SoundAction(AdvancedDisplays plugin, ConfigurationSection actionSection) {
        super(plugin, List.of("sound", "volume", "pitch"), actionSection);
        Sound sound = null;
        try {
            sound = Sound.valueOf(actionSection.getString("sound"));
        } catch (IllegalArgumentException exception) {
            plugin.log(Level.WARNING, "Invalid sound found on action \"" + actionSection.getName() + "\": " + actionSection.getString("sound"));
            this.isCorrect = false;
        }

        this.sound = sound;
        this.volume = (float) actionSection.getDouble("volume");
        this.pitch = (float) actionSection.getDouble("pitch");
    }

    @Override
    public void runAction(Player clickedPlayer, Player actionPlayer) {
        actionPlayer.playSound(actionPlayer, sound, volume, pitch);
    }
}