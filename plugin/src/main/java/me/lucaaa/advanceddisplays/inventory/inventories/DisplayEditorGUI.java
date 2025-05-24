package me.lucaaa.advanceddisplays.inventory.inventories;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.*;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.api.displays.enums.EditorItem;
import me.lucaaa.advanceddisplays.data.Utils;
import me.lucaaa.advanceddisplays.inventory.*;
import me.lucaaa.advanceddisplays.inventory.Button;
import me.lucaaa.advanceddisplays.inventory.items.DisplayEditorItems;
import me.lucaaa.advanceddisplays.inventory.items.Item;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.List;

public class DisplayEditorGUI extends InventoryMethods {
    private final BaseDisplay display;
    private final List<EditorItem> disabledSettings;
    private final DisplayEditorItems items;
    private final Map<Player, EditAction> editMap = new HashMap<>();

    public DisplayEditorGUI(AdvancedDisplays plugin, BaseDisplay display, List<EditorItem> disabledSettings) {
        super(plugin, Bukkit.createInventory(null, 27, Utils.getColoredText(("&6Editing " + display.getType().name() + " display: &e" + display.getName()))));
        this.display = display;
        this.disabledSettings = disabledSettings;
        this.items = new DisplayEditorItems(display);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);

        if (event.getClickedInventory() == getInventory()) {
            super.onClick(event);
        }
    }

    @Override
    public void decorate() {
        // ---[ BRIGHTNESS ]----
        addIfAllowed(EditorItem.BLOCK_LIGHT, 0, new Button.InventoryButton<>(items.BLOCK_LIGHT) {
            @Override
            public void onClick(InventoryClickEvent event) {
                int newBrightness = getItem().setNewItemBrightness(event.isLeftClick());
                getInventory().setItem(0, getItem().getStack());
                display.setBrightness(new Display.Brightness(newBrightness, display.getBrightness().getSkyLight()));
            }
        });

        addIfAllowed(EditorItem.SKY_LIGHT, 9, new Button.InventoryButton<>(items.SKY_LIGHT) {
            @Override
            public void onClick(InventoryClickEvent event) {
                int newBrightness = getItem().setNewItemBrightness(event.isLeftClick());
                getInventory().setItem(9, getItem().getStack());
                display.setBrightness(new Display.Brightness(display.getBrightness().getBlockLight(), newBrightness));
            }
        });
        // ----------

        // ----[ SHADOW ]-----
        addIfAllowed(EditorItem.SHADOW_RADIUS, 1, new Button.InventoryButton<>(items.SHADOW_RADIUS) {
            @Override
            public void onClick(InventoryClickEvent event) {
                double newValue = getItem().changeValue(event.isLeftClick(), event.isShiftClick(), 0.0);
                getInventory().setItem(1, getItem().getStack());
                display.setShadow((float) newValue, display.getShadowStrength());
            }
        });

        addIfAllowed(EditorItem.SHADOW_STRENGTH, 10, new Button.InventoryButton<>(items.SHADOW_STRENGTH) {
            @Override
            public void onClick(InventoryClickEvent event) {
                double newValue = getItem().changeValue(event.isLeftClick(), event.isShiftClick(), 0.0);
                getInventory().setItem(10, getItem().getStack());
                display.setShadow(display.getShadowRadius(), (float) newValue);
            }
        });
        // ----------

        // ----[ GLOW ]-----
        addIfAllowed(EditorItem.GLOW_TOGGLE, 2, new Button.InventoryButton<>(items.GLOW_TOGGLE) {
            @Override
            public void onClick(InventoryClickEvent event) {
                boolean newValue = getItem().changeValue();
                getInventory().setItem(2, getItem().getStack());
                display.setGlowing(newValue);
            }
        });

        addIfAllowed(EditorItem.GLOW_COLOR_SELECTOR, 11, new Button.InventoryButton<>(items.GLOW_COLOR_SELECTOR) {
            @Override
            public void onClick(InventoryClickEvent event) {
                ColorGUI inventory = new ColorGUI(plugin, DisplayEditorGUI.this, display, false, display.getGlowColorOverride(), color -> {
                    display.setGlowColorOverride(color);
                    getItem().setColor(display.getGlowColorOverride());
                    getInventory().setItem(11, getItem().getStack());
                });

                plugin.getInventoryManager().handleOpen((Player) event.getWhoClicked(), inventory);
            }
        });
        // ----------

        // ----[ LOCATION ]-----
        addIfAllowed(EditorItem.TELEPORT, 18, new Button.InventoryButton<>(items.TELEPORT) {
            @Override
            public void onClick(InventoryClickEvent event) {
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().teleport(display.getLocation());
            }
        });

        addIfAllowed(EditorItem.MOVE_HERE, 19, new Button.InventoryButton<>(items.MOVE_HERE) {
            @Override
            public void onClick(InventoryClickEvent event) {
                event.getWhoClicked().closeInventory();
                display.setLocation(event.getWhoClicked().getLocation());
                Location loc = event.getWhoClicked().getLocation();
                String location = BigDecimal.valueOf(loc.getX()).setScale(2, RoundingMode.HALF_UP).doubleValue() + ";" + BigDecimal.valueOf(loc.getY()).setScale(2, RoundingMode.HALF_UP).doubleValue() + ";" + BigDecimal.valueOf(loc.getZ()).setScale(2, RoundingMode.HALF_UP).doubleValue();
                getItem().setValue(location);
                getInventory().setItem(19, getItem().getStack());
            }
        });

        addIfAllowed(EditorItem.CENTER, 20, new Button.InventoryButton<>(items.CENTER) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Location loc = display.center();
                String location = loc.getX() + ";" + loc.getY() + ";" + loc.getZ();
                getItem().setValue(location);
                getInventory().setItem(20, getItem().getStack());
            }
        });
        // ----------

        // ----[ OTHER ]-----
        addIfAllowed(EditorItem.BILLBOARD, 4, new Button.InventoryButton<>(items.BILLBOARD) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Display.Billboard newBillboard = getItem().changeValue();
                getInventory().setItem(4, getItem().getStack());
                display.setBillboard(newBillboard);
            }
        });

        addIfAllowed(EditorItem.HITBOX_OVERRIDE, 12, new Button.InventoryButton<>(items.HITBOX_OVERRIDE) {
            @Override
            public void onClick(InventoryClickEvent event) {
                boolean newValue = getItem().changeValue();
                getInventory().setItem(12, getItem().getStack());
                display.setHitboxSize(newValue, display.getHitboxWidth(), display.getHitboxHeight());
            }
        });
        // ----------

        // ----[ ACTIONS ]-----
        addIfAllowed(EditorItem.CURRENT_VALUE, 13, new Button.InventoryButton<>(items.CURRENT_VALUE) {
            @Override
            public void onClick(InventoryClickEvent event) {
                event.getWhoClicked().closeInventory();
                plugin.getInventoryManager().getEditingPlayer((Player) event.getWhoClicked()).setChatEditing(true);

                if (display.getType() != DisplayType.TEXT) {
                    editMap.put((Player) event.getWhoClicked(), EditAction.CHANGE_MATERIAL);
                    if (display.getType() == DisplayType.BLOCK) {
                        event.getWhoClicked().sendMessage(plugin.getMessagesManager().getColoredMessage("&6Enter the name of a valid block."));
                        event.getWhoClicked().sendMessage(plugin.getMessagesManager().getColoredMessage("&6You can find a list of them here: &ehttps://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/BlockType.html"));
                    } else {
                        event.getWhoClicked().sendMessage(plugin.getMessagesManager().getColoredMessage("&6Enter the name of a valid material."));
                        event.getWhoClicked().sendMessage(plugin.getMessagesManager().getColoredMessage("&6You can find a list of them here: &ehttps://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html"));
                    }

                } else {
                    if (event.isLeftClick()) {
                        editMap.put((Player) event.getWhoClicked(), EditAction.REMOVE_TEXT);
                        event.getWhoClicked().sendMessage(plugin.getMessagesManager().getColoredMessage("&6Enter the name of the animation to remove. Valid animations: &e" + String.join("&6, &e", ((TextDisplay) display).getText().keySet())));
                        event.getWhoClicked().sendMessage(plugin.getMessagesManager().getColoredMessage("&6Type \"&ecancel&6\" to cancel the operation."));
                    } else {
                        editMap.put((Player) event.getWhoClicked(), EditAction.ADD_TEXT);
                        event.getWhoClicked().sendMessage(plugin.getMessagesManager().getColoredMessage("&6Enter the name of the animation to add along with its value. The name must not include spaces. You may use legacy color codes, minimessage format, placeholders and '\\n' to add a new line."));
                        event.getWhoClicked().sendMessage(plugin.getMessagesManager().getColoredMessage("&6Example: \"&emyAnimation3 <red>Hello %player%\\n<yellow>How are you?&6\""));
                        event.getWhoClicked().sendMessage(plugin.getMessagesManager().getColoredMessage("&6Type \"&ecancel&6\" to cancel the operation."));
                    }
                }
            }
        });

        addIfAllowed(EditorItem.REMOVE, 22, new Button.InventoryButton<>(items.REMOVE) {
            @Override
            public void onClick(InventoryClickEvent event) {
                event.getWhoClicked().closeInventory();
                plugin.getDisplaysManager().removeDisplay(display.getName());
            }
        });
        // ----------

        // ----[ DISPLAY-SPECIFIC ]-----
        switch (display.getType()) {
            case BLOCK -> setBlockData();

            case ITEM -> addIfAllowed(EditorItem.ITEM_TRANSFORMATION, 8, new Button.InventoryButton<>(items.ITEM_TRANSFORMATION) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    org.bukkit.entity.ItemDisplay.ItemDisplayTransform newTransform = getItem().changeValue();
                    getInventory().setItem(8, getItem().getStack());
                    ((ItemDisplay) display).setItemTransformation(newTransform);
                }
            });

            case TEXT -> {
                TextDisplay textDisplay = (TextDisplay) display;
                addIfAllowed(EditorItem.TEXT_ALIGNMENT, 8, new Button.InventoryButton<>(items.TEXT_ALIGNMENT) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        org.bukkit.entity.TextDisplay.TextAlignment newAlignment = getItem().changeValue();
                        getInventory().setItem(8, getItem().getStack());
                        textDisplay.setAlignment(newAlignment);
                    }
                });

                addIfAllowed(EditorItem.BACKGROUND_COLOR, 7, new Button.InventoryButton<>(items.BACKGROUND_COLOR) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        ColorGUI inventory = new ColorGUI(plugin, DisplayEditorGUI.this, display, true, textDisplay.getBackgroundColor(), color -> {
                            textDisplay.setBackgroundColor(color);
                            getItem().setColor(color);
                            getInventory().setItem(7, getItem().getStack());
                        });

                        plugin.getInventoryManager().handleOpen((Player) event.getWhoClicked(), inventory);
                    }
                });

                addIfAllowed(EditorItem.LINE_WIDTH, 6, new Button.InventoryButton<>(items.LINE_WIDTH) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        int newValue = (int) getItem().changeValue(event.isLeftClick(), event.isShiftClick(), 0.0);
                        getInventory().setItem(6, getItem().getStack());
                        textDisplay.setLineWidth(newValue);
                    }
                });

                addIfAllowed(EditorItem.TEXT_OPACITY, 17, new Button.InventoryButton<>(items.TEXT_OPACITY) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        byte newValue = (byte) getItem().changeValue(event.isLeftClick(), event.isShiftClick(), -1.0, 127.0);
                        getInventory().setItem(17, getItem().getStack());
                        textDisplay.setTextOpacity(newValue);
                    }
                });

                addIfAllowed(EditorItem.USE_DEFAULT_BACKGROUND, 16, new Button.InventoryButton<>(items.USE_DEFAULT_BACKGROUND) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        boolean newValue = getItem().changeValue();
                        getInventory().setItem(16, getItem().getStack());
                        textDisplay.setUseDefaultBackground(newValue);
                    }
                });

                addIfAllowed(EditorItem.SEE_THROUGH, 15, new Button.InventoryButton<>(items.SEE_THROUGH) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        boolean newValue = getItem().changeValue();
                        getInventory().setItem(15, getItem().getStack());
                        textDisplay.setSeeThrough(newValue);
                    }
                });

                addIfAllowed(EditorItem.SHADOWED, 26, new Button.InventoryButton<>(items.SHADOWED) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        boolean newValue = getItem().changeValue();
                        getInventory().setItem(26, getItem().getStack());
                        textDisplay.setShadowed(newValue);
                    }
                });

                addIfAllowed(EditorItem.ANIMATION_TIME, 25, new Button.InventoryButton<>(items.ANIMATION_TIME) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        int newValue = (int) getItem().changeValue(event.isLeftClick(), event.isShiftClick(), 1.0);
                        getInventory().setItem(25, getItem().getStack());
                        textDisplay.setAnimationTime(newValue);
                    }
                });

                addIfAllowed(EditorItem.REFRESH_TIME, 24, new Button.InventoryButton<>(items.REFRESH_TIME) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        int newValue = (int) getItem().changeValue(event.isLeftClick(), event.isShiftClick(), 0.0);
                        getInventory().setItem(24, getItem().getStack());
                        textDisplay.setRefreshTime(newValue);
                    }
                });
            }
        }
        // ----------

        super.decorate();
    }

    private void setBlockData() {
        BlockDisplay blockDisplay = (BlockDisplay) display;
        String data = blockDisplay.getBlock().getAsString();
        if (data.contains("[")) {
            addIfAllowed(EditorItem.BLOCK_DATA, 8, new Button.InventoryButton<>(items.BLOCK_DATA) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    InventoryMethods inventory = new BlockDataGUI(plugin, DisplayEditorGUI.this, blockDisplay, blockData -> {
                        blockDisplay.setBlock(blockData);
                        getItem().setValue(blockData.toString());
                        getInventory().setItem(8, getItem().getStack());
                    });

                    plugin.getInventoryManager().handleOpen((Player) event.getWhoClicked(), inventory);
                }
            });
            items.BLOCK_DATA.setValue(blockDisplay.getBlock().getAsString());

        } else {
            addIfAllowed(EditorItem.BLOCK_DATA, 8, new Button.InventoryButton<>(items.BLOCK_DATA) {
                @Override
                public void onClick(InventoryClickEvent event) {}
            });
            items.BLOCK_DATA.setValue("This block has no data");
        }
        getInventory().setItem(8, items.BLOCK_DATA.getStack());
    }

    @Override
    public void handleChatEdit(Player player, String input) {
        if (!input.equalsIgnoreCase("cancel")) {
            @SuppressWarnings("unchecked")
            Item<String> item = (Item<String>) getButton(13).getItem();

            switch (editMap.get(player)) {
                case REMOVE_TEXT -> {
                    if (((TextDisplay) display).removeText(input)) {
                        player.sendMessage(plugin.getMessagesManager().getColoredMessage("&aThe animation &e" + input + " &a has been removed. If it didn't exist, nothing will be changed."));
                    } else {
                        player.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe animation &b" + input + " &cdoes not exist!"));
                        return;
                    }

                    item.setValue(((TextDisplay) display).getText().size() + " text animation(s)");
                }

                case ADD_TEXT -> {
                    int firstSpace = input.indexOf(" ");

                    if (firstSpace == -1){
                        player.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe text you have entered is invalid. Remember that the format is &b<animation name (no spaces)> <animation text>"));
                        return;
                    }

                    String identifier = input.substring(0, firstSpace);
                    String joined = input.substring(firstSpace + 1).replace("\\n", "\n");
                    if (((TextDisplay) display).addText(identifier, joined)) {
                        player.sendMessage(plugin.getMessagesManager().getColoredMessage("&aThe animation &e" + identifier + " &a has been created and added after the last animation."));
                    } else {
                        player.sendMessage(plugin.getMessagesManager().getColoredMessage("&cAn animation with the name &b" + identifier + " &calready exists!"));
                        return;
                    }

                    item.setValue(((TextDisplay) display).getText().size() + " text animation(s)");
                }

                case CHANGE_MATERIAL -> {
                    Material material = Material.getMaterial(input.toUpperCase());

                    if (material == null) {
                        player.sendMessage(plugin.getMessagesManager().getColoredMessage("&b" + input + " &cis not a valid material!"));
                        return;
                    }

                    if (material == Material.AIR) {
                        player.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe material cannot be air!"));
                        return;
                    }

                    if (display.getType() == DisplayType.ITEM) {
                        ((ItemDisplay) display).setItem(new ItemStack(material));

                    } else {
                        try {
                            BlockData blockData = material.createBlockData();
                            ((BlockDisplay) display).setBlock(blockData);
                            setBlockData();

                        } catch (IllegalArgumentException | NullPointerException ignored) {
                            player.sendMessage(plugin.getMessagesManager().getColoredMessage("&b" + material.name() + " &cis not a valid block!"));
                            return;
                        }
                    }

                    item.getStack().setType(material);
                    item.getStack().setAmount(1);
                    item.setValue(material.name());
                }
            }
        }

        getInventory().setItem(13, getButton(13).getItem().getStack());
        editMap.remove(player);
        plugin.getInventoryManager().handleOpen(player, this);
    }

    private enum EditAction {
        ADD_TEXT,
        REMOVE_TEXT,
        CHANGE_MATERIAL
    }

    private void addIfAllowed(EditorItem requirement, int slot, Button.InventoryButton<?> button) {
        if (!disabledSettings.contains(requirement)) {
            addButton(slot, button);
            return;
        }

        ItemStack itemStack = button.getItem().getStack();
        ItemMeta meta = Objects.requireNonNull(itemStack.getItemMeta());
        List<String> lore = new ArrayList<>();
        for (String line : Objects.requireNonNull(meta.getLore())) {
            if (!line.startsWith(ChatColor.YELLOW + "")) continue;

            lore.add(line);
        }

        lore.remove(lore.size() - 1);
        lore.add(ChatColor.RED + "" + ChatColor.BOLD + "Setting disabled!");
        lore.add(ChatColor.GRAY + "You won't be able to change it");
        lore.add("");
        lore.add(ChatColor.BLUE + "Current value: " + ChatColor.GRAY + button.getItem().getValue());
        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        addButton(slot, new Button.InventoryButton<Item<?>>(button.getItem()) {
            @Override
            public void onClick(InventoryClickEvent event) {}
        });
    }
}