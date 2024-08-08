package me.lucaaa.advanceddisplays.inventory.items;

import me.lucaaa.advanceddisplays.api.displays.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class EditorItems {
    public final Item BLOCK_LIGHT;
    public final Item SKY_LIGHT;

    public final Item SHADOW_RADIUS;
    public final Item SHADOW_STRENGTH;

    public final Item GLOW_TOGGLE;
    public final Item GLOW_COLOR_SELECTOR;

    public final Item TELEPORT;
    public final Item MOVE_HERE;
    public final Item CENTER;

    public final Item BILLBOARD;
    public final Item HITBOX_OVERRIDE;

    public final Item CURRENT_VALUE;
    public final Item REMOVE;

    // Display-specific
    public Item BLOCK_DATA;

    public Item ITEM_TRANSFORMATION;

    public Item TEXT_ALIGNMENT;
    public Item BACKGROUND_COLOR;
    public Item LINE_WIDTH;
    public Item TEXT_OPACITY;
    public Item USE_DEFAULT_BACKGROUND;
    public Item SEE_THROUGH;
    public Item SHADOWED;
    public Item ANIMATION_TIME;
    public Item REFRESH_TIME;

    public EditorItems(BaseDisplay display) {
        BLOCK_LIGHT = createLight("Block light", "Changes the block lighting component of the display", display.getBrightness().getBlockLight());
        SKY_LIGHT = createLight("Sky light", "Changes the sky lighting component of the display", display.getBrightness().getSkyLight());

        SHADOW_RADIUS = createShadow("Shadow Radius", "Changes how big the shadow is", display.getShadowRadius());
        SHADOW_STRENGTH = createShadow("Shadow Strength", "Changes how dark the shadow is", display.getShadowStrength());

        GLOW_TOGGLE = new Item(Material.GLOW_BERRIES, "Toggle glow", "Enables or disables the display's glowing status", display.isGlowing());
        GLOW_COLOR_SELECTOR = new Item(Material.GLOW_INK_SAC, "Glow color", "Changes the display's glow color", ChatColor.of(new Color(display.getGlowColor().asRGB())) + "Preview");

        Location loc = display.getLocation();
        String location = BigDecimal.valueOf(loc.getX()).setScale(2, RoundingMode.HALF_UP).doubleValue() + ";" + BigDecimal.valueOf(loc.getY()).setScale(2, RoundingMode.HALF_UP).doubleValue() + ";" + BigDecimal.valueOf(loc.getZ()).setScale(2, RoundingMode.HALF_UP).doubleValue();
        TELEPORT = new Item(Material.ENDER_PEARL, "Teleport", "Teleports you to the display", location);
        MOVE_HERE = new Item(Material.CHORUS_FRUIT, "Move here", "Moves the display to your location", location);
        CENTER = new Item(Material.LIGHTNING_ROD, "Center", "Centers the display on the block it's on", location);

        BILLBOARD = new Item(Material.STRUCTURE_VOID, "Change billboard", "Changes the display's rotation axis", display.getBillboard());
        HITBOX_OVERRIDE = new Item(Material.END_CRYSTAL, "Override hitbox size", List.of("Whether the hitbox size is set", "automatically or manually"), display.isHitboxSizeOverriden(), false);

        switch (display.getType()) {
            case TEXT -> CURRENT_VALUE = new Item(Material.OAK_SIGN, "Display text", List.of("Changes the text that is being displayed", "", "&7Use &cLEFT_CLICK &7to remove an animation", "&7Use &cRIGHT_CLICK &7to add an animation"), ((TextDisplay) display).getText().size() + " text animation(s)", false, false, 0.0, 0.0);
            case ITEM -> CURRENT_VALUE = new Item(((ItemDisplay) display).getItem().getType(), "Display item", List.of("Changes the item that is being displayed"/*, "You must have an item in your cursor."*/), ((ItemDisplay) display).getItem().getType(), false, false, 0.0, 0.0);
            case BLOCK -> CURRENT_VALUE = new Item(((BlockDisplay) display).getBlock().getMaterial(), "Display block", List.of("Changes the block that is being displayed"/*, "You must have a valid block in your cursor."*/), ((BlockDisplay) display).getBlock().getMaterial(), false, false, 0.0, 0.0);
            default -> CURRENT_VALUE = new Item(new ItemStack(Material.BARRIER));
        }
        REMOVE = new Item(Material.BARRIER, "&cRemove", "Permanently removes this display", null);

        switch (display.getType()) {
            case BLOCK -> BLOCK_DATA = new Item(Material.COMMAND_BLOCK, "Block data", "Changes values that makes the block look different", ((BlockDisplay) display).getBlock().toString());
            case ITEM -> ITEM_TRANSFORMATION = new Item(Material.ARMOR_STAND, "Item model transform", "Changes how the displayed item is shown", ((ItemDisplay) display).getItemTransformation());
            case TEXT -> {
                TextDisplay textDisplay = (TextDisplay) display;

                TEXT_ALIGNMENT = new Item(Material.FILLED_MAP, "Text alignment", "Changes the text's alignment", textDisplay.getAlignment().name());
                BACKGROUND_COLOR = new Item(new ItemStack(Material.LEATHER_CHESTPLATE)).setArmorColor(textDisplay.getBackgroundColor()).setPreviewLore(textDisplay.getBackgroundColor(), true, "Background Color");
                LINE_WIDTH = new Item(Material.BLACK_DYE, "Line width", List.of("Changes the display's line width"), textDisplay.getLineWidth(), true, true, 10.0, 1.0);
                TEXT_OPACITY = new Item(Material.GRAY_DYE, "Text opacity", List.of("Changes the text's opacity"), textDisplay.getTextOpacity(), true, true, 10.0, 1.0);
                USE_DEFAULT_BACKGROUND = new Item(Material.WHITE_DYE, "Use default background", "Changes whether the display uses the default background or not", textDisplay.getUseDefaultBackground());
                SEE_THROUGH = new Item(Material.TINTED_GLASS, "See through", "Changes whether the display can be seen through blocks or not", textDisplay.isSeeThrough());
                SHADOWED = new Item(Material.BLACK_STAINED_GLASS, "Shadowed", "Changes whether the text is shadowed or not", textDisplay.isShadowed());
                ANIMATION_TIME = new Item(Material.NAME_TAG, "Animation time", List.of("Changes how often the text changes", "Value must be in ticks"), textDisplay.getAnimationTime(), true, true, 10.0, 1.0);
                REFRESH_TIME = new Item(Material.NAME_TAG, "Refresh time", List.of("Changes how often placeholders update", "Value must be in ticks"), textDisplay.getRefreshTime(), true, true, 10.0, 1.0);
            }
        }
    }

    private Item createLight(String title, String lore, int value) {
        Item item = new Item(Material.LIGHT, title, List.of(lore), value, true, false, 1.0, 0.1);
        return item.setItemBrightness(value);
    }

    private Item createShadow(String title, String rawLore, float value) {
        return new Item(Material.COAL, title, List.of(rawLore), value, true, true, 1.0, 0.1);
    }
}