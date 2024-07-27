package me.lucaaa.advanceddisplays.common.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
        return ComponentSerializer.toString(parseMessage(player, null, text, false));
    }

    public static BaseComponent[] getTextComponent(String message, Player clickedPlayer, Player globalPlayer, boolean useGlobalPlaceholders) {
        return parseMessage(clickedPlayer, globalPlayer, message, useGlobalPlaceholders);
    }

    private static BaseComponent[] parseMessage(Player player, Player globalPlayer, String message, boolean useGlobalPlaceholders) {
        message = message.replace("%player%", player.getName());
        if (globalPlayer != null) message = message.replace("%global_player%", globalPlayer.getName());

        Player placeholderPlayer = (useGlobalPlaceholders) ? globalPlayer : player;
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            message = PlaceholderAPI.setPlaceholders(placeholderPlayer, message);
        }

        // From legacy and minimessage format to a component
        Component legacy = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
        // From component to Minimessage String. Replacing the "\" with nothing makes the minimessage formats work.
        String minimessage = MiniMessage.miniMessage().serialize(legacy).replace("\\", "");
        // From Minimessage String to Minimessage component
        Component component = MiniMessage.miniMessage().deserialize(minimessage);
        // From Minimessage component to legacy string.
        return BungeeComponentSerializer.get().serialize(component);
    }
}