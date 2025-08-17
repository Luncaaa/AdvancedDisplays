package me.lucaaa.advanceddisplays.actions.actionTypes;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.actions.Action;
import me.lucaaa.advanceddisplays.api.displays.BaseEntity;
import me.lucaaa.advanceddisplays.data.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.logging.Level;

public class ParticleAction extends Action {
    private final Particle particle;
    private final String[] unparsedLoc;
    private final int count;

    private String[] unparsedOffset = {};
    private final double speed;

    public ParticleAction(AdvancedDisplays plugin, ConfigurationSection section) {
        super(
                plugin,
                ActionType.PARTICLE,
                section,
                List.of(
                        new Field("particle", String.class),
                        new Field("location", String.class),
                        new Field("count", Integer.class)
                )
        );

        String configParticle = section.getString("particle", "");
        Particle particle1;
        try {
            particle1 = Particle.valueOf(configParticle.toUpperCase());
        } catch (IllegalArgumentException e) {
            particle1 = null;
            errors.add("Invalid particle type: " + configParticle);
        }
        this.particle = particle1;

        String unparsedLocString = section.getString("location", "");
        this.unparsedLoc = unparsedLocString.split(";");
        errors.addAll(Utils.isStringValidLoc(unparsedLocString));

        this.count = section.getInt("count");

        if (section.isString("offset")) {
            String unparsedOffsetString = section.getString("offset", "");
            this.unparsedOffset = unparsedOffsetString.split(";");
            errors.addAll(Utils.isStringValidLoc(unparsedOffsetString));
        }

        if (particle.getDataType() != Void.class) {
            errors.add("Unsupported particle - contact developer.");
        }

        speed = section.getDouble("speed", 0.0);
    }

    @Override
    public void runAction(Player clickedPlayer, Player actionPlayer, BaseEntity display) {
        try {
            Location loc = display.getLocation();
            double x = Utils.parsePosition(unparsedLoc[0], Utils.CoordComponent.X, loc, actionPlayer);
            double y = Utils.parsePosition(unparsedLoc[1], Utils.CoordComponent.Y, loc, actionPlayer);
            double z = Utils.parsePosition(unparsedLoc[2], Utils.CoordComponent.Z, loc, actionPlayer);
            Location location = new Location(actionPlayer.getWorld(), x, y, z);

            if (unparsedOffset.length != 0) {
                double offsetX = Double.parseDouble(unparsedOffset[0]);
                double offsetY = Double.parseDouble(unparsedOffset[1]);
                double offsetZ = Double.parseDouble(unparsedOffset[2]);

                actionPlayer.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, speed);

            } else {
                actionPlayer.spawnParticle(particle, location, count);
            }

        } catch (NumberFormatException e) {
            plugin.log(Level.WARNING, "One of the coordinates is not a valid number or relative position! For decimals use dots (\".\"), NOT commas (\",\").");
        }
    }
}