package me.lucaaa.advanceddisplays.inventory.inventories;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.displays.BaseDisplay;
import me.lucaaa.advanceddisplays.api.displays.BlockDisplay;
import me.lucaaa.advanceddisplays.common.utils.Utils;
import me.lucaaa.advanceddisplays.inventory.Button;
import me.lucaaa.advanceddisplays.inventory.InventoryMethods;
import me.lucaaa.advanceddisplays.inventory.items.GlobalItems;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.function.Consumer;

public class BlockDataGUI extends InventoryMethods {
    private final EditorGUI previous;
    private final BaseDisplay display;
    private final Consumer<BlockData> onDone;
    private final Material material;
    private final Map<String, String> dataMap = new HashMap<>();
    private final Map<Player, String> editMap = new HashMap<>();

    public BlockDataGUI(AdvancedDisplays plugin, EditorGUI previousInventory, BlockDisplay display, Consumer<BlockData> onDone) {
        super(plugin, Bukkit.createInventory(null, 27, Utils.getColoredText("&6Editing block data of: &e" + display.getName())));
        this.previous = previousInventory;
        this.display = display;
        this.onDone = onDone;
        this.material = display.getBlock().getMaterial();

        BlockData blockData = display.getBlock();
        String fullData = blockData.getAsString().substring(blockData.getAsString().indexOf("[") + 1, blockData.getAsString().lastIndexOf("]"));
        for (String data : fullData.split(",")) {
            String[] dataPart = data.split("=");
            dataMap.put(dataPart[0], dataPart[1]);
        }
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
        int slot = 0;
        for (Map.Entry<String, String> entry : this.dataMap.entrySet()) {
            addButton(slot, new Button.InventoryButton(create(entry.getKey(), entry.getValue())) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    editMap.put((Player) event.getWhoClicked(), entry.getKey());
                    plugin.getInventoryManager().getEditingPlayer((Player) event.getWhoClicked()).setChatEditing(true);
                    event.getWhoClicked().closeInventory();
                    event.getWhoClicked().sendMessage(Utils.getColoredText("&6Enter the new value for the &e" + entry.getKey() + " &6data value. Type &ecancel &6to cancel the operation."));
                }
            });
            slot++;
        }

        addButton(18, new Button.InventoryButton(GlobalItems.CANCEL) {
            @Override
            public void onClick(InventoryClickEvent event) {
                onClose((Player) event.getWhoClicked());
            }
        });

        addButton(26, new Button.InventoryButton(GlobalItems.DONE) {
            @Override
            public void onClick(InventoryClickEvent event) {
                String blockData = "minecraft:" + material.name().toLowerCase() + "[";
                ArrayList<String> dataParts = new ArrayList<>();
                for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                    dataParts.add(entry.getKey() + "=" + entry.getValue());
                }
                blockData = blockData.concat(String.join(",", dataParts));
                onDone.accept(Bukkit.createBlockData(blockData + "]"));
                onClose((Player) event.getWhoClicked());
            }
        });

        super.decorate();
    }

    @Override
    public void handleChatEdit(Player player, String input) {
        if (!input.equalsIgnoreCase("cancel")) {
            dataMap.put(editMap.remove(player), input);
            decorate();
        }
        plugin.getInventoryManager().handleOpen(player, this, display);
    }

    @Override
    public void onClose(Player player) {
        if (editMap.containsKey(player)) return;
        player.closeInventory();
        // The task is run so that the InventoryCloseEvent is fully run before opening a new inventory.
        // Otherwise, the inventory will open but won't be registered as a plugin's GUI.
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getInventoryManager().handleOpen(player, previous, display);
            }
        }.runTask(plugin);
    }

    private ItemStack create(String title, String value) {
        ItemStack item = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

        meta.setDisplayName(ChatColor.GOLD + title);

        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.BLUE + "Current value: " + ChatColor.GRAY + value);

        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }
}