package me.lucaaa.advanceddisplays.inventory.inventories;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.*;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayType;
import me.lucaaa.advanceddisplays.api.displays.enums.EditorItem;
import me.lucaaa.advanceddisplays.data.Utils;
import me.lucaaa.advanceddisplays.displays.ADTextDisplay;
import me.lucaaa.advanceddisplays.inventory.*;
import me.lucaaa.advanceddisplays.inventory.Button;
import me.lucaaa.advanceddisplays.inventory.items.ColorItems;
import me.lucaaa.advanceddisplays.inventory.items.EditorItems;
import me.lucaaa.advanceddisplays.inventory.items.Item;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.*;
import java.util.List;

public class DisplayEditorGUI extends ADInventory {
    private final BaseDisplay display;
    // private final EditorItems items;
    private final Map<Player, EditAction> editMap = new HashMap<>();

    public DisplayEditorGUI(AdvancedDisplays plugin, BaseDisplay display, List<EditorItem> disabledSettings) {
        super(plugin, Bukkit.createInventory(null, 27, Utils.getColoredText(("&6Editing " + display.getType().name() + " display: &e" + display.getName()))), disabledSettings);
        this.display = display;
    }

    @Override
    public void decorate() {
        EditorItems items = new EditorItems(display);

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

        addIfAllowed(EditorItem.GLOW_COLOR_SELECTOR, 11, new Button.InventoryButton<>(items.GLOW_COLOR_OVERRIDE) {
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

        // ----[ OTHER ]-----
        addIfAllowed(EditorItem.BILLBOARD, 18, new Button.InventoryButton<>(items.BILLBOARD) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Display.Billboard newBillboard = getItem().changeValue();
                getInventory().setItem(18, getItem().getStack());
                display.setBillboard(newBillboard);
            }
        });

        addIfAllowed(EditorItem.HITBOX_OVERRIDE, 19, new Button.InventoryButton<>(items.HITBOX_OVERRIDE) {
            @Override
            public void onClick(InventoryClickEvent event) {
                boolean newValue = getItem().changeValue();
                getInventory().setItem(12, getItem().getStack());
                display.setHitboxSize(newValue, display.getHitboxWidth(), display.getHitboxHeight());
            }
        });

        addButton(20, new Button.InventoryButton<>(items.ENTITY_SETTINGS) {
            @Override
            public void onClick(InventoryClickEvent event) {
                EntityEditorGUI inventory = new EntityEditorGUI(plugin, display, disabledSettings, DisplayEditorGUI.this);
                plugin.getInventoryManager().handleOpen((Player) event.getWhoClicked(), inventory);
            }
        });
        // ----------

        // ----[ ACTIONS ]-----
        addIfAllowed(EditorItem.CURRENT_VALUE, 13, new Button.InventoryButton<>(items.CURRENT_VALUE) {
            @Override
            public void onClick(InventoryClickEvent event) {
                event.getWhoClicked().closeInventory();
                plugin.getPlayersManager().getPlayerData((Player) event.getWhoClicked()).setChatEditing(true);

                if (display.getType() != DisplayType.TEXT) {
                    editMap.put((Player) event.getWhoClicked(), EditAction.CHANGE_MATERIAL);
                    if (display.getType() == DisplayType.BLOCK) {
                        event.getWhoClicked().sendMessage(plugin.getMessagesManager().getColoredMessage("&6Enter the name of a valid block or \"cancel\" to keep the current one."));
                        event.getWhoClicked().sendMessage(plugin.getMessagesManager().getColoredMessage("&6You can find a list of them here: &ehttps://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/BlockType.html"));
                    } else {
                        event.getWhoClicked().sendMessage(plugin.getMessagesManager().getColoredMessage("&6Enter the name of a valid material or \"cancel\" to keep the current one."));
                        event.getWhoClicked().sendMessage(plugin.getMessagesManager().getColoredMessage("&6You can find a list of them here: &ehttps://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html"));
                    }

                } else {
                    if (event.isRightClick()) {
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
            case BLOCK -> setBlockData(items);

            case ITEM -> {
                ItemDisplay itemDisplay = (ItemDisplay) display;
                addIfAllowed(EditorItem.ITEM_TRANSFORMATION, 8, new Button.InventoryButton<>(items.ITEM_TRANSFORMATION) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        org.bukkit.entity.ItemDisplay.ItemDisplayTransform newTransform = getItem().changeValue();
                        getInventory().setItem(8, getItem().getStack());
                        itemDisplay.setItemTransformation(newTransform);
                    }
                });

                addIfAllowed(EditorItem.ENCHANTED, 7, new Button.InventoryButton<>(items.ENCHANTED) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        boolean isEnchanted = getItem().changeValue();
                        getInventory().setItem(7, getItem().getStack());
                        itemDisplay.setEnchanted(isEnchanted);
                        updateCurrentValue(itemDisplay.getItem().getItemMeta(), itemDisplay.getItem().getType().name());
                    }
                });

                addIfAllowed(EditorItem.CUSTOM_MODEL_DATA, metadataSlots.get(2), new Button.InventoryButton<>(items.CUSTOM_MODEL_DATA) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        double newValue = getItem().changeValue(event.isLeftClick(), event.isShiftClick(), 0.0);
                        getInventory().setItem(metadataSlots.get(2), getItem().getStack());

                        ItemStack item = itemDisplay.getItem().clone();
                        ItemMeta meta = item.getItemMeta();
                        if (meta != null) {
                            meta.setCustomModelData((int) newValue);
                            item.setItemMeta(meta);
                            itemDisplay.setItem(item);
                            getInventory().setItem(13, item);
                        }
                    }
                });

                addMetadataButtons(itemDisplay);
            }

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

    private void setBlockData(EditorItems items) {
        BlockDisplay blockDisplay = (BlockDisplay) display;
        String data = blockDisplay.getBlock().getAsString();
        if (data.contains("[")) {
            addIfAllowed(EditorItem.BLOCK_DATA, 8, new Button.InventoryButton<>(items.BLOCK_DATA) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    ADInventory inventory = new BlockDataGUI(plugin, DisplayEditorGUI.this, blockDisplay, blockData -> {
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
            Item.ClickableItem item = (Item.ClickableItem) getButton(13).getItem();

            switch (editMap.get(player)) {
                case REMOVE_TEXT -> {
                    ADTextDisplay textDisplay = (ADTextDisplay) display;
                    if (textDisplay.removeText(input)) {
                        player.sendMessage(plugin.getMessagesManager().getColoredMessage("&aThe animation &e" + input + " &a has been removed. If it didn't exist, nothing will be changed."));
                    } else {
                        player.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe animation &b" + input + " &cdoes not exist!"));
                        return;
                    }

                    List<String> lore = new ArrayList<>();
                    lore.add("Changes the text that is being displayed");
                    lore.add("");
                    lore.add("&7Use &cLEFT_CLICK &7to add an animation");
                    if (textDisplay.isNotEmpty()) {
                        lore.add("&7Use &cRIGHT_CLICK &7to remove an animation");
                    }
                    lore.add("");
                    item.setLore(lore);

                    item.setValue(textDisplay.getTextsNumber() + " text animation(s)");
                }

                case ADD_TEXT -> {
                    ADTextDisplay textDisplay = (ADTextDisplay) display;
                    int firstSpace = input.indexOf(" ");

                    if (firstSpace == -1){
                        player.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe text you have entered is invalid. Remember that the format is &b<animation name (no spaces)> <animation text>"));
                        return;
                    }

                    String identifier = input.substring(0, firstSpace);
                    String joined = input.substring(firstSpace + 1).replace("\\n", "\n");
                    if (textDisplay.addText(identifier, joined)) {
                        player.sendMessage(plugin.getMessagesManager().getColoredMessage("&aThe animation &e" + identifier + " &a has been created and added after the last animation."));
                    } else {
                        player.sendMessage(plugin.getMessagesManager().getColoredMessage("&cAn animation with the name &b" + identifier + " &calready exists!"));
                        return;
                    }

                    List<String> lore = new ArrayList<>();
                    lore.add("Changes the text that is being displayed");
                    lore.add("");
                    lore.add("&7Use &cLEFT_CLICK &7to add an animation");
                    if (textDisplay.isNotEmpty()) {
                        lore.add("&7Use &cRIGHT_CLICK &7to remove an animation");
                    }
                    lore.add("");
                    item.setLore(lore);

                    item.setValue(textDisplay.getText().size() + " text animation(s)");
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
                        ItemDisplay itemDisplay = (ItemDisplay) display;
                        // Save the enchanted state to a variable because setting a new item stack
                        // will make the item not be enchanted.
                        boolean enchanted = itemDisplay.isEnchanted();
                        itemDisplay.setItem(new ItemStack(material));
                        itemDisplay.setEnchanted(enchanted);

                    } else {
                        try {
                            ((BlockDisplay) display).setBlock(material.createBlockData());

                        } catch (IllegalArgumentException | NullPointerException ignored) {
                            player.sendMessage(plugin.getMessagesManager().getColoredMessage("&b" + material.name() + " &cis not a valid block!"));
                            return;
                        }
                    }
                }
            }

            clearButtons();
            decorate();
        }

        editMap.remove(player);
        plugin.getInventoryManager().handleOpen(player, this);
    }

    // The current item has the title and lore.
    // The new meta (argument parameter) has the "visible" properties, such as a potion's color.
    public void updateCurrentValue(ItemMeta meta, String value) {
        if (meta == null) return;

        Item.ClickableItem item = (Item.ClickableItem) getButton(13).getItem();
        item.setValue(value);
        item.applyMeta(meta);
        getInventory().setItem(13, item.getStack());
    }

    public void updateGlowToggleItem() {
        if (disabledSettings.contains(EditorItem.GLOW_TOGGLE)) return;

        Item.BooleanItem item = (Item.BooleanItem) getButton(2).getItem();
        item.setValue(display.isGlowing());
        getInventory().setItem(2, item.getStack());
    }

    private enum EditAction {
        ADD_TEXT,
        REMOVE_TEXT,
        CHANGE_MATERIAL
    }

    /**
     * Adds buttons to modify the item's metadata, such as a potion's color.
     */
    @SuppressWarnings("UnstableApiUsage")
    private void addMetadataButtons(ItemDisplay display) {
        ItemStack item = display.getItem().clone();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        int slot = metadataSlots.get(3);
        if (meta instanceof PotionMeta potion) {
            // Item type must be a potion (normal, splash or lingering) because meta is an instance of PotionMeta
            Color color = (potion.getColor() == null) ? Color.ORANGE : potion.getColor();
            ColorItems.ColorPreview preview = new ColorItems.ColorPreview(item.getType(), "Potion color", List.of("", "&7Use &cRIGHT_CLICK &7to reset"), color, ColorItems.ColorComponent.ALL, false);
            addIfAllowed(EditorItem.ITEM_META, slot, new Button.InventoryButton<>(preview) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (event.isRightClick()) {
                        getItem().setColor(Color.ORANGE);
                        getInventory().setItem(slot, getItem().getStack());
                        setPotionColor(display, null);
                        return;
                    }

                    ColorGUI inventory = new ColorGUI(plugin, DisplayEditorGUI.this, item.getType(), display, false, color, color -> {
                        getItem().setColor(color);
                        getInventory().setItem(slot, getItem().getStack());
                        setPotionColor(display, color);
                    });

                    plugin.getInventoryManager().handleOpen((Player) event.getWhoClicked(), inventory);
                }
            });

        } else if (meta instanceof ArmorMeta armor) {
            final TrimPattern[] pattern = {(armor.getTrim() == null) ? null : armor.getTrim().getPattern()};
            final TrimMaterial[] material = {(armor.getTrim() == null) ? null : armor.getTrim().getMaterial()};

            Item.RegistryItem patternItem = new Item.RegistryItem(getMatFromPattern(pattern[0]), "Armor trim pattern", List.of("Changes the armor's trim pattern"), pattern[0] == null ? TrimPattern.SENTRY : pattern[0], true, !armor.hasTrim());
            addIfAllowed(EditorItem.ITEM_META, slot, new Button.InventoryButton<>(patternItem) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    // If, for whatever reason, trim patterns are null, do not attempt to change the value.
                    if (TrimPattern.SENTRY == null) return;
                    pattern[0] = (TrimPattern) getItem().changeValue();
                    // Item is set in this method
                    setArmorTrim(display, pattern[0], material[0], slot, metadataSlots.get(4));
                }
            });

            Item.RegistryItem materialItem = new Item.RegistryItem(getMatFromTrimMat(material[0]), "Armor trim material", List.of("Changes the armor's trim material"), material[0] == null ? TrimMaterial.NETHERITE : material[0], true, !armor.hasTrim());
            addIfAllowed(EditorItem.ITEM_META, metadataSlots.get(4), new Button.InventoryButton<>(materialItem) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    // If, for whatever reason, trim materials are null, do not attempt to change the value.
                    if (TrimMaterial.DIAMOND == null) return;
                    material[0] = (TrimMaterial) getItem().changeValue();
                    // Item is set in this method
                    setArmorTrim(display, pattern[0], material[0], slot, metadataSlots.get(4));
                }
            });

            if (meta instanceof LeatherArmorMeta leatherArmor) {
                // Item type must be an armor part (helmet, boots...) because meta is an instance of LeatherArmorMeta
                ColorItems.ColorPreview preview = new ColorItems.ColorPreview(item.getType(), "Armor color", List.of("", "&7Use &cRIGHT_CLICK &7to reset"), leatherArmor.getColor(), ColorItems.ColorComponent.ALL, false);
                addIfAllowed(EditorItem.ITEM_META, metadataSlots.get(5), new Button.InventoryButton<>(preview) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        if (event.isRightClick()) {
                            getItem().setColor(plugin.getServer().getItemFactory().getDefaultLeatherColor());
                            getInventory().setItem(metadataSlots.get(5), getItem().getStack());
                            setArmorColor(display, null);
                            return;
                        }

                        ColorGUI inventory = new ColorGUI(plugin, DisplayEditorGUI.this, item.getType(), display, false, leatherArmor.getColor(), color -> {
                            getItem().setColor(color);
                            getInventory().setItem(metadataSlots.get(5), getItem().getStack());
                            setArmorColor(display, color);
                        });

                        plugin.getInventoryManager().handleOpen((Player) event.getWhoClicked(), inventory);
                    }
                });
            }

        } else if (meta instanceof BannerMeta banner) {
            Item.ClickableItem bannerItem = new Item.ClickableItem(item, "Banner patterns", List.of("Changes the banner's pattern"), null);
            addIfAllowed(EditorItem.ITEM_META, slot, new Button.InventoryButton<>(bannerItem) {
                private BannerMeta meta = banner;

                @Override
                public void onClick(InventoryClickEvent event) {
                    BannerEditorGUI inventory = new BannerEditorGUI(plugin, DisplayEditorGUI.this, item.getType(), this.meta, meta -> {
                        this.meta = meta;
                        ItemStack item = display.getItem().clone(); // Gets the potion type (normal, splash or lingering)
                        item.setItemMeta(meta);
                        display.setItem(item);
                        // Cloning the meta prevents the "applyMeta" method run in "updateCurrentValue" from changing anything in the metadata item.
                        getItem().applyMeta(meta.clone());
                        getInventory().setItem(slot, getItem().getStack());
                        updateCurrentValue(meta, item.getType().name());
                    });

                    plugin.getInventoryManager().handleOpen((Player) event.getWhoClicked(), inventory);
                }
            });

        } else if (meta instanceof CompassMeta compass) {
            String location = compass.hasLodestone() ? Utils.locToString(Objects.requireNonNull(compass.getLodestone())) : "No lodestone";
            Item.ClickableItem lodestoneLocation = new Item.ClickableItem(Material.LODESTONE, "Compass lodestone", List.of("Changes the compass' lodestone", "", "&7Use &cRIGHT_CLICK &7to reset"), location);
            addIfAllowed(EditorItem.ITEM_META, slot, new Button.InventoryButton<>(lodestoneLocation) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    boolean sameWorld = event.getWhoClicked().getWorld().equals(display.getLocation().getWorld());
                    if (event.isRightClick() || !sameWorld) {
                        getItem().setValue(sameWorld ? "No lodestone" : "Player and display world don't match!");
                        getInventory().setItem(slot, getItem().getStack());
                        setCompassLodestone(display, null);
                        return;
                    }

                    Location loc = event.getWhoClicked().getLocation();
                    getItem().setValue(Utils.locToString(loc));
                    getInventory().setItem(slot, getItem().getStack());
                    setCompassLodestone(display, loc);
                }
            });

        } else if (meta instanceof BundleMeta bundle) {
            Item.BooleanItem hasItems = new Item.BooleanItem(item, "Has items", List.of("Changes whether the bundle has items or not"), bundle.hasItems(), false);
            addIfAllowed(EditorItem.ITEM_META, slot, new Button.InventoryButton<>(hasItems) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    boolean hasItems = getItem().changeValue();
                    ItemStack item = display.getItem().clone();
                    ItemMeta meta = item.getItemMeta();
                    if (meta instanceof BundleMeta bundleMeta) {
                        if (hasItems) {
                            bundleMeta.addItem(new ItemStack(Material.DIAMOND, 64));
                        } else {
                            bundleMeta.setItems(List.of());
                        }

                        item.setItemMeta(meta);
                        display.setItem(item);
                        // Cloning the meta prevents the "applyMeta" method run in "updateCurrentValue" from changing anything in the metadata item.
                        getItem().applyMeta(meta.clone());
                        getInventory().setItem(slot, getItem().getStack());
                        updateCurrentValue(meta, item.getType().name());
                    }
                }
            });

        } else if (meta instanceof AxolotlBucketMeta bucketMeta) {
            Item.EnumItem bucketItem = new Item.EnumItem(item, "Axolotl variant", List.of("Changes the variant of the axolotl"), bucketMeta.hasVariant() ? bucketMeta.getVariant() : Axolotl.Variant.LUCY, false);
            addIfAllowed(EditorItem.ITEM_META, slot, new Button.InventoryButton<>(bucketItem) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    Axolotl.Variant newVariant = getItem().changeValue();
                    ItemStack item = display.getItem().clone();
                    ItemMeta meta = item.getItemMeta();
                    if (meta instanceof AxolotlBucketMeta axolotlBucketMeta) {
                        axolotlBucketMeta.setVariant(newVariant);
                        item.setItemMeta(meta);
                        display.setItem(item);
                        // Cloning the meta prevents the "applyMeta" method run in "updateCurrentValue" from changing anything in the metadata item.
                        getItem().applyMeta(meta.clone());
                        getInventory().setItem(slot, getItem().getStack());
                        updateCurrentValue(meta, item.getType().name());
                    }
                }
            });

        } else if (meta instanceof CrossbowMeta crossbowMeta) {
            CrossbowAmmo ammo;
            if (!crossbowMeta.getChargedProjectiles().isEmpty()) {
                ItemStack firstProjectile = crossbowMeta.getChargedProjectiles().get(0);
                ammo = (firstProjectile.getType() == Material.FIREWORK_ROCKET) ? CrossbowAmmo.ROCKET :CrossbowAmmo.ARROW;
            } else {
                ammo = CrossbowAmmo.NONE;
            }

            Item.EnumItem ammoItem = new Item.EnumItem(item, "Crossbow ammo", List.of("Changes the ammo loaded into the crossbow"), ammo, false);
            addIfAllowed(EditorItem.ITEM_META, slot, new Button.InventoryButton<>(ammoItem) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    CrossbowAmmo newAmmo = getItem().changeValue();
                    ItemStack item = display.getItem().clone();
                    ItemMeta meta = item.getItemMeta();
                    if (meta instanceof CrossbowMeta crossbowMeta) {
                        crossbowMeta.setChargedProjectiles(newAmmo.getItems());
                        item.setItemMeta(meta);
                        display.setItem(item);
                        // Cloning the meta prevents the "applyMeta" method run in "updateCurrentValue" from changing anything in the metadata item.
                        getItem().applyMeta(meta.clone());
                        getInventory().setItem(slot, getItem().getStack());
                        updateCurrentValue(meta, item.getType().name());
                    }
                }
            });
        }
    }

    private void setPotionColor(ItemDisplay display, Color color) {
        ItemStack item = display.getItem().clone(); // Gets the potion type (normal, splash or lingering)
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof PotionMeta potionMeta) {
            potionMeta.setColor(color);
            item.setItemMeta(meta);
            display.setItem(item);
            updateCurrentValue(meta, item.getType().name());
        }
    }

    private void setArmorColor(ItemDisplay display, Color color) {
        ItemStack item = display.getItem().clone(); // Gets the armor part (helmet, boots...)
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof LeatherArmorMeta leatherArmor) {
            leatherArmor.setColor(color);
            item.setItemMeta(meta);
            display.setItem(item);
            updateCurrentValue(meta, item.getType().name());
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    private void setArmorTrim(ItemDisplay display, TrimPattern pattern, TrimMaterial material, int patternSlot, int matSlot) {
        ItemStack item = display.getItem().clone(); // Gets the armor type (part and material)
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof ArmorMeta armorMeta) {
            if (pattern == null || material == null) {
                armorMeta.setTrim(null);
            } else {
                armorMeta.setTrim(new ArmorTrim(material, pattern));
            }

            item.setItemMeta(meta);
            display.setItem(item);
            updateCurrentValue(meta, item.getType().name());
        }

        Item.RegistryItem patternItem = (Item.RegistryItem) getButton(patternSlot).getItem();
        patternItem.getStack().setType(getMatFromPattern(pattern));
        getInventory().setItem(patternSlot, patternItem.getStack());

        Item.RegistryItem materialItem = (Item.RegistryItem) getButton(matSlot).getItem();
        materialItem.getStack().setType(getMatFromTrimMat(material));
        getInventory().setItem(matSlot, materialItem.getStack());
    }

    private void setCompassLodestone(ItemDisplay display, Location lodestone) {
        ItemStack item = display.getItem().clone();
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof CompassMeta compass) {
            compass.setLodestone(lodestone);
            compass.setLodestoneTracked(lodestone != null);
            item.setItemMeta(meta);
            display.setItem(item);
            updateCurrentValue(meta, item.getType().name());
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    private Material getMatFromPattern(TrimPattern pattern) {
        if (pattern == null) return Material.BARRIER;

        try {
            return Material.valueOf(pattern.getKey().getKey().toUpperCase() + "_ARMOR_TRIM_SMITHING_TEMPLATE");
        } catch (IllegalArgumentException e) {
            return Material.BARRIER;
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    private Material getMatFromTrimMat(TrimMaterial mat) {
        if (mat == null) return Material.BARRIER;

        return switch (mat.getKey().getKey().toLowerCase()) {
            case "amethyst" -> Material.AMETHYST_SHARD;
            case "copper" -> Material.COPPER_INGOT;
            case "diamond" -> Material.DIAMOND;
            case "emerald" -> Material.EMERALD;
            case "gold" -> Material.GOLD_INGOT;
            case "iron" -> Material.IRON_INGOT;
            case "lapis" -> Material.LAPIS_LAZULI;
            case "netherite" -> Material.NETHERITE_INGOT;
            case "quartz" -> Material.QUARTZ;
            case "redstone" -> Material.REDSTONE;
            case "resin" -> Material.getMaterial("RESIN_BRICK"); // Not available in 1.19.4
            default -> Material.BARRIER;
        };
    }

    private enum CrossbowAmmo {
        NONE(null),
        ARROW(new ItemStack(Material.ARROW)),
        ROCKET(new ItemStack(Material.FIREWORK_ROCKET));

        private final List<ItemStack> items;

        CrossbowAmmo(ItemStack item) {
            this.items = (item == null) ? List.of() : List.of(item);
        }

        public List<ItemStack> getItems() {
            return items;
        }
    }
}