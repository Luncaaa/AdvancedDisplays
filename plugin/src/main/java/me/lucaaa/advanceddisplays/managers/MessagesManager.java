package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import org.bukkit.ChatColor;

public class MessagesManager {
    public static String getColoredMessage(String message, boolean addPrefix) {
        String messageToSend = message;
        if (addPrefix) messageToSend = AdvancedDisplays.mainConfig.getConfig().getString("prefix") + " " + messageToSend;

        return ChatColor.translateAlternateColorCodes('&', messageToSend);
    }
}