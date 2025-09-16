package me.lucaaa.advanceddisplays.actions.actionTypes;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.actions.Action;
import me.lucaaa.advanceddisplays.api.displays.BaseEntity;
import me.lucaaa.advanceddisplays.data.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.logging.Level;

public class SoundAction extends Action {
    private final String sound;
    private final String[] unparsedLoc;
    private final float volume;
    private final float pitch;

    public SoundAction(AdvancedDisplays plugin, ConfigurationSection section) {
        super(
                plugin,
                ActionType.PLAY_SOUND,
                section,
                List.of(
                        new Field("sound", String.class),
                        // new Field("location", String.class), // Use display's location by default
                        new Field("volume", Double.class),
                        new Field("pitch", Double.class)
                )
        );

        sound = section.getString("sound", "");

        String unparsedLocString = section.getString("location", "*;*;*");
        this.unparsedLoc = unparsedLocString.split(";");
        errors.addAll(Utils.isStringValidLoc(unparsedLocString));

        volume = (float) section.getDouble("volume");
        pitch = (float) section.getDouble("pitch");
    }

    @Override
    public void runAction(Player clickedPlayer, Player actionPlayer, BaseEntity display) {
        try {
            Location loc = display.getLocation();
            double x = Utils.parsePosition(unparsedLoc[0], Utils.CoordComponent.X, loc, actionPlayer);
            double y = Utils.parsePosition(unparsedLoc[1], Utils.CoordComponent.Y, loc, actionPlayer);
            double z = Utils.parsePosition(unparsedLoc[2], Utils.CoordComponent.Z, loc, actionPlayer);
            Location location = new Location(actionPlayer.getWorld(), x, y, z);

            try {
                Sound soundEnum = Sound.valueOf(sound);
                actionPlayer.playSound(location, soundEnum, volume, pitch);
            } catch (IllegalArgumentException e) {
                actionPlayer.playSound(location, sound, volume, pitch);
            }

        } catch (NumberFormatException e) {
            plugin.log(Level.WARNING, "One of the coordinates is not a valid number or relative position! For decimals use dots (\".\"), NOT commas (\",\").");
        }
    }
}