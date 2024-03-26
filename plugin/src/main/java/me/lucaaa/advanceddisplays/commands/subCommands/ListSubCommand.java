package me.lucaaa.advanceddisplays.commands.subCommands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.managers.MessagesManager;
import org.bukkit.command.CommandSender;

public class ListSubCommand extends SubCommandsFormat{
    public ListSubCommand() {
        this.name = "list";
        this.description = "Shows every display created by the plugin.";
        this.usage = "/ad list";
        this.minArguments = 0;
        this.executableByConsole = true;
        this.neededPermission = "ad.admin";
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        String list = String.join("&6,&e ", AdvancedDisplays.displaysManager.displays.keySet());
        sender.sendMessage(MessagesManager.getColoredMessage("&6List of displays: &e" + list, true));
    }
}
