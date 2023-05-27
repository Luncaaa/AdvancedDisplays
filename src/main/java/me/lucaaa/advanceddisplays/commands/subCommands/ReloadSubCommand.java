package me.lucaaa.advanceddisplays.commands.subCommands;

import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.managers.MessagesManager;
import org.bukkit.command.CommandSender;

public class ReloadSubCommand extends SubCommandsFormat {
    public ReloadSubCommand() {
        this.name = "reload";
        this.description = "Reloads the plugin's configuration files.";
        this.usage = "/ad reload";
        this.minArguments = 0;
        this.executableByConsole = true;
        this.neededPermission = "ad.reload";
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        AdvancedDisplays.reloadConfigs();
        sender.sendMessage(MessagesManager.getColoredMessage("&aThe configuration file has been reloaded successfully.", true));
    }
}