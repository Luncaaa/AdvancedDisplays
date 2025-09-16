package me.lucaaa.advanceddisplays.managers;

import me.clip.placeholderapi.PlaceholderAPI;
import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.util.ComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessagesManager {
    private final String prefix;
    private final boolean isPapiInstalled;
    
    public MessagesManager(AdvancedDisplays plugin, ConfigManager mainConfigManager) {
        this.prefix = mainConfigManager.getConfig().getString("prefix");
        this.isPapiInstalled = plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
    }
    
    public String getColoredMessage(String message) {
        String messageToSend = prefix + " " + message;

        return ChatColor.translateAlternateColorCodes('&', messageToSend);
    }

    public Component parseColorsAndPlaceholders(Player player, String text) {
        text = text.replace("%player%", player.getName());

        if (isPapiInstalled) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }

        return ComponentSerializer.deserialize(text);
    }
}