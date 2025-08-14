package me.lucaaa.advanceddisplays.inventory.inventories;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.data.Utils;
import me.lucaaa.advanceddisplays.inventory.Button;
import me.lucaaa.advanceddisplays.inventory.ADInventory;
import me.lucaaa.advanceddisplays.inventory.items.ColorItems;
import me.lucaaa.advanceddisplays.inventory.items.GlobalItems;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ColorGUI extends ADInventory {
    private final Material previewMaterial;
    private Color savedColor;
    private final boolean alphaEnabled;
    private ColorItems.ColorPreview preview;

    // This is run when clicking the check head. The super onDone method is null so, when closing the
    // inventory, it'll be as if the player had cancelled the editing (previous inv is opened with no changes)
    private final Consumer<Color> onDone;

    public ColorGUI(AdvancedDisplays plugin, DisplayEditorGUI previousInventory, BaseDisplay display, boolean alphaEnabled, Color initialColor, Consumer<Color> onDone, Runnable onClose) {
        this(plugin, previousInventory, Material.LEATHER_CHESTPLATE, display, alphaEnabled, initialColor, onDone, onClose);
    }

    public ColorGUI(AdvancedDisplays plugin, DisplayEditorGUI previousInventory, Material previewMaterial, BaseDisplay display, boolean alphaEnabled, Color initialColor, Consumer<Color> onDone, Runnable onClose) {
        super(plugin, Bukkit.createInventory(null, 27, Utils.getColoredText(("&6Editing glow color of: &e" + display.getName()))), List.of(), previousInventory, onClose);
        this.previewMaterial = previewMaterial;
        this.savedColor = initialColor;
        this.alphaEnabled = alphaEnabled;
        this.onDone = onDone;
    }

    @Override
    public void decorate() {
        ColorItems items = new ColorItems(previewMaterial, savedColor, alphaEnabled);
        preview = items.PREVIEW;

        // ---[ RED ]----
        ColorSet red = new ColorSet(this, ColorItems.ColorComponent.RED, 10, items.RED_PREVIEW);
        red.addSelector(0, items.RED_1);
        red.addSelector(9, items.RED_10);
        red.addSelector(18, items.RED_100);
        // ----------

        // ---[ GREEN ]----
        ColorSet green = new ColorSet(this, ColorItems.ColorComponent.GREEN, 12, items.GREEN_PREVIEW);
        green.addSelector(2, items.GREEN_1);
        green.addSelector(11, items.GREEN_10);
        green.addSelector(20, items.GREEN_100);
        // ----------

        // ---[ BLUE ]----
        ColorSet blue = new ColorSet(this, ColorItems.ColorComponent.BLUE, 14, items.BLUE_PREVIEW);
        blue.addSelector(4, items.BLUE_1);
        blue.addSelector(13, items.BLUE_10);
        blue.addSelector(22, items.BLUE_100);
        // ----------

        // ---[ ALPHA ]----
        if (alphaEnabled) {
            ColorSet alpha = new ColorSet(this, ColorItems.ColorComponent.ALPHA, 16, items.ALPHA_PREVIEW);
            alpha.addSelector(6, items.BLUE_1);
            alpha.addSelector(15, items.BLUE_10);
            alpha.addSelector(24, items.BLUE_100);
        }
        // ----------

        // ---[ BUTTONS & PREVIEW ]----
        addButton(8, new Button.InventoryButton<>(GlobalItems.cancel(plugin)) {
            @Override
            public void onClick(InventoryClickEvent event) {
                event.getWhoClicked().closeInventory();
            }
        });

        addButton(17, new Button.Unclickable<>(preview));

        addButton(26, new Button.InventoryButton<>(GlobalItems.done(plugin)) {
            @Override
            public void onClick(InventoryClickEvent event) {
                onDone.accept(savedColor);
                event.getWhoClicked().closeInventory();
            }
        });
        // ----------

        super.decorate();
    }

    public ColorItems.ColorPreview getPreview() {
        return preview;
    }

    public Color getSavedColor() {
        return savedColor;
    }

    public void setSavedColor(Color savedColor) {
        this.savedColor = savedColor;
    }

    public void addButton(int slot, Button<?> button) {
        super.addButton(slot, button);
    }

    private static class ColorSet {
        private final ColorGUI gui;
        private final ColorItems.ColorComponent component;
        private final int previewSlot;
        private final ColorItems.ColorPreview preview;
        private final Map<Integer, ColorItems.ColorSelector> selectors = new HashMap<>();

        public ColorSet(ColorGUI gui, ColorItems.ColorComponent component, int previewSlot, ColorItems.ColorPreview preview) {
            this.gui = gui;
            this.component = component;
            this.previewSlot = previewSlot;
            this.preview = preview;

            gui.addButton(previewSlot, new Button.Unclickable<>(preview));
        }

        public void addSelector(int slot, ColorItems.ColorSelector selector) {
            Button.InventoryButton<ColorItems.ColorSelector> button = new Button.InventoryButton<>(selector) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    switch (component) {
                        case RED -> {
                            int updated = updateColors(gui.getSavedColor().getRed(), event.isLeftClick(), getItem().getAmount());
                            gui.setSavedColor(gui.getSavedColor().setRed(updated));
                        }
                        case GREEN -> {
                            int updated = updateColors(gui.getSavedColor().getGreen(), event.isLeftClick(), getItem().getAmount());
                            gui.setSavedColor(gui.getSavedColor().setGreen(updated));
                        }
                        case BLUE -> {
                            int updated = updateColors(gui.getSavedColor().getBlue(), event.isLeftClick(), getItem().getAmount());
                            gui.setSavedColor(gui.getSavedColor().setBlue(updated));
                        }
                        case ALPHA -> {
                            int updated = updateColors(gui.getSavedColor().getAlpha(), event.isLeftClick(), getItem().getAmount());
                            gui.setSavedColor(gui.getSavedColor().setAlpha(updated));
                        }
                    }

                    preview.setColor(gui.getSavedColor());
                    gui.getInventory().setItem(previewSlot, preview.getStack());

                    gui.getPreview().setColor(gui.getSavedColor());
                    gui.getInventory().setItem(17, gui.getPreview().getStack());
                }
            };

            selectors.put(slot, selector);
            gui.addButton(slot, button);
        }

        public int updateColors(int oldValue, boolean increase, int amount) {
            int value = (increase) ? oldValue + amount : oldValue - amount;
            if (value > 255) {
                value = 255;
            } else if (value < 0) {
                value = 0;
            }

            for (Map.Entry<Integer, ColorItems.ColorSelector> entry : selectors.entrySet()) {
                entry.getValue().updateColor(value);
                gui.getInventory().setItem(entry.getKey(), entry.getValue().getStack());
            }

            return value;
        }
    }
}