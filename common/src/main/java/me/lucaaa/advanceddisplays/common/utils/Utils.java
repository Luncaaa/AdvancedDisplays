package me.lucaaa.advanceddisplays.common.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import me.lucaaa.advanceddisplays.api.util.ComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Utils {
    public static String getColoredText(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String getColoredTextWithPlaceholders(Player player, String text) {
        text = text.replace("%player%", player.getName());

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }

        return text;
    }

    public static String getTextString(String message, Player clickedPlayer, Player globalPlayer, boolean useGlobalPlaceholders) {
        message = message.replace("%player%", clickedPlayer.getName());
        if (globalPlayer != null) message = message.replace("%global_player%", globalPlayer.getName());

        Player placeholderPlayer = (useGlobalPlaceholders) ? globalPlayer : clickedPlayer;
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            message = PlaceholderAPI.setPlaceholders(placeholderPlayer, message);
        }

        return LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build().serialize(ComponentSerializer.deserialize(message));
    }
}