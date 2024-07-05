package me.lucaaa.advanceddisplays.inventory.inventories;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.*;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.common.utils.Utils;
import me.lucaaa.advanceddisplays.inventory.*;
import me.lucaaa.advanceddisplays.inventory.items.EditorItems;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class EditorGUI extends InventoryMethods {
    private final BaseDisplay display;

    public EditorGUI(AdvancedDisplays plugin, BaseDisplay display) {
        super(plugin, Bukkit.createInventory(null, 27, Utils.getColoredText(("&6Editing " + display.getType().name() + " display: &e" + display.getName()))));
        this.display = display;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (display.getType() == DisplayType.TEXT || event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            event.setCancelled(true);

        } else {
            // 13 is the display value slot
            if (event.getClickedInventory() == this.getInventory() && (event.getSlot() != 13 || Objects.requireNonNull(event.getCursor()).getType() == Material.AIR)) {
                event.setCancelled(true);

            } else if (display.getType() == DisplayType.BLOCK && event.getCurrentItem() != null) {
                try {
                    // If the material is not block, the player won't be able to pick it up.
                    event.getCurrentItem().getType().createBlockData();
                } catch (IllegalArgumentException | NullPointerException ignored) {
                    event.setCancelled(true);
                }
            }
        }

        if (event.getClickedInventory() == this.getInventory()) {
            super.onClick(event);
        }
    }

    @Override
    public void decorate() {
        EditorItems items = new EditorItems(this.display);

        // ---[ BRIGHTNESS ]----
        addButton(0, new InventoryButton(items.BLOCK_LIGHT) {
            @Override
            public void onClick(InventoryClickEvent event) {
                int newBrightness = InventoryUtils.setNewItemBrightness(getItem(), display.getBrightness().getBlockLight(), event.isLeftClick());
                getInventory().setItem(0, getItem());
                display.setBrightness(new Display.Brightness(newBrightness, display.getBrightness().getSkyLight()));
            }
        });

        addButton(9, new InventoryButton(items.SKY_LIGHT) {
            @Override
            public void onClick(InventoryClickEvent event) {
                int newBrightness = InventoryUtils.setNewItemBrightness(getItem(), display.getBrightness().getSkyLight(), event.isLeftClick());
                getInventory().setItem(9, getItem());
                display.setBrightness(new Display.Brightness(display.getBrightness().getBlockLight(), newBrightness));
            }
        });
        // ----------

        // ----[ SHADOW ]-----
        addButton(1, new InventoryButton(items.SHADOW_RADIUS) {
            @Override
            public void onClick(InventoryClickEvent event) {
                double newValue = InventoryUtils.changeDoubleValue(getItem(), display.getShadowRadius(), event.isShiftClick(), 0.0, event.isLeftClick());
                getInventory().setItem(1, getItem());
                display.setShadow((float) newValue, display.getShadowStrength());
            }
        });

        addButton(10, new InventoryButton(items.SHADOW_STRENGTH) {
            @Override
            public void onClick(InventoryClickEvent event) {
                double newValue = InventoryUtils.changeDoubleValue(getItem(), display.getShadowStrength(), event.isShiftClick(), 0.0, event.isLeftClick());
                getInventory().setItem(10, getItem());
                display.setShadow(display.getShadowRadius(), (float) newValue);
            }
        });
        // ----------

        // ----[ GLOW ]-----
        addButton(2, new InventoryButton(new ItemStack(items.GLOW_TOGGLE)) {
            @Override
            public void onClick(InventoryClickEvent event) {
                boolean newValue = !display.isGlowing();
                InventoryUtils.changeCurrentValue(getItem(), newValue);
                getInventory().setItem(2, getItem());
                display.setGlowing(newValue);
            }
        });

        addButton(11, new InventoryButton(new ItemStack(items.GLOW_COLOR_SELECTOR)) {
            @Override
            public void onClick(InventoryClickEvent event) {
                event.getWhoClicked().closeInventory();
                plugin.getInventoryManager().handleOpen((Player) event.getWhoClicked(), new ColorGUI(plugin, EditorGUI.this, display, false, display.getGlowColor(), color -> {
                    display.setGlowColor(color);
                    InventoryUtils.changeCurrentValue(getItem(), ChatColor.of(new Color(display.getGlowColor().asRGB())) + "Preview");
                    getInventory().setItem(11, getItem());
                }));
            }
        });
        // ----------

        // ----[ LOCATION ]-----
        addButton(18, new InventoryButton(items.TELEPORT) {
            @Override
            public void onClick(InventoryClickEvent event) {
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().teleport(display.getLocation());
            }
        });

        addButton(19, new InventoryButton(items.MOVE_HERE) {
            @Override
            public void onClick(InventoryClickEvent event) {
                event.getWhoClicked().closeInventory();
                display.setLocation(event.getWhoClicked().getEyeLocation());
            }
        });

        addButton(19, new InventoryButton(items.MOVE_HERE) {
            @Override
            public void onClick(InventoryClickEvent event) {
                event.getWhoClicked().closeInventory();
                display.setLocation(event.getWhoClicked().getLocation());
                Location loc = event.getWhoClicked().getEyeLocation();
                String location = BigDecimal.valueOf(loc.getX()).setScale(2, RoundingMode.HALF_UP).doubleValue() + ";" + BigDecimal.valueOf(loc.getY()).setScale(2, RoundingMode.HALF_UP).doubleValue() + ";" + BigDecimal.valueOf(loc.getZ()).setScale(2, RoundingMode.HALF_UP).doubleValue();
                InventoryUtils.changeCurrentValue(getItem(), location);
                getInventory().setItem(19, getItem());
            }
        });

        addButton(20, new InventoryButton(items.CENTER) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Location loc = display.center();
                String location = loc.getX() + ";" + loc.getX() + ";" + loc.getZ();
                InventoryUtils.changeCurrentValue(getItem(), location);
                getInventory().setItem(20, getItem());
            }
        });
        // ----------

        // ----[ OTHER ]-----
        addButton(4, new InventoryButton(items.BILLBOARD) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Display.Billboard newBillboard = InventoryUtils.changeEnumValue(getItem(), display.getBillboard());
                getInventory().setItem(4, getItem());
                display.setBillboard(newBillboard);
            }
        });
        // ----------

        // ----[ ACTIONS ]-----
        addButton(13, new InventoryButton(items.CURRENT_VALUE) {
            @Override
            public void onClick(InventoryClickEvent event) {
                ItemStack cursorItem = Objects.requireNonNull(event.getCursor()).clone();
                if (cursorItem.getType() == Material.AIR) return;

                switch (display.getType()) {
                    case BLOCK -> ((BlockDisplay) display).setBlock(cursorItem.getType().createBlockData());
                    case ITEM -> ((ItemDisplay) display).setMaterial(cursorItem.getType());
                }

                if (display.getType() != DisplayType.TEXT) {
                    getItem().setType(cursorItem.getType());
                    getItem().setAmount(1);

                    InventoryUtils.changeCurrentValue(getItem(), cursorItem.getType().name());

                    // A Runnable must be used, or it won't work.
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            getInventory().setItem(13, getItem());
                            event.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));
                        }
                    }.runTask(plugin);

                    setBlockData(items);
                }
            }
        });

        addButton(22, new InventoryButton(items.REMOVE) {
            @Override
            public void onClick(InventoryClickEvent event) {
                event.getWhoClicked().closeInventory();
                plugin.getDisplaysManager().removeDisplay(display.getName());
            }
        });
        // ----------

        // ----[ DISPLAY-SPECIFIC ]-----
        switch (display.getType()) {
            case BLOCK -> setBlockData(items);

            case ITEM -> addButton(8, new InventoryButton(items.ITEM_TRANSFORMATION) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    org.bukkit.entity.ItemDisplay.ItemDisplayTransform newTransform = InventoryUtils.changeEnumValue(getItem(), ((ItemDisplay) display).getItemTransformation());
                    getInventory().setItem(8, getItem());
                    ((ItemDisplay) display).setItemTransformation(newTransform);
                }
            });

            case TEXT -> {
                TextDisplay textDisplay = (TextDisplay) display;
                addButton(8, new InventoryButton(items.TEXT_ALIGNMENT) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        org.bukkit.entity.TextDisplay.TextAlignment newAlignment = InventoryUtils.changeEnumValue(getItem(), textDisplay.getAlignment());
                        getInventory().setItem(8, getItem());
                        textDisplay.setAlignment(newAlignment);
                    }
                });

                addButton(7, new InventoryButton(items.BACKGROUND_COLOR) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        event.getWhoClicked().closeInventory();
                        plugin.getInventoryManager().handleOpen((Player) event.getWhoClicked(), new ColorGUI(plugin, EditorGUI.this, display, true, textDisplay.getBackgroundColor(), color -> {
                            textDisplay.setBackgroundColor(color);
                            InventoryUtils.changeArmorColor(getItem(), textDisplay.getBackgroundColor());
                            InventoryUtils.changeCurrentValue(getItem(), ChatColor.of(new Color(textDisplay.getBackgroundColor().asRGB())) + "Preview");
                            getInventory().setItem(7, getItem());
                        }));
                    }
                });

                addButton(6, new InventoryButton(items.LINE_WIDTH) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        int newValue = (int) InventoryUtils.changeDoubleValue(getItem(), textDisplay.getLineWidth(), 10.0, 1.0, event.isShiftClick(), 0.0, null, event.isLeftClick());
                        getInventory().setItem(6, getItem());
                        textDisplay.setLineWidth(newValue);
                    }
                });

                addButton(17, new InventoryButton(items.TEXT_OPACITY) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        byte newValue = (byte) InventoryUtils.changeDoubleValue(getItem(), textDisplay.getTextOpacity(), 10.0, 1.0, event.isShiftClick(), -1.0, 127.0, event.isLeftClick());
                        getInventory().setItem(17, getItem());
                        textDisplay.setTextOpacity(newValue);
                    }
                });

                addButton(16, new InventoryButton(items.USE_DEFAULT_BACKGROUND) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        boolean newValue = InventoryUtils.changeBooleanValue(getItem(), textDisplay.getUseDefaultBackground());
                        getInventory().setItem(16, getItem());
                        textDisplay.setUseDefaultBackground(newValue);
                    }
                });

                addButton(15, new InventoryButton(items.SEE_THROUGH) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        boolean newValue = InventoryUtils.changeBooleanValue(getItem(), textDisplay.isSeeThrough());
                        getInventory().setItem(15, getItem());
                        textDisplay.setSeeThrough(newValue);
                    }
                });

                addButton(26, new InventoryButton(items.SHADOWED) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        boolean newValue = InventoryUtils.changeBooleanValue(getItem(), textDisplay.isShadowed());
                        getInventory().setItem(26, getItem());
                        textDisplay.setShadowed(newValue);
                    }
                });

                addButton(25, new InventoryButton(items.ANIMATION_TIME) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        int newValue = (int) InventoryUtils.changeDoubleValue(getItem(), textDisplay.getAnimationTime(), 10.0, 1.0, event.isShiftClick(), 1.0, null, event.isLeftClick());
                        getInventory().setItem(25, getItem());
                        textDisplay.setAnimationTime(newValue);
                    }
                });

                addButton(24, new InventoryButton(items.REFRESH_TIME) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        int newValue = (int) InventoryUtils.changeDoubleValue(getItem(), textDisplay.getRefreshTime(), 10.0, 1.0, event.isShiftClick(), 0.0, null, event.isLeftClick());
                        getInventory().setItem(24, getItem());
                        textDisplay.setRefreshTime(newValue);
                    }
                });
            }
        }
        // ----------

        super.decorate();
    }

    private void setBlockData(EditorItems items) {
        BlockDisplay blockDisplay = (BlockDisplay) display;
        String data = blockDisplay.getBlock().getAsString();
        if (data.contains("[")) {
            addButton(8, new InventoryButton(items.BLOCK_DATA) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    event.getWhoClicked().closeInventory();
                    plugin.getInventoryManager().handleOpen((Player) event.getWhoClicked(), new BlockDataGUI(plugin, EditorGUI.this, blockDisplay, blockData -> {
                        blockDisplay.setBlock(blockData);
                        InventoryUtils.changeCurrentValue(getItem(), blockData.toString());
                        getInventory().setItem(8, getItem());
                    }));
                }
            });
            InventoryUtils.changeCurrentValue(items.BLOCK_DATA, blockDisplay.getBlock().getAsString());

        } else {
            addButton(8, new InventoryButton(items.BLOCK_DATA) {
                @Override
                public void onClick(InventoryClickEvent event) {}
            });
            InventoryUtils.changeCurrentValue(items.BLOCK_DATA, "This block has no data");
        }
        getInventory().setItem(8, items.BLOCK_DATA);
    }
}