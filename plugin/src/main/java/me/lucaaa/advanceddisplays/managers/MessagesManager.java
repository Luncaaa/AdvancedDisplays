package me.lucaaa.advanceddisplays.managers;

import me.lucaaa.advanceddisplays.common.managers.ConfigManager;
import org.bukkit.ChatColor;

public class MessagesManager {
    private final ConfigManager mainConfig;
    
    public MessagesManager(ConfigManager mainConfigManager) {
        this.mainConfig = mainConfigManager;
    }
    
    public String getColoredMessage(String message, boolean addPrefix) {
        String messageToSend = message;
        if (addPrefix) messageToSend = mainConfig.getConfig().getString("prefix") + " " + messageToSend;

        return ChatColor.translateAlternateColorCodes('&', messageToSend);
    }
}