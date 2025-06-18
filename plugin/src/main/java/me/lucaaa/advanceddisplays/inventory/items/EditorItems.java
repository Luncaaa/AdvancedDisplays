package me.lucaaa.advanceddisplays.inventory.items;

import me.lucaaa.advanceddisplays.api.displays.*;
import me.lucaaa.advanceddisplays.data.Utils;
import me.lucaaa.advanceddisplays.displays.ADTextDisplay;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.Levelled;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditorItems {
    // Entity settings
    public final Item.BooleanItem ON_FIRE;
    public final Item.BooleanItem SPRINTING;
    public final Item.BooleanItem GLOW_TOGGLE;
    public final Item.EnumItem GLOW_COLOR;
    public final Item.ClickableItem CUSTOM_NAME;
    public final Item.EnumItem CUSTOM_NAME_VISIBILITY;

    public final Item.ClickableItem TELEPORT;
    public final Item.ClickableItem MOVE_HERE;
    public final Item.ClickableItem CENTER;

    // General display settings
    public Item.StepItem BLOCK_LIGHT;
    public Item.StepItem SKY_LIGHT;

    public Item.StepItem SHADOW_RADIUS;
    public Item.StepItem SHADOW_STRENGTH;

    public ColorItems.ColorPreview GLOW_COLOR_OVERRIDE;

    public Item.EnumItem BILLBOARD;
    public Item.BooleanItem HITBOX_OVERRIDE;
    public Item.ClickableItem ENTITY_SETTINGS;

    public Item.ClickableItem CURRENT_VALUE;
    public final Item.ClickableItem REMOVE;

    // Display-specific
    public Item.ClickableItem BLOCK_DATA;

    public Item.EnumItem ITEM_TRANSFORMATION;
    public Item.BooleanItem ENCHANTED;

    public Item.EnumItem TEXT_ALIGNMENT;
    public ColorItems.ColorPreview BACKGROUND_COLOR;
    public Item.StepItem LINE_WIDTH;
    public Item.StepItem TEXT_OPACITY;
    public Item.BooleanItem USE_DEFAULT_BACKGROUND;
    public Item.BooleanItem SEE_THROUGH;
    public Item.BooleanItem SHADOWED;
    public Item.StepItem ANIMATION_TIME;
    public Item.StepItem REFRESH_TIME;

    public EditorItems(BaseEntity entity) {
        ON_FIRE = new Item.BooleanItem(Material.CAMPFIRE, "On fire", "Changes whether the entity is on fire or not", entity.isOnFire());
        SPRINTING = new Item.BooleanItem(Material.IRON_BOOTS, "Sprinting", "Changes whether the entity appears to be sprinting or not", entity.isSprinting());
        GLOW_TOGGLE = new Item.BooleanItem(Material.GLOW_BERRIES, "Toggle glow", "Enables or disables the display's glowing status", entity.isGlowing());
        GLOW_COLOR = new Item.EnumItem(Material.RED_DYE, "Glow color", "Enables or disables the display's glowing status", entity.getGlowColor());
        if (entity.getCustomName() == null) {
            CUSTOM_NAME = new Item.ClickableItem(Material.NAME_TAG, "Custom name", List.of("Changes the entity's custom name", "", "&7Click to change"), Utils.getColoredText("&cNo custom name set"));
        } else {
            CUSTOM_NAME = new Item.ClickableItem(Material.NAME_TAG, "Custom name", List.of("Changes the entity's custom name", "", "&7Click to change"), Utils.getLegacyUnparsed(entity.getCustomName()));
        }
        CUSTOM_NAME_VISIBILITY = new Item.EnumItem(Material.REDSTONE, "Custom name visiblity", "Changes the entity's custom name visibility", entity.getCustomNameVisibility());

        Location loc = entity.getLocation();
        String location = BigDecimal.valueOf(loc.getX()).setScale(2, RoundingMode.HALF_UP).doubleValue() + ";" + BigDecimal.valueOf(loc.getY()).setScale(2, RoundingMode.HALF_UP).doubleValue() + ";" + BigDecimal.valueOf(loc.getZ()).setScale(2, RoundingMode.HALF_UP).doubleValue();
        TELEPORT = new Item.ClickableItem(Material.ENDER_PEARL, "Teleport", "Teleports you to the display", location);
        MOVE_HERE = new Item.ClickableItem(Material.CHORUS_FRUIT, "Move here", "Moves the display to your location", location);
        CENTER = new Item.ClickableItem(Material.LIGHTNING_ROD, "Center", "Centers the display on the block it's on", location);

        if (entity instanceof BaseDisplay display) {
            BLOCK_LIGHT = createLight("Block light", "Changes the block lighting component of the display", display.getBrightness().getBlockLight());
            SKY_LIGHT = createLight("Sky light", "Changes the sky lighting component of the display", display.getBrightness().getSkyLight());

            SHADOW_RADIUS = createShadow("Shadow Radius", "Changes how big the shadow is", display.getShadowRadius());
            SHADOW_STRENGTH = createShadow("Shadow Strength", "Changes how dark the shadow is", display.getShadowStrength());

            GLOW_COLOR_OVERRIDE = new ColorItems.ColorPreview("Glow color", display.getGlowColorOverride(), ColorItems.ColorComponent.ALL, false);

            BILLBOARD = new Item.EnumItem(Material.STRUCTURE_VOID, "Change billboard", "Changes the display's rotation axis", display.getBillboard());
            HITBOX_OVERRIDE = new Item.BooleanItem(Material.END_CRYSTAL, "Override hitbox size", List.of("Whether the hitbox size is set", "automatically or manually"), display.isHitboxSizeOverriden());
            ENTITY_SETTINGS = new Item.ClickableItem(Material.CREEPER_HEAD, "Entity settings", "Changes this entity's properties");

            switch (display.getType()) {
                case TEXT -> {
                    ADTextDisplay textDisplay = (ADTextDisplay) display;
                    List<String> lore = new ArrayList<>();
                    lore.add("Changes the text that is being displayed");
                    lore.add("");
                    lore.add("&7Use &cLEFT_CLICK &7to add an animation");
                    if (textDisplay.isNotEmpty()) {
                        lore.add("&7Use &cRIGHT_CLICK &7to remove an animation");
                    }
                    CURRENT_VALUE = new Item.ClickableItem(Material.OAK_SIGN, "Display text", lore, textDisplay.getTextsNumber() + " text animation(s)");
                    TEXT_ALIGNMENT = new Item.EnumItem(Material.FILLED_MAP, "Text alignment", "Changes the text's alignment", textDisplay.getAlignment());
                    BACKGROUND_COLOR = new ColorItems.ColorPreview("Background color", textDisplay.getBackgroundColor(), ColorItems.ColorComponent.ALL, true);
                    LINE_WIDTH = new Item.StepItem(Material.BLACK_DYE, "Line width", List.of("Changes the display's line width"), textDisplay.getLineWidth(), 10.0, 1.0);
                    TEXT_OPACITY = new Item.StepItem(Material.GRAY_DYE, "Text opacity", List.of("Changes the text's opacity"), textDisplay.getTextOpacity(), 10.0, 1.0);
                    USE_DEFAULT_BACKGROUND = new Item.BooleanItem(Material.WHITE_DYE, "Use default background", "Changes whether the display uses the default background or not", textDisplay.getUseDefaultBackground());
                    SEE_THROUGH = new Item.BooleanItem(Material.TINTED_GLASS, "See through", "Changes whether the display can be seen through blocks or not", textDisplay.isSeeThrough());
                    SHADOWED = new Item.BooleanItem(Material.BLACK_STAINED_GLASS, "Shadowed", "Changes whether the text is shadowed or not", textDisplay.isShadowed());
                    ANIMATION_TIME = new Item.StepItem(Material.NAME_TAG, "Animation time", List.of("Changes how often the text changes", "Value must be in ticks"), textDisplay.getAnimationTime(), 10.0, 1.0);
                    REFRESH_TIME = new Item.StepItem(Material.NAME_TAG, "Refresh time", List.of("Changes how often placeholders update", "Value must be in ticks"), textDisplay.getRefreshTime(), 10.0, 1.0);
                }
                case ITEM -> {
                    CURRENT_VALUE = new Item.ClickableItem(((ItemDisplay) display).getItem(), "Display item", List.of("The item that is being displayed", "", "&7Click to change"), ((ItemDisplay) display).getItem().getType().name());
                    ITEM_TRANSFORMATION = new Item.EnumItem(Material.ARMOR_STAND, "Item model transform", "Changes how the displayed item is shown", ((ItemDisplay) display).getItemTransformation());
                    ENCHANTED = new Item.BooleanItem(Material.ENCHANTED_BOOK, "Enchanted", "Changes whether the enchanted effect is visible or not", ((ItemDisplay) display).isEnchanted());
                }
                case BLOCK -> {
                    CURRENT_VALUE = new Item.ClickableItem(((BlockDisplay) display).getBlock().getMaterial(), "Display block", List.of("The block that is being displayed", "", "&7Click to change"), ((BlockDisplay) display).getBlock().getMaterial().name());
                    BLOCK_DATA = new Item.ClickableItem(Material.COMMAND_BLOCK, "Block data", "Changes values that makes the block look different", ((BlockDisplay) display).getBlock().toString());
                } // Block data will be set in the EditorGUI class once it's opened.
                default -> CURRENT_VALUE = new Item.ClickableItem(Material.BARRIER, "&cUnextected error", "&7Report to developer.");
            }
        }

        REMOVE = new Item.ClickableItem(Material.BARRIER, "&cRemove", List.of("Permanently removes this display", "&cThis action cannot be undone!"), null);
    }

    private Item.StepItem createLight(String title, String lore, int value) {
        Item.StepItem item = new Item.StepItem(Material.LIGHT, title, List.of(lore), value, 1.0);
        setBrightness(item.getStack(), value);
        return item;
    }

    public static void setBrightness(ItemStack item, int brightness) {
        BlockDataMeta meta = (BlockDataMeta) Objects.requireNonNull(item.getItemMeta());
        Levelled data = (Levelled) Material.LIGHT.createBlockData();
        data.setLevel(brightness);
        meta.setBlockData(data);
        item.setItemMeta(meta);
    }

    private Item.StepItem createShadow(String title, String rawLore, float value) {
        return new Item.StepItem(Material.COAL, title, List.of(rawLore), value, 1.0, 0.1);
    }
}