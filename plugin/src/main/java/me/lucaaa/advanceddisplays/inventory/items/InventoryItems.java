package me.lucaaa.advanceddisplays.inventory.items;

import me.lucaaa.advanceddisplays.api.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.inventory.inventories.PlayerInv;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class InventoryItems {
    // SCALE_TRANSLATION
    public final ItemStack SCALE_X;
    public final ItemStack SCALE_Y;
    public final ItemStack SCALE_Z;

    public final ItemStack TRANSLATION_X;
    public final ItemStack TRANSLATION_Y;
    public final ItemStack TRANSLATION_Z;
    // ---

    // LEFT_ROTATION_YAW_PITCH
    public final ItemStack LEFT_ROTATION_X;
    public final ItemStack LEFT_ROTATION_Y;
    public final ItemStack LEFT_ROTATION_Z;
    public final ItemStack LEFT_ROTATION_ANGLE;

    public final ItemStack YAW;
    public final ItemStack PITCH;
    // ---

    // RIGHT_ROTATION_HITBOX
    public final ItemStack RIGHT_ROTATION_X;
    public final ItemStack RIGHT_ROTATION_Y;
    public final ItemStack RIGHT_ROTATION_Z;
    public final ItemStack RIGHT_ROTATION_ANGLE;

    public final ItemStack HITBOX_WIDTH;
    public final ItemStack HITBOX_HEIGHT;
    // ---

    // GLOBAL
    public final ItemStack OPEN_GUI;
    public final ItemStack CHANGE_ROW;
    // ---

    public InventoryItems(BaseDisplay display) {
        Transformation transformation = display.getTransformation();

        SCALE_X = GlobalItems.create(Material.LARGE_AMETHYST_BUD, "Scale X", "Changes the x component of the display's scale", transformation.getScale().x, true, true, 1.0, 0.1, true);
        SCALE_Y = GlobalItems.create(Material.LARGE_AMETHYST_BUD, "Scale Y", "Changes the y component of the display's scale", transformation.getScale().y, true, true, 1.0, 0.1, true);
        SCALE_Z = GlobalItems.create(Material.LARGE_AMETHYST_BUD, "Scale Z", "Changes the z component of the display's scale", transformation.getScale().z, true, true, 1.0, 0.1, true);

        TRANSLATION_X = GlobalItems.create(Material.REPEATER, "Translation X", "Changes the x component of the display's translation", transformation.getTranslation().x, true, true, 1.0, 0.1, true);
        TRANSLATION_Y = GlobalItems.create(Material.REPEATER, "Translation Y", "Changes the y component of the display's translation", transformation.getTranslation().y, true, true, 1.0, 0.1, true);
        TRANSLATION_Z = GlobalItems.create(Material.REPEATER, "Translation Z", "Changes the z component of the display's translation", transformation.getTranslation().z, true, true, 1.0, 0.1, true);

        LEFT_ROTATION_X = GlobalItems.create(Material.BLAZE_ROD, "Left Rotation X", "Changes the x component of the display's left rotation", transformation.getLeftRotation().x, true, true, 1.0, 0.1, true);
        LEFT_ROTATION_Y = GlobalItems.create(Material.BLAZE_ROD, "Left Rotation Y", "Changes the y component of the display's left rotation", transformation.getLeftRotation().y, true, true, 1.0, 0.1, true);
        LEFT_ROTATION_Z = GlobalItems.create(Material.BLAZE_ROD, "Left Rotation Z", "Changes the z component of the display's left rotation", transformation.getLeftRotation().z, true, true, 1.0, 0.1, true);
        LEFT_ROTATION_ANGLE = GlobalItems.create(Material.MAGMA_CREAM, "Left Rotation Angle", "Changes the angle of the display's left rotation", BigDecimal.valueOf(Math.toDegrees(transformation.getLeftRotation().angle())).setScale(2, RoundingMode.HALF_UP).doubleValue(), true, true, 10.0, 1.0, true);

        YAW = GlobalItems.create(Material.SLIME_BALL, "Yaw", "Changes the display's yaw", display.getYaw(), true, true, 10.0, 1.0, true);
        PITCH = GlobalItems.create(Material.FIRE_CHARGE, "Pitch", "Changes the display's pitch", display.getPitch(), true, true, 10.0, 1.0, true);

        RIGHT_ROTATION_X = GlobalItems.create(Material.STICK, "Right Rotation X", "Changes the x component of the display's right rotation", transformation.getRightRotation().x, true, true, 1.0, 0.1, true);
        RIGHT_ROTATION_Y = GlobalItems.create(Material.STICK, "Right Rotation Y", "Changes the y component of the display's right rotation", transformation.getRightRotation().y, true, true, 1.0, 0.1, true);
        RIGHT_ROTATION_Z = GlobalItems.create(Material.STICK, "Left Rotation Z", "Changes the z component of the display's right rotation", transformation.getRightRotation().z, true, true, 1.0, 0.1, true);
        RIGHT_ROTATION_ANGLE = GlobalItems.create(Material.MAGMA_CREAM, "Right Rotation Angle", "Changes the angle of the display's right rotation", BigDecimal.valueOf(Math.toDegrees(transformation.getRightRotation().angle())).setScale(2, RoundingMode.HALF_UP).doubleValue(), true, true, 10.0, 1.0, true);

        HITBOX_WIDTH = GlobalItems.create(Material.LEATHER, "Hitbox width", List.of("Changes the width of the display's hitbox.", "It will automatically set the hitbox size override to true,", "although you can change it to false in the GUI"), display.getHitboxWidth(), true, true, 1.0, 0.1, true);
        HITBOX_HEIGHT = GlobalItems.create(Material.RABBIT_HIDE, "Hitbox height", List.of("Changes the height of the display's hitbox.", "It will automatically set the hitbox size override to true,", "although you can change it to false in the GUI"), display.getHitboxHeight(), true, true, 1.0, 0.1, true);

        OPEN_GUI = GlobalItems.create(Material.NETHER_STAR, "Open editor GUI", List.of("Opens a GUI with more options", "to edit the display"), "(" + display.getType() + ") " + display.getName(), false);
        CHANGE_ROW = GlobalItems.create(Material.ARROW, "Change row", List.of("Changes the tools in your hotbar", "with another row of tools"), PlayerInv.InventoryRows.LEFT_ROTATION_YAW_PITCH.getName(), true);
    }
}
