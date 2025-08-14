package me.lucaaa.advanceddisplays.inventory.inventories;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.data.Utils;
import me.lucaaa.advanceddisplays.inventory.ADInventory;
import me.lucaaa.advanceddisplays.inventory.Button;
import me.lucaaa.advanceddisplays.inventory.items.GlobalItems;
import me.lucaaa.advanceddisplays.inventory.items.Item;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Consumer;

public class BannerEditorGUI extends ADInventory {
    private final int startLayer;
    private final Map<Integer, Pattern> patterns;
    private final Material banner; // For banner color
    private final Consumer<BannerMeta> onDone;
    private final Runnable onClose;
    private final ItemStack DISABLED_LAYER;

    public BannerEditorGUI(AdvancedDisplays plugin, ADInventory previous, Material banner, BannerMeta meta, Consumer<BannerMeta> onDone, Runnable onClose) {
        this(plugin, previous, 1, getPatterns(meta), banner, onDone, onClose);
    }

    public BannerEditorGUI(AdvancedDisplays plugin, ADInventory previous, int startLayer, Map<Integer, Pattern> patterns, Material banner, Consumer<BannerMeta> onDone, Runnable onClose) {
        super(plugin, Bukkit.createInventory(null, 54, Utils.getColoredText(("&6Banner editor"))), List.of(), previous, onClose);

        this.startLayer = startLayer;
        this.patterns = patterns;
        this.banner = banner;
        this.onDone = onDone;
        this.onClose = onClose;

        this.DISABLED_LAYER = new ItemStack(Material.BARRIER);
        ItemMeta disabledMeta = Objects.requireNonNull(DISABLED_LAYER.getItemMeta());
        disabledMeta.setDisplayName(Utils.getColoredText("&cClick the button on top to add this layer."));
        Utils.hideFlags(disabledMeta);
        DISABLED_LAYER.setItemMeta(disabledMeta);
    }

    @Override
    public void decorate() {
        int slot = 0; // Top left
        for (int layer = startLayer; layer <= startLayer + 8; layer++) {
            if (patterns.get(layer) == null) {
                setDisabledLayer(slot, layer);
                slot++;
                continue;
            }

            Pattern pattern = patterns.get(layer);
            setLayerButtons(slot, layer, pattern.getPattern(), pattern.getColor());
            slot++;
        }

        // Previous button
        if (startLayer > 1) {
            addButton(45, new Button.InventoryButton<>(new Item.ClickableItem(plugin.cachedHeads.LEFT, "Previous layer", List.of("See one more layer to the bottom"), null)) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    shouldOpenPrevious = false;
                    plugin.getInventoryManager().handleOpen((Player) event.getWhoClicked(), new BannerEditorGUI(plugin, previous, startLayer - 1, patterns, banner, onDone, onClose));
                }
            });
        }

        // Cancel button
        addButton(48, new Button.InventoryButton<>(GlobalItems.cancel(plugin)) {
            @Override
            public void onClick(InventoryClickEvent event) {
                event.getWhoClicked().closeInventory();
            }
        });

        // Banner preview
        getInventory().setItem(49, previewBanner());

        // Done button
        addButton(50, new Button.InventoryButton<>(GlobalItems.done(plugin)) {
            @Override
            public void onClick(InventoryClickEvent event) {
                onDone.accept(getMeta());
                event.getWhoClicked().closeInventory();
            }
        });

        // Next button
        addButton(53, new Button.InventoryButton<>(new Item.ClickableItem(plugin.cachedHeads.RIGHT, "Next layer", List.of("See one more layer on top"), null)) {
            @Override
            public void onClick(InventoryClickEvent event) {
                shouldOpenPrevious = false;
                plugin.getInventoryManager().handleOpen((Player) event.getWhoClicked(), new BannerEditorGUI(plugin, previous, startLayer + 1, patterns, banner, onDone, onClose));
            }
        });

        super.decorate();
    }

    private void setDisabledLayer(int topSlotIndex, int layer) {
        patterns.put(layer, null);
        // 8 because -1 +9 (-1 to get the real index and +9 so it is 2nd row)
        removeButton(topSlotIndex + 18); // Pattern type button
        removeButton(topSlotIndex + 27); // Dye button
        getInventory().setItem(topSlotIndex + 18, DISABLED_LAYER);
        getInventory().setItem(topSlotIndex + 27, DISABLED_LAYER);
        // getInventory().setItem(topSlotIndex + 27, DISABLED_LAYER); // Uncomment if preview and type selector are different buttons

        // Add layer move buttons
        addMoveLayerButtons(topSlotIndex, layer);

        // Replace remove button with add one.
        addButton(topSlotIndex, getAddButton(topSlotIndex, layer));

        // Update the preview item
        updatePreview();
    }

    private void setLayerButtons(int topSlotIndex, int layer, PatternType pattern, DyeColor color) {
        Pattern newPattern = new Pattern(color, pattern);
        patterns.put(layer, newPattern);
        // Layer preview
        /* Removed due to the need for space (combined preview and pattern type selector)
        addButton(topSlotIndex + 9, new Button.InventoryButton<>(new Item.ClickableItem(previewBanner(this.banner, pattern, color), "Layer " + layer + " preview", List.of("Preview the pattern in layer " + layer), pattern.name() + ":" + color.name())) {
            @Override
            public void onClick(InventoryClickEvent event) {}
        });
        */

        // Pattern type button
        // Use previewBanner(Material.WHITE_BANNER, pattern, DyeColor.BLACK) for "generic" type selector (independent of background and stripe colors)
        addButton(topSlotIndex + 18, new Button.InventoryButton<>(new Item.ClickableItem(previewBanner(this.banner, pattern, color), "Layer " + layer + " pattern type", List.of("Click to select another pattern type"), pattern.name())) {
            @Override
            public void onClick(InventoryClickEvent event) {
                SelectorGUI<PatternType> inventory = new SelectorGUI<>(
                        plugin,
                        BannerEditorGUI.this,
                        PatternType.values(),
                        value -> {
                            ItemStack banner =  new ItemStack(BannerEditorGUI.this.banner);
                            BannerMeta meta = (BannerMeta) Objects.requireNonNull(banner.getItemMeta());
                            meta.addPattern(new Pattern(color, value));
                            banner.setItemMeta(meta);
                            return banner;
                        },
                        selected -> setLayerButtons(topSlotIndex, layer, selected, color),
                        () -> shouldOpenPrevious = true
                );

                shouldOpenPrevious = false;
                plugin.getInventoryManager().handleOpen((Player) event.getWhoClicked(), inventory);
            }
        });

        // Dye button
        addButton(topSlotIndex + 27, new Button.InventoryButton<>(new Item.ClickableItem(Material.valueOf(color.name() + "_DYE"), "Layer " + layer + " pattern color", "Click to select another color", color.name())) {
            @Override
            public void onClick(InventoryClickEvent event) {
                SelectorGUI<DyeColor> inventory = new SelectorGUI<>(
                        plugin,
                        BannerEditorGUI.this,
                        color.getDeclaringClass().getEnumConstants(),
                        value -> new ItemStack(Material.valueOf(value.name() + "_DYE")),
                        selected -> setLayerButtons(topSlotIndex, layer, pattern, selected),
                        () -> shouldOpenPrevious = true
                );

                shouldOpenPrevious = false;
                plugin.getInventoryManager().handleOpen((Player) event.getWhoClicked(), inventory);
            }
        });

        // Add layer move buttons
        addMoveLayerButtons(topSlotIndex, layer);

        // Replace add button with remove one.
        addButton(topSlotIndex, getRemoveButton(topSlotIndex, layer));

        // Update the preview item
        updatePreview();
    }

    private Button.InventoryButton<Item.ClickableItem> getAddButton(int topSlotIndex, int layer) {
        ItemStack item = plugin.cachedHeads.ADD.clone();
        item.setAmount(Math.min(layer, 64));
        Item.ClickableItem addItem = new Item.ClickableItem(item, "Add layer " + layer, List.of("Adds a pattern in layer " + layer), null);
        return new Button.InventoryButton<>(addItem) {
            @Override
            public void onClick(InventoryClickEvent event) {
                setLayerButtons(topSlotIndex, layer, PatternType.BASE, DyeColor.ORANGE);
                updatePreview();
            }
        };
    }

    private Button.InventoryButton<Item.ClickableItem> getRemoveButton(int topSlotIndex, int layer) {
        ItemStack item = plugin.cachedHeads.CANCEL.clone();
        item.setAmount(Math.min(layer, 64));
        Item.ClickableItem removeItem = new Item.ClickableItem(item, "Remove layer " + layer, List.of("Removes the pattern in layer " + layer), null);
        return new Button.InventoryButton<>(removeItem) {
            @Override
            public void onClick(InventoryClickEvent event) {
                setDisabledLayer(topSlotIndex, layer);
            }
        };
    }

    private void addMoveLayerButtons(int topSlotIndex, int layer) {
        List<String> lore = new ArrayList<>();
        lore.add("");

        // Only show the move right button if it's not the last layer in the GUI
        if (topSlotIndex < 8) {
            lore.add("&7Use &cLEFT_CLICK &7to move this layer 1 position above");
        } else {
            lore.add("&7Scroll using the button on the bottom right");
            lore.add("&7to be able to move this layer up");
        }

        // Only show the move right button if it's not the first layer in the GUI
        if (layer > 1 && topSlotIndex > 0) {
            lore.add("&7Use &cRIGHT_CLICK &7to move this layer 1 position below");
        } else {
            if (layer > 1) {
                lore.add("&7Scroll using the button on the bottom left");
                lore.add("&7to be able to move this layer below");
            } else {
                lore.add("&7You cannot move the bottom layer further below");
            }
        }

        addButton(topSlotIndex + 9, new Button.InventoryButton<>(new Item.ClickableItem(Material.PISTON, "Move layer", lore, null)) {
            @Override
            public void onClick(InventoryClickEvent event) {
                if (event.isLeftClick() && topSlotIndex < 8) {
                    handleReplaceLayers(layer, topSlotIndex, layer + 1, topSlotIndex + 1);
                } else if (event.isRightClick() && layer > 1 && topSlotIndex > 0) {
                    handleReplaceLayers(layer, topSlotIndex, layer - 1, topSlotIndex - 1);
                }
            }
        });
    }

    private void handleReplaceLayers(int layer1, int topSlotIndex1, int layer2, int topSlotIndex2) {
        Pattern layer1Pattern = patterns.get(layer1);
        Pattern layer2Pattern = patterns.get(layer2);

        if (layer1Pattern == null) {
            setDisabledLayer(topSlotIndex2, layer2);
        } else {
            setLayerButtons(topSlotIndex2, layer2, layer1Pattern.getPattern(), layer1Pattern.getColor());
        }

        if (layer2Pattern == null) {
            setDisabledLayer(topSlotIndex1, layer1);
        } else {
            setLayerButtons(topSlotIndex1, layer1, layer2Pattern.getPattern(), layer2Pattern.getColor());
        }
    }

    private ItemStack previewBanner(Material bannerMat, PatternType pattern, DyeColor color) {
        ItemStack banner = new ItemStack(bannerMat);
        BannerMeta meta = (BannerMeta) Objects.requireNonNull(banner.getItemMeta());
        meta.addPattern(new Pattern(color, pattern));
        banner.setItemMeta(meta);
        return banner;
    }

    private BannerMeta getMeta() {
        BannerMeta meta = (BannerMeta) Objects.requireNonNull(plugin.getServer().getItemFactory().getItemMeta(banner));

        List<Pattern> patternsList = new ArrayList<>();

        List<Integer> sortedKeys = new ArrayList<>(patterns.keySet());
        sortedKeys.sort(Integer::compareTo);

        if (!sortedKeys.isEmpty()) {
            int min = sortedKeys.get(0);
            int max = sortedKeys.get(sortedKeys.size() - 1);

            for (int i = min; i <= max; i++) {
                Pattern pattern = patterns.get(i);
                if (pattern != null) patternsList.add(pattern);
            }
        }

        meta.setPatterns(patternsList);
        return meta;
    }

    private static Map<Integer, Pattern> getPatterns(BannerMeta meta) {
        Map<Integer, Pattern> patterns = new HashMap<>();
        int layer = 1;
        for (Pattern pattern : meta.getPatterns()) {
            patterns.put(layer, pattern);
            layer++;
        }
        return patterns;
    }

    private void updatePreview() {
        getInventory().setItem(49, previewBanner());
    }

    private ItemStack previewBanner() {
        ItemStack bannerItem = new ItemStack(banner);
        BannerMeta meta = getMeta();
        meta.setDisplayName(Utils.getColoredText("&6Banner preview"));
        bannerItem.setItemMeta(meta);
        return bannerItem;
    }
}