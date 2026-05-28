package me.lucaaa.advanceddisplays.paper;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class PaperMessageSender {
    public static void sendMessage(CommandSender sender, Component message) {
        sender.sendMessage(message);
    }

    public static void sendActionbar(CommandSender sender, Component message) {
        sender.sendActionBar(message);
    }
}