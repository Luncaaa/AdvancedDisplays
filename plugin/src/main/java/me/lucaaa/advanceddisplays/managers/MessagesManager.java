package me.lucaaa.advanceddisplays.managers;

import org.bukkit.ChatColor;

public class MessagesManager {
    private final String prefix;
    
    public MessagesManager(ConfigManager mainConfigManager) {
        this.prefix = mainConfigManager.getConfig().getString("prefix");
    }
    
    public String getColoredMessage(String message, boolean addPrefix) {
        String messageToSend = message;
        if (addPrefix) messageToSend =  prefix + " " + messageToSend;

        return ChatColor.translateAlternateColorCodes('&', messageToSend);
    }
}