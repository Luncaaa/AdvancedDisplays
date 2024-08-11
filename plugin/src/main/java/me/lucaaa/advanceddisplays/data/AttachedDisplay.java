package me.lucaaa.advanceddisplays.data;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public record AttachedDisplay(String name, Side side, Component content, boolean saveToConfig) {
    public enum Side {
        LEFT,
        RIGHT,
        CENTER
    }

    public static float getYaw(BlockFace clickedFace) {
        return switch (clickedFace) {
            case NORTH -> -180.0f;
            // South is covered by "default" branch
            case EAST -> -90.0f;
            case WEST -> 90.0f;
            default -> 0.0f;
        };
    }

    public static float getPos(BlockFace clickedFace, Side side) {
        if (clickedFace == BlockFace.NORTH || clickedFace == BlockFace.EAST) {
            return switch (side) {
                case LEFT -> 1.0f;
                case CENTER -> 0.5f;
                case RIGHT -> 0.0f;
            };

        } else {
            return switch (side) {
                case LEFT -> 0.0f;
                case CENTER -> 0.5f;
                case RIGHT -> 1.0f;
            };
        }
    }

    public static Location addSides(BlockFace face, Location clickedBlockLocation, double pos, boolean add) {
        Location location = clickedBlockLocation.clone();

        // If the clicked face is UP or DOWN, don't add 1 (so it's at the base of the clicked block.)
        double add1 = 0.0;
        if (add) {
            add1 = switch (face) {
                case NORTH, WEST -> 1.0;
                case SOUTH, EAST -> -1.0;
                default -> 0.0;
            };
        }

        switch (face) {
            case NORTH -> location.add(pos, 0.0, add1 -0.001);
            case SOUTH -> location.add(pos, 0.0, add1 + 1.001);
            case EAST -> location.add(add1 + 1.001, 0.0, pos);
            case WEST -> location.add(add1 -0.001, 0.0, pos);
        }
        return location;
    }
}