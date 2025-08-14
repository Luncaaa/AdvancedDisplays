package me.lucaaa.advanceddisplays.events;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.ADAPIImplementation;
import me.lucaaa.advanceddisplays.data.AttachedDisplay;
import me.lucaaa.advanceddisplays.data.PlayerData;
import me.lucaaa.advanceddisplays.displays.ADTextDisplay;
import me.lucaaa.advanceddisplays.managers.ConfigManager;
import me.lucaaa.advanceddisplays.managers.DisplaysManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Objects;

@SuppressWarnings("unused")
public class PlayerEventsListener implements Listener {
    private final AdvancedDisplays plugin;
    private final HashMap<Player, Long> pastInteractions = new HashMap<>();

    public PlayerEventsListener(AdvancedDisplays plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        ConfigManager savedConfig = plugin.getSavesConfig();
        YamlConfiguration config = savedConfig.getConfig();
        if (!config.contains("saved")) config.createSection("saved");
        ConfigurationSection saved = Objects.requireNonNull(config.getConfigurationSection("saved"));
        if (saved.contains(player.getName())) {
            player.getInventory().clear();
            ConfigurationSection playerSection = Objects.requireNonNull(saved.getConfigurationSection(player.getName()));
            for (String key : playerSection.getKeys(false)) {
                player.getInventory().setItem(Integer.parseInt(key), playerSection.getItemStack(key));
            }
            saved.set(player.getName(), null);
            plugin.getSavesConfig().save();
        }

        plugin.getPacketsManager().add(player);
        plugin.getPlayersManager().addPlayer(player);
        plugin.getDisplaysManager().spawnDisplays(player);
        for (ADAPIImplementation implementation : plugin.getApiDisplays().getApiMap().values()) {
            implementation.getDisplaysManager().spawnDisplays(player);
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        plugin.getDisplaysManager().spawnDisplays(player);
        for (ADAPIImplementation implementation : plugin.getApiDisplays().getApiMap().values()) {
            implementation.getDisplaysManager().spawnDisplays(player);
        }
        plugin.getPlayersManager().getPlayerData(player).stopRunnables();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayersManager().getPlayerData(player);

        plugin.getPacketsManager().remove(player);
        if (playerData.isEditing()) {
            playerData.finishEditing();
        }
        plugin.getDisplaysManager().removeAttachingDisplay(player);
        plugin.getInventoryManager().onQuit(player);
        plugin.getPlayersManager().removePlayer(player);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!plugin.getPlayersManager().getPlayerData(event.getPlayer()).isChatEditing()) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getInventoryManager().handleChatEdit(event.getPlayer(), event.getMessage());
            }
        }.runTask(plugin);
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayersManager().getPlayerData(player);
        Action action = event.getAction();

        DisplaysManager manager = plugin.getDisplaysManager();
        if (manager.isPlayerAttaching(player) && action == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            AttachedDisplay display = manager.getAttachingDisplay(player);
            ADTextDisplay newDisplay = manager.createAttachedDisplay(event, display);
            if (newDisplay == null) {
                player.sendMessage(plugin.getMessagesManager().getColoredMessage("&cA display with the name &b" + display.name() + " &calready exists!"));
            } else {
                player.sendMessage(plugin.getMessagesManager().getColoredMessage("&aThe display &e" + display.name() + " &ahas been successfully created."));
            }
        }

        if (!playerData.isEditing()) return;

        // Because the event is fired twice, the current time is stored in a map along with the player that interacted with the display.
        // When the event is called again, the current time and the one stored in the map are compared. If less than or 20ms have passed, ignore this event.
        if (action == Action.RIGHT_CLICK_AIR) {
            if (pastInteractions.containsKey(player)) {
                long now = System.currentTimeMillis();
                if (now - pastInteractions.remove(player) <= 500) {
                    return;
                }
            } else {
                pastInteractions.put(player, System.currentTimeMillis());
            }
        }

        event.setCancelled(true);
        playerData.handleClick(event);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (!plugin.getPlayersManager().getPlayerData(event.getPlayer()).isEditing()) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!plugin.getPlayersManager().getPlayerData(player).isEditing()) return;

        event.setCancelled(true);
    }
}