package me.lucaaa.advanceddisplays.common.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Utils {
    public static String getColoredTextWithPlaceholders(Player globalPlayer, String text, Player clickedPlayer) {
        String transformedText = text.replaceAll("%player%", globalPlayer.getName());
        if (clickedPlayer != null) transformedText = transformedText.replaceAll("%clicked_player%", clickedPlayer.getName());

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            transformedText = PlaceholderAPI.setPlaceholders(globalPlayer, transformedText);
        }

        MiniMessage mm = MiniMessage.miniMessage();
        Component c = mm.deserialize(transformedText);
        return ChatColor.translateAlternateColorCodes('&', JSONComponentSerializer.json().serialize(c)).replace("\\n", "\n");
    }

    public static String getColoredTextWithPlaceholders(Player player, String text) {
        return getColoredTextWithPlaceholders(player, text, null);
    }
}
