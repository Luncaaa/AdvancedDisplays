package me.lucaaa.advanceddisplays.managers;

import org.bukkit.ChatColor;

public class MessagesManager {
    private final String prefix;
    
    public MessagesManager(ConfigManager mainConfigManager) {
        this.prefix = mainConfigManager.getConfig().getString("prefix");
    }
    
    public String getColoredMessage(String message) {
        String messageToSend = prefix + " " + message;

        return ChatColor.translateAlternateColorCodes('&', messageToSend);
    }
}