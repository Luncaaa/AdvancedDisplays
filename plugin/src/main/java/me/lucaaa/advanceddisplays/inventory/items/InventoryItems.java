package me.lucaaa.advanceddisplays.inventory.items;

import me.lucaaa.advanceddisplays.api.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.inventory.inventories.PlayerInv;
import org.bukkit.Material;
import org.bukkit.util.Transformation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class InventoryItems {
    // SCALE_TRANSLATION
    public final Item SCALE_X;
    public final Item SCALE_Y;
    public final Item SCALE_Z;

    public final Item TRANSLATION_X;
    public final Item TRANSLATION_Y;
    public final Item TRANSLATION_Z;
    // ---

    // LEFT_ROTATION_YAW_PITCH
    public final Item LEFT_ROTATION_X;
    public final Item LEFT_ROTATION_Y;
    public final Item LEFT_ROTATION_Z;
    public final Item LEFT_ROTATION_ANGLE;

    public final Item YAW;
    public final Item PITCH;
    // ---

    // RIGHT_ROTATION_HITBOX
    public final Item RIGHT_ROTATION_X;
    public final Item RIGHT_ROTATION_Y;
    public final Item RIGHT_ROTATION_Z;
    public final Item RIGHT_ROTATION_ANGLE;

    public final Item HITBOX_WIDTH;
    public final Item HITBOX_HEIGHT;
    // ---

    // GLOBAL
    public final Item OPEN_GUI;
    public final Item CHANGE_ROW;
    // ---

    public InventoryItems(BaseDisplay display) {
        Transformation transformation = display.getTransformation();

        SCALE_X = new Item(Material.LARGE_AMETHYST_BUD, "Scale X", "Changes the x component of the display's scale", transformation.getScale().x, true, true, 1.0, 0.1, true);
        SCALE_Y = new Item(Material.LARGE_AMETHYST_BUD, "Scale Y", "Changes the y component of the display's scale", transformation.getScale().y, true, true, 1.0, 0.1, true);
        SCALE_Z = new Item(Material.LARGE_AMETHYST_BUD, "Scale Z", "Changes the z component of the display's scale", transformation.getScale().z, true, true, 1.0, 0.1, true);

        TRANSLATION_X = new Item(Material.REPEATER, "Translation X", "Changes the x component of the display's translation", transformation.getTranslation().x, true, true, 1.0, 0.1, true);
        TRANSLATION_Y = new Item(Material.REPEATER, "Translation Y", "Changes the y component of the display's translation", transformation.getTranslation().y, true, true, 1.0, 0.1, true);
        TRANSLATION_Z = new Item(Material.REPEATER, "Translation Z", "Changes the z component of the display's translation", transformation.getTranslation().z, true, true, 1.0, 0.1, true);

        LEFT_ROTATION_X = new Item(Material.BLAZE_ROD, "Left Rotation X", "Changes the x component of the display's left rotation", transformation.getLeftRotation().x, true, true, 1.0, 0.1, true);
        LEFT_ROTATION_Y = new Item(Material.BLAZE_ROD, "Left Rotation Y", "Changes the y component of the display's left rotation", transformation.getLeftRotation().y, true, true, 1.0, 0.1, true);
        LEFT_ROTATION_Z = new Item(Material.BLAZE_ROD, "Left Rotation Z", "Changes the z component of the display's left rotation", transformation.getLeftRotation().z, true, true, 1.0, 0.1, true);
        LEFT_ROTATION_ANGLE = new Item(Material.MAGMA_CREAM, "Left Rotation Angle", "Changes the angle of the display's left rotation", BigDecimal.valueOf(Math.toDegrees(transformation.getLeftRotation().angle())).setScale(2, RoundingMode.HALF_UP).doubleValue(), true, true, 10.0, 1.0, true);

        YAW = new Item(Material.SLIME_BALL, "Yaw", "Changes the display's yaw", display.getYaw(), true, true, 10.0, 1.0, true);
        PITCH = new Item(Material.FIRE_CHARGE, "Pitch", "Changes the display's pitch", display.getPitch(), true, true, 10.0, 1.0, true);

        RIGHT_ROTATION_X = new Item(Material.STICK, "Right Rotation X", "Changes the x component of the display's right rotation", transformation.getRightRotation().x, true, true, 1.0, 0.1, true);
        RIGHT_ROTATION_Y = new Item(Material.STICK, "Right Rotation Y", "Changes the y component of the display's right rotation", transformation.getRightRotation().y, true, true, 1.0, 0.1, true);
        RIGHT_ROTATION_Z = new Item(Material.STICK, "Left Rotation Z", "Changes the z component of the display's right rotation", transformation.getRightRotation().z, true, true, 1.0, 0.1, true);
        RIGHT_ROTATION_ANGLE = new Item(Material.MAGMA_CREAM, "Right Rotation Angle", "Changes the angle of the display's right rotation", BigDecimal.valueOf(Math.toDegrees(transformation.getRightRotation().angle())).setScale(2, RoundingMode.HALF_UP).doubleValue(), true, true, 10.0, 1.0, true);

        HITBOX_WIDTH = new Item(Material.LEATHER, "Hitbox width", List.of("Changes the width of the display's hitbox.", "It will automatically set the hitbox size override to true,", "although you can change it to false in the GUI"), display.getHitboxWidth(), true, true, 1.0, 0.1, true);
        HITBOX_HEIGHT = new Item(Material.RABBIT_HIDE, "Hitbox height", List.of("Changes the height of the display's hitbox.", "It will automatically set the hitbox size override to true,", "although you can change it to false in the GUI"), display.getHitboxHeight(), true, true, 1.0, 0.1, true);

        OPEN_GUI = new Item(Material.NETHER_STAR, "Open editor GUI", List.of("Opens a GUI with more options", "to edit the display"), "(" + display.getType() + ") " + display.getName(), false);
        CHANGE_ROW = new Item(Material.ARROW, "Change row", List.of("Changes the tools in your hotbar", "with another row of tools"), PlayerInv.InventoryRows.LEFT_ROTATION_YAW_PITCH, true);
    }
}
