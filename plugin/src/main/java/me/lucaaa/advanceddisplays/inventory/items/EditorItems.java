package me.lucaaa.advanceddisplays.inventory.items;

import me.lucaaa.advanceddisplays.api.displays.*;
import me.lucaaa.advanceddisplays.inventory.InventoryUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class EditorItems {
    public final ItemStack BLOCK_LIGHT;
    public final ItemStack SKY_LIGHT;

    public final ItemStack SHADOW_RADIUS;
    public final ItemStack SHADOW_STRENGTH;

    public final ItemStack GLOW_TOGGLE;
    public final ItemStack GLOW_COLOR_SELECTOR;

    public final ItemStack TELEPORT;
    public final ItemStack MOVE_HERE;
    public final ItemStack CENTER;

    public final ItemStack BILLBOARD;

    public final ItemStack CURRENT_VALUE;
    public final ItemStack REMOVE;

    // Display-specific
    public ItemStack BLOCK_DATA;

    public ItemStack ITEM_TRANSFORMATION;

    public ItemStack TEXT_ALIGNMENT;
    public ItemStack BACKGROUND_COLOR;
    public ItemStack LINE_WIDTH;
    public ItemStack TEXT_OPACITY;
    public ItemStack USE_DEFAULT_BACKGROUND;
    public ItemStack SEE_THROUGH;
    public ItemStack SHADOWED;
    public ItemStack ANIMATION_TIME;
    public ItemStack REFRESH_TIME;

    public EditorItems(BaseDisplay display) {
        BLOCK_LIGHT = InventoryUtils.setItemBrightness(createLight("Block light", "Changes the block lighting component of the display", display.getBrightness().getBlockLight()), display.getBrightness().getBlockLight());
        SKY_LIGHT = InventoryUtils.setItemBrightness(createLight("Sky light", "Changes the sky lighting component of the display", display.getBrightness().getSkyLight()), display.getBrightness().getSkyLight());

        SHADOW_RADIUS = createShadow("Shadow Radius", "Changes how big the shadow is", display.getShadowRadius());
        SHADOW_STRENGTH = createShadow("Shadow Strength", "Changes how dark the shadow is", display.getShadowStrength());

        GLOW_TOGGLE = GlobalItems.create(Material.GLOW_BERRIES, "Toggle glow", "Enables or disables the display's glowing status", display.isGlowing());
        GLOW_COLOR_SELECTOR = GlobalItems.create(Material.GLOW_INK_SAC, "Glow color", "Changes the display's glow color", ChatColor.of(new Color(display.getGlowColor().asRGB())) + "Preview");

        Location loc = display.getLocation();
        String location = BigDecimal.valueOf(loc.getX()).setScale(2, RoundingMode.HALF_UP).doubleValue() + ";" + BigDecimal.valueOf(loc.getY()).setScale(2, RoundingMode.HALF_UP).doubleValue() + ";" + BigDecimal.valueOf(loc.getZ()).setScale(2, RoundingMode.HALF_UP).doubleValue();
        TELEPORT = GlobalItems.create(Material.ENDER_PEARL, "Teleport", "Teleports you to the display", location);
        MOVE_HERE = GlobalItems.create(Material.CHORUS_FRUIT, "Move here", "Moves the display to your location", location);
        CENTER = GlobalItems.create(Material.LIGHTNING_ROD, "Center", "Centers the display on the block it's on", location);

        BILLBOARD = GlobalItems.create(Material.STRUCTURE_VOID, "Change billboard", "Changes the display's rotation axis", display.getBillboard().name());

        switch (display.getType()) {
            case TEXT -> CURRENT_VALUE = GlobalItems.create(Material.OAK_SIGN, "Display text", List.of("Changes the text that is being displayed", "", "&7Use &cLEFT_CLICK &7to remove an animation", "&7Use &cRIGHT_CLICK &7to add an animation"), ((TextDisplay) display).getText().size() + " text animation(s)", false, false, 0.0, 0.0);
            case ITEM -> CURRENT_VALUE = GlobalItems.create(((ItemDisplay) display).getMaterial(), "Display item", List.of("Changes the item that is being displayed", "You must have an item in your cursor."), ((ItemDisplay) display).getMaterial(), false, false, 0.0, 0.0);
            case BLOCK -> CURRENT_VALUE = GlobalItems.create(((BlockDisplay) display).getBlock().getMaterial(), "Display block", List.of("Changes the block that is being displayed", "You must have a valid block in your cursor."), ((BlockDisplay) display).getBlock().getMaterial(), false, false, 0.0, 0.0);
            default -> CURRENT_VALUE = new ItemStack(Material.BARRIER);
        }
        REMOVE = GlobalItems.create(Material.BARRIER, "&cRemove", "Permanently removes this display", null);

        switch (display.getType()) {
            case BLOCK -> BLOCK_DATA = GlobalItems.create(Material.COMMAND_BLOCK, "Block data", "Changes values that makes the block look different", ((BlockDisplay) display).getBlock().toString());
            case ITEM -> ITEM_TRANSFORMATION = GlobalItems.create(Material.ARMOR_STAND, "Item model transform", "Changes how the displayed item is shown", ((ItemDisplay) display).getItemTransformation().name());
            case TEXT -> {
                TextDisplay textDisplay = (TextDisplay) display;

                TEXT_ALIGNMENT = GlobalItems.create(Material.FILLED_MAP, "Text alignment", "Changes the text's alignment", textDisplay.getAlignment().name());
                BACKGROUND_COLOR = InventoryUtils.changeArmorColor(ColorItems.createPreview(textDisplay.getBackgroundColor(), true, "Background Color"), textDisplay.getBackgroundColor());
                LINE_WIDTH = GlobalItems.create(Material.BLACK_DYE, "Line width", List.of("Changes the text's line width"), textDisplay.getLineWidth(), true, true, 10.0, 1.0);
                TEXT_OPACITY = GlobalItems.create(Material.GRAY_DYE, "Text opacity", List.of("Changes the text's opacity"), textDisplay.getTextOpacity(), true, true, 10.0, 1.0);
                USE_DEFAULT_BACKGROUND = GlobalItems.create(Material.WHITE_DYE, "Use default background", "Changes whether the display uses the default background or not", textDisplay.getUseDefaultBackground());
                SEE_THROUGH = GlobalItems.create(Material.TINTED_GLASS, "See through", "Changes whether the display can be seen through blocks or not", textDisplay.isSeeThrough());
                SHADOWED = GlobalItems.create(Material.BLACK_STAINED_GLASS, "Shadowed", "Changes whether the text is shadowed or not", textDisplay.isShadowed());
                ANIMATION_TIME = GlobalItems.create(Material.NAME_TAG, "Animation time", List.of("Changes how often the text changes", "Value must be in ticks"), textDisplay.getAnimationTime(), true, true, 10.0, 1.0);
                REFRESH_TIME = GlobalItems.create(Material.NAME_TAG, "Refresh time", List.of("Changes how often placeholders update", "Value must be in ticks"), textDisplay.getRefreshTime(), true, true, 10.0, 1.0);
            }
        }
    }

    private ItemStack createLight(String title, String lore, Object value) {
        return GlobalItems.create(Material.LIGHT, title, List.of(lore), value, true, false, 1.0, 0.1);
    }

    private ItemStack createShadow(String title, String rawLore, Object value) {
        return GlobalItems.create(Material.COAL, title, List.of(rawLore), value, true, true, 1.0, 0.1);
    }
}