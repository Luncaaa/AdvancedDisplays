package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import org.bukkit.ChatColor;

public class MessagesManager {
    public static String getColoredMessage(String message, boolean addPrefix) {
        String messageToSend = message;
        if (addPrefix) messageToSend = AdvancedDisplays.mainConfig.getConfig().getString("prefix") + " " + messageToSend;

        return ChatColor.translateAlternateColorCodes('&', messageToSend);
    }

    // For PAPI.
    /*private static String replacePlaceholders(String message, HashMap<String, String> placeholders) {
        String newMessage = message;
        placeholders.put("%prefix%", AdvancedDisplays.mainConfig.getConfig().getString("prefix"));
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            newMessage = newMessage.replace(entry.getKey(), entry.getValue());
        }
        return newMessage;
    }*/
}