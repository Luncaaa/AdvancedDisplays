package me.lucaaa.advanceddisplays.common.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Utils {
    public static String getColoredText(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String getColoredTextWithPlaceholders(Player player, String text) {
        String transformedText = text.replace("%player%", player.getName());

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            transformedText = PlaceholderAPI.setPlaceholders(player, transformedText);
        }

        Component c = MiniMessage.miniMessage().deserialize(transformedText);
        return ChatColor.translateAlternateColorCodes('&', JSONComponentSerializer.json().serialize(c)).replace("\\n", "\n");
    }

    public static BaseComponent[] getTextComponent(String message, Player clickedPlayer, Player actionPlayer, boolean useGlobalPlaceholders) {
        String transformedText = message.replace("%player%", clickedPlayer.getName()).replace("%global_player%", actionPlayer.getName());

        Player placeholderPlayer = (useGlobalPlaceholders) ? actionPlayer : clickedPlayer;
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            transformedText = PlaceholderAPI.setPlaceholders(placeholderPlayer, transformedText);
        }

        Component c = MiniMessage.miniMessage().deserialize(transformedText);
        String json = ChatColor.translateAlternateColorCodes('&', JSONComponentSerializer.json().serialize(c)).replace("\\n", "\n");
        return ComponentSerializer.parse(json);
    }
}