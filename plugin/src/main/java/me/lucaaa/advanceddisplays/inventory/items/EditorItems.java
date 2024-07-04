package me.lucaaa.advanceddisplays.inventory.items;

import me.lucaaa.advanceddisplays.api.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.api.displays.BlockDisplay;
import me.lucaaa.advanceddisplays.api.displays.ItemDisplay;
import me.lucaaa.advanceddisplays.api.displays.TextDisplay;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.common.utils.Utils;
import me.lucaaa.advanceddisplays.inventory.InventoryUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        GLOW_TOGGLE = create(Material.GLOW_BERRIES, "Toggle glow", "Enables or disables the display's glowing status", display.isGlowing());
        GLOW_COLOR_SELECTOR = create(Material.GLOW_INK_SAC, "Change glow color", "Changes the display's glow color", ChatColor.of(new Color(display.getGlowColor().asRGB())) + "Preview");

        Location loc = display.getLocation();
        String location = BigDecimal.valueOf(loc.getX()).setScale(2, RoundingMode.HALF_UP).doubleValue() + ";" + BigDecimal.valueOf(loc.getY()).setScale(2, RoundingMode.HALF_UP).doubleValue() + ";" + BigDecimal.valueOf(loc.getZ()).setScale(2, RoundingMode.HALF_UP).doubleValue();
        TELEPORT = create(Material.ENDER_PEARL, "Teleport", "Teleports you to the display", location);
        MOVE_HERE = create(Material.CHORUS_FRUIT, "Move here", "Moves the display to your location", location);
        CENTER = create(Material.LIGHTNING_ROD, "Center", "Centers the display on the block it's on", location);

        BILLBOARD = create(Material.STRUCTURE_VOID, "Change billboard", "Changes the display's rotation axis", display.getBillboard().name());

        Material currentMaterial = switch (display.getType()) {
            case TEXT -> Material.OAK_SIGN;
            case ITEM -> ((ItemDisplay) display).getMaterial();
            case BLOCK -> ((BlockDisplay) display).getBlock().getMaterial();
        };
        String value = (display.getType() == DisplayType.TEXT) ? (((TextDisplay) display).getText().size() + " text animation(s)") : currentMaterial.name();
        CURRENT_VALUE = create(currentMaterial, "Display value", List.of("Changes what the display is displaying", "You must have an item in your cursor to", "change the value of block and item displays."), value, false, false, 0.0, 0.0);
        REMOVE = create(Material.BARRIER, "&cRemove", "Permanently removes this display", null);

        switch (display.getType()) {
            case BLOCK -> BLOCK_DATA = create(Material.COMMAND_BLOCK, "Block data", "Changes values that makes the block look different", ((BlockDisplay) display).getBlock().toString());
            case ITEM -> ITEM_TRANSFORMATION = create(Material.ARMOR_STAND, "Item model transform", "Changes how the displayed item is shown", ((ItemDisplay) display).getItemTransformation().name());
            case TEXT -> {
                TextDisplay textDisplay = (TextDisplay) display;

                TEXT_ALIGNMENT = create(Material.FILLED_MAP, "Text alignment", "Changes the text's alignment", textDisplay.getAlignment().name());
                BACKGROUND_COLOR = InventoryUtils.changeArmorColor(ColorItems.createPreview(textDisplay.getBackgroundColor(), true), textDisplay.getBackgroundColor());
                LINE_WIDTH = create(Material.BLACK_DYE, "Line width", List.of("Changes the text's line width"), textDisplay.getLineWidth(), true, true, 10.0, 1.0);
                TEXT_OPACITY = create(Material.GRAY_DYE, "Text opacity", List.of("Changes the text's opacity"), textDisplay.getTextOpacity(), true, true, 10.0, 1.0);
                USE_DEFAULT_BACKGROUND = create(Material.WHITE_DYE, "Use default background", "Changes whether the display uses the default background or not", textDisplay.getUseDefaultBackground());
                SEE_THROUGH = create(Material.TINTED_GLASS, "See through", "Changes whether the display can be seen through blocks or not", textDisplay.isSeeThrough());
                SHADOWED = create(Material.BLACK_STAINED_GLASS, "Shadowed", "Changes whether the text is shadowed or not", textDisplay.isShadowed());
                ANIMATION_TIME = create(Material.NAME_TAG, "Animation time", List.of("Changes how often the text changes", "Value must be in ticks"), textDisplay.getAnimationTime(), true, true, 10.0, 1.0);
                REFRESH_TIME = create(Material.NAME_TAG, "Refresh time", List.of("Changes how often placeholders update", "Value must be in ticks"), textDisplay.getRefreshTime(), true, true, 10.0, 1.0);
            }
        }
    }

    private ItemStack createLight(String title, String lore, Object value) {
        return create(Material.LIGHT, title, List.of(lore), value, true, false, 1.0, 0.1);
    }

    private ItemStack createShadow(String title, String rawLore, Object value) {
        return create(Material.COAL, title, List.of(rawLore), value, true, true, 1.0, 0.1);
    }

    // Create without clicks
    private ItemStack create(Material material, String title, String lore, Object value) {
        return create(material, title, List.of(lore), value, false, false, 0.0, 0.0);
    }

    private ItemStack create(Material material, String title, List<String> rawLore, Object value, boolean bigClicks, boolean smallClicks, double bigChange, double smallChange) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

        meta.setDisplayName(Utils.getColoredText("&6" + title));

        ArrayList<String> lore = new ArrayList<>(rawLore.stream().map(line -> "&e" + line).toList());
        if (bigClicks) {
            lore.add("");
            lore.add("&7Use &cLEFT_CLICK &7to add " + bigChange);
            lore.add("&7Use &cRIGHT_CLICK &7to subtract " + bigChange);
        }

        if (smallClicks) {
            if (!bigClicks) lore.add("");
            lore.add("&7Use &cSHIFT + LEFT_CLICK &7to add " + smallChange);
            lore.add("&7Use &cSHIFT + RIGHT_CLICK &7to subtract " + smallChange);
        }

        if (value != null) {
            lore.add("");
            lore.add("&9Current value: &7" + value);
        }

        meta.setLore(lore.stream().map(Utils::getColoredText).toList());

        item.setItemMeta(meta);
        return item;
    }
}