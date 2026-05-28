package me.lucaaa.advanceddisplays.managers;

import me.clip.placeholderapi.PlaceholderAPI;
import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.util.ComponentSerializer;
import me.lucaaa.advanceddisplays.paper.PaperMessageSender;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class MessagesManager {
    private final String prefix;
    private final boolean isPapiInstalled;
    private final boolean isPaper;
    
    public MessagesManager(AdvancedDisplays plugin, ConfigManager mainConfigManager) {
        this.prefix = mainConfigManager.getConfig().getString("prefix");
        this.isPapiInstalled = plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;

        this.isPaper = isPaper();
        if (!isPaper) {
            plugin.log(Level.WARNING, "This server is not running Paper or a fork of it. Chat components in actions will not have hover or click events!");
        }
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

    public void sendMessage(CommandSender sender, Component message) {
        if (isPaper) {
            PaperMessageSender.sendMessage(sender, message);
        } else {
            sender.sendMessage(ComponentSerializer.getLegacyString(message));
        }
    }

    public void sendActionbar(CommandSender sender, Component message) {
        if (isPaper) {
            PaperMessageSender.sendActionbar(sender, message);
        } else if (sender instanceof Player player) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ComponentSerializer.getLegacyString(message)));
        }
    }

    private boolean isPaper() {
        try {
            Class.forName("io.papermc.paper.event.player.AsyncChatEvent");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}