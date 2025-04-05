package me.lucaaa.advanceddisplays.actions.actionTypes;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.actions.Action;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class EffectAction extends Action {
    private final PotionEffect effect;

    public EffectAction(AdvancedDisplays plugin, ConfigurationSection actionSection) {
        super(plugin, List.of("effect", "duration", "amplifier"), actionSection);
        PotionEffectType type = PotionEffectType.getByName(Objects.requireNonNull(actionSection.getString("effect")));

        if (type == null) {
            plugin.log(Level.WARNING, "Invalid effect type found on action \"" + actionSection.getName() + "\": " + actionSection.getString("effect"));
            this.effect = null;
            this.isCorrect = false;
            return;
        }

        int duration = actionSection.getInt("duration");
        int amplifier = actionSection.getInt("amplifier");
        boolean ambient = actionSection.getBoolean("ambient", true);
        boolean particles = actionSection.getBoolean("particles", true);
        boolean icon = actionSection.getBoolean("icon", true);

        this.effect = new PotionEffect(type, duration, amplifier, ambient, particles, icon);
    }

    @Override
    public void runAction(Player clickedPlayer, Player actionPlayer) {
        actionPlayer.addPotionEffect(effect);
    }
}