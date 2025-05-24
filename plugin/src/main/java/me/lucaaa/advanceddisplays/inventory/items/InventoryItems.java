package me.lucaaa.advanceddisplays.inventory.items;

import me.lucaaa.advanceddisplays.api.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.api.displays.BaseEntity;
import me.lucaaa.advanceddisplays.inventory.inventories.PlayerInv;
import org.bukkit.Material;
import org.bukkit.util.Transformation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class InventoryItems {
    // LEFT_ROTATION_YAW_PITCH
    public final Item.StepItem YAW;
    public final Item.StepItem PITCH;
    // ---

    // GLOBAL
    public final Item.ClickableItem OPEN_GUI;
    public final Item.EnumItem CHANGE_ROW;
    // ---

    public InventoryItems(BaseEntity entity) {
        YAW = new Item.StepItem(Material.SLIME_BALL, "Yaw", "Changes the display's yaw", entity.getYaw(), 10.0, 1.0, true);
        PITCH = new Item.StepItem(Material.FIRE_CHARGE, "Pitch", "Changes the display's pitch", entity.getPitch(), 10.0, 1.0, true);

        OPEN_GUI = new Item.ClickableItem(Material.NETHER_STAR, "Open editor GUI", List.of("Opens a GUI with more options", "to edit the display"), "(" + entity.getType() + ") " + entity.getName());
        CHANGE_ROW = new Item.EnumItem(Material.ARROW, "Change row", List.of("Changes the tools in your hotbar", "with another row of tools"), PlayerInv.InventoryRows.LEFT_ROTATION_YAW_PITCH, true);
    }

    public static class DisplayItems extends InventoryItems {
        // LEFT_ROTATION_YAW_PITCH
        public final Item.StepItem LEFT_ROTATION_X;
        public final Item.StepItem LEFT_ROTATION_Y;
        public final Item.StepItem LEFT_ROTATION_Z;
        public final Item.StepItem LEFT_ROTATION_ANGLE;
        // ---

        // RIGHT_ROTATION_HITBOX
        public final Item.StepItem  RIGHT_ROTATION_X;
        public final Item.StepItem  RIGHT_ROTATION_Y;
        public final Item.StepItem  RIGHT_ROTATION_Z;
        public final Item.StepItem  RIGHT_ROTATION_ANGLE;

        public final Item.StepItem  HITBOX_WIDTH;
        public final Item.StepItem  HITBOX_HEIGHT;
        // ---

        // SCALE_TRANSLATION
        public final Item.StepItem SCALE_X;
        public final Item.StepItem SCALE_Y;
        public final Item.StepItem SCALE_Z;

        public final Item.StepItem TRANSLATION_X;
        public final Item.StepItem TRANSLATION_Y;
        public final Item.StepItem TRANSLATION_Z;
        // ---

        public DisplayItems(BaseDisplay display) {
            super(display);
            Transformation transformation = display.getTransformation();

            SCALE_X = new Item.StepItem(Material.LARGE_AMETHYST_BUD, "Scale X", "Changes the x component of the display's scale", transformation.getScale().x, 1.0, 0.1, true);
            SCALE_Y = new Item.StepItem(Material.LARGE_AMETHYST_BUD, "Scale Y", "Changes the y component of the display's scale", transformation.getScale().y, 1.0, 0.1, true);
            SCALE_Z = new Item.StepItem(Material.LARGE_AMETHYST_BUD, "Scale Z", "Changes the z component of the display's scale", transformation.getScale().z, 1.0, 0.1, true);

            TRANSLATION_X = new Item.StepItem(Material.REPEATER, "Translation X", "Changes the x component of the display's translation", transformation.getTranslation().x,1.0, 0.1, true);
            TRANSLATION_Y = new Item.StepItem(Material.REPEATER, "Translation Y", "Changes the y component of the display's translation", transformation.getTranslation().y, 1.0, 0.1, true);
            TRANSLATION_Z = new Item.StepItem(Material.REPEATER, "Translation Z", "Changes the z component of the display's translation", transformation.getTranslation().z, 1.0, 0.1, true);

            LEFT_ROTATION_X = new Item.StepItem(Material.BLAZE_ROD, "Left Rotation X", "Changes the x component of the display's left rotation", transformation.getLeftRotation().x, 1.0, 0.1, true);
            LEFT_ROTATION_Y = new Item.StepItem(Material.BLAZE_ROD, "Left Rotation Y", "Changes the y component of the display's left rotation", transformation.getLeftRotation().y, 1.0, 0.1, true);
            LEFT_ROTATION_Z = new Item.StepItem(Material.BLAZE_ROD, "Left Rotation Z", "Changes the z component of the display's left rotation", transformation.getLeftRotation().z, 1.0, 0.1, true);
            LEFT_ROTATION_ANGLE = new Item.StepItem(Material.MAGMA_CREAM, "Left Rotation Angle", "Changes the angle of the display's left rotation", BigDecimal.valueOf(Math.toDegrees(transformation.getLeftRotation().angle())).setScale(2, RoundingMode.HALF_UP).doubleValue(), 10.0, 1.0, true);

            RIGHT_ROTATION_X = new Item.StepItem(Material.STICK, "Right Rotation X", "Changes the x component of the display's right rotation", transformation.getRightRotation().x, 1.0, 0.1, true);
            RIGHT_ROTATION_Y = new Item.StepItem(Material.STICK, "Right Rotation Y", "Changes the y component of the display's right rotation", transformation.getRightRotation().y, 1.0, 0.1, true);
            RIGHT_ROTATION_Z = new Item.StepItem(Material.STICK, "Left Rotation Z", "Changes the z component of the display's right rotation", transformation.getRightRotation().z, 1.0, 0.1, true);
            RIGHT_ROTATION_ANGLE = new Item.StepItem(Material.MAGMA_CREAM, "Right Rotation Angle", "Changes the angle of the display's right rotation", BigDecimal.valueOf(Math.toDegrees(transformation.getRightRotation().angle())).setScale(2, RoundingMode.HALF_UP).doubleValue(), 10.0, 1.0, true);

            HITBOX_WIDTH = new Item.StepItem(Material.LEATHER, "Hitbox width", List.of("Changes the width of the display's hitbox.", "It will automatically set the hitbox size override to true,", "although you can change it to false in the GUI"), display.getHitboxWidth(), 1.0, 0.1, true);
            HITBOX_HEIGHT = new Item.StepItem(Material.RABBIT_HIDE, "Hitbox height", List.of("Changes the height of the display's hitbox.", "It will automatically set the hitbox size override to true,", "although you can change it to false in the GUI"), display.getHitboxHeight(), 1.0, 0.1, true);
        }
    }
}