package me.lucaaa.advanceddisplays.commands.subcommands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import org.bukkit.command.CommandSender;

public class ListSubCommand extends SubCommandsFormat{
    public ListSubCommand(AdvancedDisplays plugin) {
        super(plugin);
        this.name = "list";
        this.description = "Shows every display created by the plugin.";
        this.usage = "/ad list";
        this.minArguments = 0;
        this.executableByConsole = true;
        this.neededPermission = "ad.admin";
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        String list = String.join("&6,&e ", plugin.getDisplaysManager().getDisplays().keySet());
        sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&6List of displays: &e" + list, true));
    }
}