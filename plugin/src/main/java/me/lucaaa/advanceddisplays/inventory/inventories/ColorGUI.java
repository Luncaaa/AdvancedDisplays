package me.lucaaa.advanceddisplays.inventory.inventories;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.common.utils.Utils;
import me.lucaaa.advanceddisplays.inventory.InventoryButton;
import me.lucaaa.advanceddisplays.inventory.InventoryMethods;
import me.lucaaa.advanceddisplays.inventory.InventoryUtils;
import me.lucaaa.advanceddisplays.inventory.items.ColorItems;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.function.Consumer;

public class ColorGUI extends InventoryMethods {
    private final EditorGUI previous;
    private Color savedColor;
    private final boolean alphaEnabled;
    private final Consumer<Color> onDone;

    public ColorGUI(AdvancedDisplays plugin, EditorGUI previousInventory, BaseDisplay display, boolean alphaEnabled, Color initialColor, Consumer<Color> onDone) {
        super(plugin, Bukkit.createInventory(null, 27, Utils.getColoredText(("&6Editing glow color of: &e" + display.getName()))));
        this.previous = previousInventory;
        this.savedColor = initialColor;
        this.alphaEnabled = alphaEnabled;
        this.onDone = onDone;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == this.getInventory() || event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            event.setCancelled(true);
        }

        super.onClick(event);
    }

    @Override
    public void decorate() {
        ColorItems items = new ColorItems(savedColor, alphaEnabled);
        InventoryButton redPreview = new InventoryButton(items.RED_PREVIEW) {
            @Override
            public void onClick(InventoryClickEvent event) {}
        };
        InventoryButton greenPreview = new InventoryButton(items.GREEN_PREVIEW) {
            @Override
            public void onClick(InventoryClickEvent event) {}
        };
        InventoryButton bluePreview = new InventoryButton(items.BLUE_PREVIEW) {
            @Override
            public void onClick(InventoryClickEvent event) {}
        };
        InventoryButton alphaPreview = new InventoryButton(items.ALPHA_PREVIEW) {
            @Override
            public void onClick(InventoryClickEvent event) {}
        };
        InventoryButton preview = new InventoryButton(items.PREVIEW) {
            @Override
            public void onClick(InventoryClickEvent event) {}
        };

        // ---[ RED ]----
        addButton(0, new InventoryButton(items.RED_1) {
            @Override
            public void onClick(InventoryClickEvent event) {
                savedColor = savedColor.setRed(InventoryUtils.getUpdatedColor(redPreview.getItem(), savedColor.getRed(), 1, event.isLeftClick(), ColorItems.ColorComponent.RED));
                updateItems(10, redPreview.getItem(), preview.getItem());
            }
        });

        addButton(9, new InventoryButton(items.RED_10) {
            @Override
            public void onClick(InventoryClickEvent event) {
                savedColor = savedColor.setRed(InventoryUtils.getUpdatedColor(redPreview.getItem(), savedColor.getRed(), 10, event.isLeftClick(), ColorItems.ColorComponent.RED));
                updateItems(10, redPreview.getItem(), preview.getItem());
            }
        });

        addButton(18, new InventoryButton(items.RED_100) {
            @Override
            public void onClick(InventoryClickEvent event) {
                savedColor = savedColor.setRed(InventoryUtils.getUpdatedColor(redPreview.getItem(), savedColor.getRed(), 100, event.isLeftClick(), ColorItems.ColorComponent.RED));
                updateItems(10, redPreview.getItem(), preview.getItem());
            }
        });

        addButton(10, redPreview);
        // ----------

        // ---[ GREEN ]----
        addButton(2, new InventoryButton(items.GREEN_1) {
            @Override
            public void onClick(InventoryClickEvent event) {
                savedColor = savedColor.setGreen(InventoryUtils.getUpdatedColor(greenPreview.getItem(), savedColor.getGreen(), 1, event.isLeftClick(), ColorItems.ColorComponent.GREEN));
                updateItems(12, greenPreview.getItem(), preview.getItem());
            }
        });

        addButton(11, new InventoryButton(items.GREEN_10) {
            @Override
            public void onClick(InventoryClickEvent event) {
                savedColor = savedColor.setGreen(InventoryUtils.getUpdatedColor(greenPreview.getItem(), savedColor.getGreen(), 10, event.isLeftClick(), ColorItems.ColorComponent.GREEN));
                updateItems(12, greenPreview.getItem(), preview.getItem());
            }
        });

        addButton(20, new InventoryButton(items.GREEN_100) {
            @Override
            public void onClick(InventoryClickEvent event) {
                savedColor = savedColor.setGreen(InventoryUtils.getUpdatedColor(greenPreview.getItem(), savedColor.getGreen(), 100, event.isLeftClick(), ColorItems.ColorComponent.GREEN));
                updateItems(12, greenPreview.getItem(), preview.getItem());
            }
        });

        addButton(12, greenPreview);
        // ----------

        // ---[ BLUE ]----
        addButton(4, new InventoryButton(items.BLUE_1) {
            @Override
            public void onClick(InventoryClickEvent event) {
                savedColor = savedColor.setBlue(InventoryUtils.getUpdatedColor(bluePreview.getItem(), savedColor.getBlue(), 1, event.isLeftClick(), ColorItems.ColorComponent.BLUE));
                updateItems(14, bluePreview.getItem(), preview.getItem());
            }
        });

        addButton(13, new InventoryButton(items.BLUE_10) {
            @Override
            public void onClick(InventoryClickEvent event) {
                savedColor = savedColor.setBlue(InventoryUtils.getUpdatedColor(bluePreview.getItem(), savedColor.getBlue(), 10, event.isLeftClick(), ColorItems.ColorComponent.BLUE));
                updateItems(14, bluePreview.getItem(), preview.getItem());
            }
        });

        addButton(22, new InventoryButton(items.BLUE_100) {
            @Override
            public void onClick(InventoryClickEvent event) {
                savedColor = savedColor.setBlue(InventoryUtils.getUpdatedColor(bluePreview.getItem(), savedColor.getBlue(), 100, event.isLeftClick(), ColorItems.ColorComponent.BLUE));
                updateItems(14, bluePreview.getItem(), preview.getItem());
            }
        });

        addButton(14, bluePreview);
        // ----------

        // ---[ ALPHA ]----
        if (alphaEnabled) {
            addButton(6, new InventoryButton(items.ALPHA_1) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    savedColor = savedColor.setAlpha(InventoryUtils.getUpdatedColor(alphaPreview.getItem(), savedColor.getAlpha(), 1, event.isLeftClick(), ColorItems.ColorComponent.ALPHA));
                    updateItems(16, alphaPreview.getItem(), preview.getItem());
                }
            });

            addButton(15, new InventoryButton(items.ALPHA_10) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    savedColor = savedColor.setAlpha(InventoryUtils.getUpdatedColor(alphaPreview.getItem(), savedColor.getAlpha(), 10, event.isLeftClick(), ColorItems.ColorComponent.ALPHA));
                    updateItems(16, alphaPreview.getItem(), preview.getItem());
                }
            });

            addButton(24, new InventoryButton(items.ALPHA_100) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    savedColor = savedColor.setAlpha(InventoryUtils.getUpdatedColor(alphaPreview.getItem(), savedColor.getAlpha(), 100, event.isLeftClick(), ColorItems.ColorComponent.ALPHA));
                    updateItems(16, alphaPreview.getItem(), preview.getItem());
                }
            });

            addButton(16, alphaPreview);
        }
        // ----------

        // ---[ BUTTONS & PREVIEW ]----
        addButton(8, new InventoryButton(items.CANCEL) {
            @Override
            public void onClick(InventoryClickEvent event) {
                onClose((Player) event.getWhoClicked());
            }
        });

        addButton(17, preview);

        addButton(26, new InventoryButton(items.DONE) {
            @Override
            public void onClick(InventoryClickEvent event) {
                onDone.accept(savedColor);
                onClose((Player) event.getWhoClicked());
            }
        });
        // ----------

        super.decorate();
    }

    @Override
    public void onClose(Player player) {
        player.closeInventory();
        // The task is run so that the InventoryCloseEvent is fully run before opening a new inventory.
        // Otherwise, the inventory will open but won't be registered as a plugin's GUI.
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getInventoryManager().handleOpen(player, previous);
            }
        }.runTaskLater(plugin, 0L);
    }

    private void updateItems(int slot, ItemStack item, ItemStack previewItem) {
        getInventory().setItem(slot, item);
        InventoryUtils.changeArmorColor(previewItem, savedColor);
        previewItem.setItemMeta(ColorItems.setPreviewLore(Objects.requireNonNull(previewItem.getItemMeta()), savedColor, alphaEnabled));
        getInventory().setItem(17, previewItem);
    }
}
